package com.wansy.vocabularygenerator.strategy.impl;

import java.util.List;

/**
 * @author ljs
 * @date 2025/3/11 11:21
 */
public class BaiduSymbolDto {
    private List<Part> parts;
    /**
     * 音标 英
     */
    private String ph_en;
    /**
     * 音标 美
     */
    private String ph_am;

    public String getPh_en() {
        return ph_en;
    }

    public void setPh_en(String ph_en) {
        this.ph_en = ph_en;
    }

    public String getPh_am() {
        return ph_am;
    }

    public void setPh_am(String ph_am) {
        this.ph_am = ph_am;
    }

    public List<Part> getParts() {
        return parts;
    }

    public void setParts(List<Part> parts) {
        this.parts = parts;
    }

    public static class Part {
        private List<String> means;
        private String part;

        public String getPart() {
            return part;
        }

        public void setPart(String part) {
            this.part = part;
        }

        public List<String> getMeans() {
            return means;
        }

        public void setMeans(List<String> means) {
            this.means = means;
        }
    }
}
