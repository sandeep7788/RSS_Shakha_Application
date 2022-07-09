package com.rss.rss_application.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ViewRegistrationUserModel {

    @SerializedName("data")
    @Expose
    private ViewRegistration data;
    @SerializedName("status")
    @Expose
    private Integer status;

    public ViewRegistration getData() {
        return data;
    }

    public void setData(ViewRegistration data) {
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }


}


