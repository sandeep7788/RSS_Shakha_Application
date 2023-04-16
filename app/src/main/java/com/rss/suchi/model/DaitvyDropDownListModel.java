package com.rss.suchi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DaitvyDropDownListModel {

    @SerializedName("daitav_id")
    @Expose
    private Integer daitav_id;
    @SerializedName("daitav_name")
    @Expose
    private String daitav_name;

    public Integer getDaitav_id() {
        return daitav_id;
    }

    public void setDaitav_id(Integer daitav_id) {
        this.daitav_id = daitav_id;
    }

    public String getDaitav_name() {
        return daitav_name.trim();
    }

    public void setDaitav_name(String daitav_name) {
        this.daitav_name = daitav_name;
    }
}
