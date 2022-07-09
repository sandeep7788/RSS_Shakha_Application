package com.rss.suchi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DashBoardShakaList {

    @SerializedName("shakhaid")
    @Expose
    private Long shakhaid;
    @SerializedName("shakha_name")
    @Expose
    private String shakha_name;

    public DashBoardShakaList(Long shakhaid, String shakha_name, Long total) {
        this.shakhaid = shakhaid;
        this.shakha_name = shakha_name;
        this.total = total;
    }

    public Long getShakhaid() {
        return shakhaid;
    }

    public void setShakhaid(Long shakhaid) {
        this.shakhaid = shakhaid;
    }

    public String getShakha_name() {
        return shakha_name;
    }

    public void setShakha_name(String shakha_name) {
        this.shakha_name = shakha_name;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    @SerializedName("total")
    @Expose
    private Long total;
}