package com.rss.suchi.model;


import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ShakaUserListModel {

    @SerializedName("data")
    @Expose
    private List<ShakaUser> data = null;
    @SerializedName("status")
    @Expose
    private Integer status;

    public List<ShakaUser> getData() {
        return data;
    }

    public void setData(List<ShakaUser> data) {
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}