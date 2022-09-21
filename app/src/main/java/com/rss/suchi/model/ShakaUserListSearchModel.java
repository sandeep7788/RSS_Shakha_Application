package com.rss.suchi.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ShakaUserListSearchModel {

    @SerializedName("data")
    @Expose
    private List<ShakaUserSearch> data = null;
    @SerializedName("status")
    @Expose
    private Integer status;

    public List<ShakaUserSearch> getData() {
        return data;
    }

    public void setData(List<ShakaUserSearch> data) {
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}