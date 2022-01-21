package com.wansy.vocabularygenerator;

import java.util.List;

/**
 * @author ljs
 * @date 2022/1/20 17:05
 */
public class VocabularyDataModel {
    private List<VocabularyItem> vocabularyItems;

    public VocabularyDataModel(List<VocabularyItem> vocabularyItems) {
        this.vocabularyItems = vocabularyItems;
    }

    public List<VocabularyItem> getVocabularyItems() {
        return vocabularyItems;
    }

    public void setVocabularyItems(List<VocabularyItem> vocabularyItems) {
        this.vocabularyItems = vocabularyItems;
    }
}
