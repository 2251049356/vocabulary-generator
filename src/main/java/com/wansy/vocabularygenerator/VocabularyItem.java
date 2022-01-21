package com.wansy.vocabularygenerator;

/**
 * 词汇条目
 *
 * @author ljs
 * @date 2022/1/13 11:47
 */
public class VocabularyItem {
    private String name;
    private String ukPronounce;
    private String usPronounce;
    private String ukSpeak;
    private String usSpeak;
    private String translate;

    public String getUkSpeak() {
        return ukSpeak;
    }

    public void setUkSpeak(String ukSpeak) {
        this.ukSpeak = ukSpeak;
    }

    public String getUsSpeak() {
        return usSpeak;
    }

    public void setUsSpeak(String usSpeak) {
        this.usSpeak = usSpeak;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUkPronounce() {
        return ukPronounce;
    }

    public void setUkPronounce(String ukPronounce) {
        this.ukPronounce = ukPronounce;
    }

    public String getUsPronounce() {
        return usPronounce;
    }

    public void setUsPronounce(String usPronounce) {
        this.usPronounce = usPronounce;
    }

    public String getTranslate() {
        return translate;
    }

    public void setTranslate(String translate) {
        this.translate = translate;
    }
}
