package com.rss.suchi.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NkkMainModel {

    @SerializedName("data")
    @Expose
    private List<NkkListModel> data = null;
    @SerializedName("status")
    @Expose
    private Integer status;

    public List<NkkListModel> getData() {
        return data;
    }

    public void setData(List<NkkListModel> data) {
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}