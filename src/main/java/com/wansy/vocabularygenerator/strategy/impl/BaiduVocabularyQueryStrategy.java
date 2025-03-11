package com.wansy.vocabularygenerator.strategy.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.wansy.vocabularygenerator.VocabularyItem;
import com.wansy.vocabularygenerator.strategy.VocabularyQueryStrategy;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ljs
 * @date 2025/3/10 13:56
 */
@Service("baiduImpl")
public class BaiduVocabularyQueryStrategy implements VocabularyQueryStrategy {
    private final WebClient webClient;

    public BaiduVocabularyQueryStrategy(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    @Override
    public VocabularyItem query(String word) {
        Map<String, Object> body = new HashMap<>();
        body.put("corpusIds", Collections.emptyList());
        body.put("domain", "common");
        body.put("from", "en");
        body.put("milliTimestamp", System.currentTimeMillis());
        body.put("needPhonetic", false);
        body.put("query", word);
        body.put("reference", "");
        body.put("to", "zh");
        Flux<String> flux = webClient.post().uri("https://fanyi.baidu.com/ait/text/translate").bodyValue(body).retrieve().bodyToFlux(String.class);
        BaiduSymbolDto symbolDto = flux.collectList().block().stream().filter(d -> d.contains("GetDictSucceed")).findFirst().flatMap(d -> ((JSONArray) JSONPath.read(d, "$.data.dictResult.simple_means.symbols")).stream().findFirst()).map(d -> ((JSONObject) d).toJavaObject(BaiduSymbolDto.class)).orElseGet(BaiduSymbolDto::new);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        VocabularyItem item = new VocabularyItem();
        item.setName(word);
        String speakTmpl = "https://fanyi.baidu.com/gettts?lan=%s&text=" + URLEncoder.encode(word) + "&spd=3";
        item.setUkSpeak(item.new Speak(String.format(speakTmpl, "uk"), true));
        item.setUsSpeak(item.new Speak(String.format(speakTmpl, "en"), false));
        item.setTranslate(symbolDto.getParts().stream().map(d1 -> d1.getPart() + " " + String.join("，", d1.getMeans())).collect(Collectors.joining("<br/>")));
        item.setUkPronounce(symbolDto.getPh_en());
        item.setUsPronounce(symbolDto.getPh_am());
        return item;
    }

    @Override
    public String getStrategyName() {
        return "百度";
    }

}