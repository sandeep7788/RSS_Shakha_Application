package com.rss.suchi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NkkUserMainModel {

    @SerializedName("data")
    @Expose
    private NkkDetailsModel data;
    @SerializedName("status")
    @Expose
    private Integer status;

    public NkkDetailsModel getData() {
        return data;
    }

    public void setData(NkkDetailsModel data) {
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}


