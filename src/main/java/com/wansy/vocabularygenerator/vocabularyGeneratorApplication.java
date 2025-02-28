package com.wansy.vocabularygenerator;

import com.wansy.vocabularygenerator.strategy.VocabularyQueryStrategy;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.util.Pair;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
public class vocabularyGeneratorApplication {
    @Autowired
    private List<VocabularyQueryStrategy> vocabularyQueryStrategies;

    private static final Logger log = LoggerFactory.getLogger(vocabularyGeneratorApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(vocabularyGeneratorApplication.class, args);
    }

    private String audioDirName = "audio";

    @Bean
    CommandLineRunner commandLineRunner(@Value("${force-write:false}") boolean forceWrite) {

        return (args) -> {
            String baseDir = System.getProperty("user.dir");
            Path outFilePath = Paths.get(baseDir + "/vocabulary.html");
            if (!forceWrite && Files.exists(outFilePath)) {
                System.out.println("输出文件" + outFilePath.getFileName() + "已存在");
                System.exit(1);
            }
            try (Writer writer = Files.newBufferedWriter(outFilePath);
                 Writer failWriter = Files.newBufferedWriter(Paths.get(baseDir + "/word_fail.txt"))) {
                // 选择策略
                System.out.println("请选择词典（输入编号即可）：");
                for (int i = 0; i < vocabularyQueryStrategies.size(); i++) {
                    System.out.println((i + 1) + " " + vocabularyQueryStrategies.get(i).getStrategyName());
                }
                String idxStr = new BufferedReader(new InputStreamReader(System.in)).readLine();
                int idx = 0;
                if (idxStr == null || !idxStr.matches("\\d+") || (idx = Integer.parseInt(idxStr)) < 1 || idx > vocabularyQueryStrategies.size()) {
                    System.out.println("编号超出范围");
                    System.exit(1);
                }
                VocabularyQueryStrategy vocabularyQueryStrategy = vocabularyQueryStrategies.get(idx - 1);
                // 离线音频
                System.out.println("是否需要离线音频（词典的音频链接有有效期）y/n:");
                boolean offlineAudio = "y".equals(new BufferedReader(new InputStreamReader(System.in)).readLine());
                String audioDir = baseDir + "/" + audioDirName + "/";
                File audioDirFile = new File(audioDir);
                if (!audioDirFile.exists()) {
                    audioDirFile.mkdir();
                }
                // 获取单词
                Path wordsFilePath = Paths.get(baseDir + "/words.txt");
                // 获取词条
                List<Pair<String, String>> fails = new ArrayList<>();
                List<VocabularyItem> vocabularyItems = Files.readAllLines(wordsFilePath).stream().flatMap(d -> Stream.of(d.split("\\s+")))
                        .filter(d -> !"".equals(d)).map(d -> {
                            try {
                                VocabularyItem item = vocabularyQueryStrategy.query(d);
                                if (offlineAudio) {
                                    downVideo(audioDir, d, item, true);
                                    downVideo(audioDir, d, item, false);
                                }
                                return item;
                            } catch (Exception e) {
                                log.debug(d, e);
                                // 节约查词成本
                                fails.add(Pair.of(d, Optional.ofNullable(e.getMessage()).orElse("null")));
                            }
                            return null;
                        }).filter(Objects::nonNull).collect(Collectors.toList());
                // 生成html
                Configuration configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
                configuration.setDirectoryForTemplateLoading(new File(this.getClass().getResource("/template").getPath()));
                Template template = configuration.getTemplate("vocabulary.ftl");
                template.process(new VocabularyDataModel(vocabularyItems), writer);
                System.out.println("词汇表生成器执行完成。执行报告如下：");
                System.out.println("词汇表文件vocabulary.html生成成功");
                if (!CollectionUtils.isEmpty(fails)) {
                    fails.forEach(d -> {
                        try {
                            failWriter.write(String.format("%s\t失败原因：%s\n", d.getFirst(), d.getSecond()));
                        } catch (IOException e) {
                            log.debug("exception", e);
                        }
                    });
                    failWriter.flush();
                    System.out.println("部分单词失败，请查阅word_fail.txt");
                }
                System.exit(0);
            }
        };
    }

    /**
     * 下载音频
     *
     * @param audioDir
     * @param word
     * @param item
     * @throws IOException
     * @throws InterruptedException
     */
    private void downVideo(String audioDir, String word, VocabularyItem item, boolean usOrUK) throws IOException, InterruptedException {
        String urlStr = usOrUK ? item.getUsSpeak() : item.getUkSpeak();
        if (!StringUtils.isEmpty(urlStr)) {
            String audioName = word + "(" + (usOrUK ? "US" : "UK") + ").mp3";
            String dir = audioDirName + "/" + audioName;
            URL url = new URL(urlStr);
            if (usOrUK)
                item.setUsSpeak(dir);
            else item.setUkSpeak(dir);
            try (InputStream in = url.openStream(); OutputStream out = new FileOutputStream(audioDir + audioName)) {
                IOUtils.copy(in, out);
            }
            Thread.sleep(100);
        }
    }

}
