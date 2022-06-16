package com.wansy.vocabularygenerator.strategy;

import com.wansy.vocabularygenerator.VocabularyItem;

/**
 * 词汇查询策略
 *
 * @author ljs
 * @date 2022/1/20 11:21
 */
public interface VocabularyQueryStrategy {
    /**
     * 查询
     *
     * @param word
     * @return
     */
    VocabularyItem query(String word);

    /**
     * 获取策略名
     * @return
     */
    String getStrategyName();
}
