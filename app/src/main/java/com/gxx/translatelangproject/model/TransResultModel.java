package com.gxx.translatelangproject.model;

import java.util.List;

public class TransResultModel {
    //翻译回来的原文 + 译文
    private String src;
    private String dst;

    private String resourceKey;
    private String resourceValue;
    private List<String> resourceChildList;


    public List<String> getResourceChildList() {
        return resourceChildList;
    }

    public void setResourceChildList(List<String> resourceChildList) {
        this.resourceChildList = resourceChildList;
    }

    public String getResourceKey() {
        return resourceKey;
    }

    public void setResourceKey(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    public String getResourceValue() {
        return resourceValue;
    }

    public void setResourceValue(String resourceValue) {
        this.resourceValue = resourceValue;
    }



    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getDst() {
        return dst;
    }

    public void setDst(String dst) {
        this.dst = dst;
    }
}
