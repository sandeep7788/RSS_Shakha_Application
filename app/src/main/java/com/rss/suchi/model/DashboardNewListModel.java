package com.rss.suchi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DashboardNewListModel {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("total_shaka")
    @Expose
    private Integer totalShaka;
    @SerializedName("toatl_swayam_sevak")
    @Expose
    private Integer toatlSwayamSevak;
    @SerializedName("present")
    @Expose
    private Integer present;
    @SerializedName("flag")
    @Expose
    private String flag;
    @SerializedName("flage")
    @Expose
    private String flage;

    public String getFlage() {
        return flage;
    }

    public void setFlage(String flage) {
        this.flage = flage;
    }

    @SerializedName("id")
    @Expose
    private Integer id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTotalShaka() {
        return totalShaka;
    }

    public void setTotalShaka(Integer totalShaka) {
        this.totalShaka = totalShaka;
    }

    public Integer getToatlSwayamSevak() {
        return toatlSwayamSevak;
    }

    public void setToatlSwayamSevak(Integer toatlSwayamSevak) {
        this.toatlSwayamSevak = toatlSwayamSevak;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public Integer getPresent() {
        return present;
    }

    public void setPresent(Integer present) {
        this.present = present;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}