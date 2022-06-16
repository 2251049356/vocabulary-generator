package com.wansy.vocabularygenerator.strategy.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.wansy.vocabularygenerator.VocabularyItem;
import com.wansy.vocabularygenerator.strategy.VocabularyQueryStrategy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * 有道实现
 *
 * @author ljs
 * @date 2022/1/20 11:23
 */
@Service("youdaoImpl")
public class YouDaoVocabularyQueryStrategyImpl implements VocabularyQueryStrategy {
    private final RestTemplate restTemplate = new RestTemplate();
    private final JSONPath ukPronouncePath = JSONPath.compile("$.ec.word[0].ukphone");
    private final JSONPath usPronouncePath = JSONPath.compile("$.ec.word[0].usphone");
    private final JSONPath translatePath = JSONPath.compile("$.ec.word[0].trs[*].tr[*].l.i[*]");

    @Override
    public VocabularyItem query(String word) {
        ResponseEntity<String> response;
        synchronized (this) {
            response = restTemplate.getForEntity("http://dict.youdao.com/jsonapi?jsonversion=2&client=mobile&q=" + word + "&network=wifi&dicts=%7B%22count%22%3A99%2C%22dicts%22%3A%5B%5B%22ec%22%5D%5D%7D&xmlVersion=5.1", String.class);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        JSONObject vocabularyInfo = JSONObject.parseObject(response.getBody());
        VocabularyItem result = new VocabularyItem();
        result.setName(word);
        result.setUkPronounce((String) ukPronouncePath.eval(vocabularyInfo));
        result.setUsPronounce((String) usPronouncePath.eval(vocabularyInfo));
        result.setTranslate(String.join("<br/>", ((List<String>) translatePath.eval(vocabularyInfo))));
        return result;
    }

    @Override
    public String getStrategyName() {
        return "有道";
    }
}
