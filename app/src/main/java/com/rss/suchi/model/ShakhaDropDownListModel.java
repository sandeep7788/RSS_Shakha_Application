package com.rss.suchi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ShakhaDropDownListModel {

    @SerializedName("shakhaid")
    @Expose
    private Integer shakhaid;
    @SerializedName("shakha")
    @Expose
    private String shakha;

    public void setShakhaid(Integer shakhaid) {
        this.shakhaid = shakhaid;
    }

    public void setShakha(String shakha) {
        this.shakha = shakha;
    }

    public Integer getShakhaid() {
        return shakhaid;
    }

    public String getShakha() {
        return shakha;
    }
}
