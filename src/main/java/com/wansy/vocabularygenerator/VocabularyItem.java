package com.wansy.vocabularygenerator;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.util.StringUtils;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

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
    private Speak ukSpeak;
    private Speak usSpeak;
    private String translate;

    public Speak getUkSpeak() {
        return ukSpeak;
    }

    public void setUkSpeak(Speak ukSpeak) {
        this.ukSpeak = ukSpeak;
    }

    public Speak getUsSpeak() {
        return usSpeak;
    }

    public void setUsSpeak(Speak usSpeak) {
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

    public class Speak {
        private String url;
        private String flag;

        public String getUrl() {
            return url;
        }

        public Speak(String url, Boolean ukOrUs) {
            this.url = url;
            this.flag = ukOrUs ? "UK" : "US";
        }

        public void down(String audioDirName, String audioDir) throws Exception {
            if (!StringUtils.isEmpty(this.url)) {
                String audioName = name + "(" + this.flag + ").mp3";
                String dir = audioDirName + "/" + audioName;
                URL url = new URL(this.url);
                this.url = dir;
                try (InputStream in = url.openStream(); OutputStream out = new FileOutputStream(audioDir + audioName)) {
                    IOUtils.copy(in, out);
                }
                Thread.sleep(100);
            }
        }

        @Override
        public String toString() {
            return this.url;
        }
    }
}
