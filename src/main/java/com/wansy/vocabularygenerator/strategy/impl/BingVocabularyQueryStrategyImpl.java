package com.wansy.vocabularygenerator.strategy.impl;

import com.wansy.vocabularygenerator.VocabularyItem;
import com.wansy.vocabularygenerator.strategy.VocabularyQueryStrategy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

/**
 * 必应实现
 *
 * @author ljs
 * @date 2022/1/20 11:23
 */
@Service("bingImpl")
public class BingVocabularyQueryStrategyImpl implements VocabularyQueryStrategy {
    private final RestTemplate restTemplate = new RestTemplate();
    private final BlockingQueue<VocabularyItem> queue = new ArrayBlockingQueue<>(100);

    @Override
    public VocabularyItem query(String word) {
        try {
            ResponseEntity<String> stringResponseEntity;
            synchronized (this) {
                stringResponseEntity = restTemplate.postForEntity("https://cn.bing.com/dict/clientsearch?mkt=zh-CN&setLang=zh&form=BDVEHC&ClientVer=BDDTV3.5.1.4320&q=" + word, null, String.class);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            HTMLEditorKit.Parser parserDelegator = new ParserDelegator();
            VocabularyItemCallback callback = new VocabularyItemCallback();
            parserDelegator.parse(new InputStreamReader(new ByteArrayInputStream(stringResponseEntity.getBody().getBytes(StandardCharsets.UTF_8))), callback, true);
            callback.flush();
            VocabularyItem item = queue.take();
            item.setName(word);
            return item;
        } catch (IOException | InterruptedException | BadLocationException e) {
            throw new RuntimeException(e);
        }
    }

    private class VocabularyItemCallback extends HTMLEditorKit.ParserCallback {
        /**
         * 发音上下文 true美式，false英式，null没有发音
         */
        private Boolean pronounceContext;
        private boolean translateContext = true;
        private Consumer<String> textConsumer;
        private final VocabularyItem vocabularyItem = new VocabularyItem();

        @Override
        public void handleText(char[] data, int pos) {
            if (textConsumer != null) textConsumer.accept(new String(data));
        }

        @Override
        public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {
            textConsumer = null;
            // 处理音标
            if (HTML.Tag.DIV.equals(t) && a.containsAttribute(HTML.Attribute.CLASS, "client_def_hd_pn")) {
                textConsumer = d -> {
                    pronounceContext = d.contains("美");
                    String pronounce = d.indexOf('[') == -1 ? "" : d.substring(d.indexOf('[') + 1, d.indexOf(']'));
                    if (pronounceContext) vocabularyItem.setUsPronounce(pronounce);
                    else vocabularyItem.setUkPronounce(pronounce);
                };
            }
            // 处理音标
            else if (pronounceContext != null && HTML.Tag.DIV.equals(t) && a.containsAttribute(HTML.Attribute.CLASS, "client_aud_o")) {
                Enumeration<?> names = a.getAttributeNames();
                while (names.hasMoreElements()) {
                    Object name = names.nextElement();
                    if ("onclick".equals(name.toString())) {
                        String value = (String) a.getAttribute(name);
                        int start = value.indexOf("'");
                        String audio = value.substring(start + 1, value.indexOf("'", start + 1));
                        if (pronounceContext) vocabularyItem.setUsSpeak(audio);
                        else vocabularyItem.setUkSpeak(audio);
                        pronounceContext = null;
                    }
                }
            }
            // 处理词性
            else if (translateContext && HTML.Tag.SPAN.equals(t) && a.containsAttribute(HTML.Attribute.CLASS, "client_def_title")) {
                textConsumer = d -> {
                    String translate = vocabularyItem.getTranslate();
                    if (translate == null) translate = "";
                    else translate += "<br/>";
                    translate += d;
                    vocabularyItem.setTranslate(translate);
                };
            }
            // 处理释义
            else if (translateContext && HTML.Tag.SPAN.equals(t) && a.containsAttribute(HTML.Attribute.CLASS, "client_def_list_word_bar")) {
                textConsumer = d -> {
                    String translate = vocabularyItem.getTranslate();
                    translate += d;
                    vocabularyItem.setTranslate(translate);
                };
            }
            // 处理释义
            else if (translateContext && HTML.Tag.SPAN.equals(t) && a.containsAttribute(HTML.Attribute.CLASS, "client_def_title_web")) {
                translateContext = false;
            }
        }

        @Override
        public void flush() throws BadLocationException {
            queue.add(vocabularyItem);
        }
    }
}
