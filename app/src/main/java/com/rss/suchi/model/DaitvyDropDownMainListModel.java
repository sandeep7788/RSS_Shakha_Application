package com.rss.suchi.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DaitvyDropDownMainListModel {

    @SerializedName("data")
    @Expose
    private ArrayList<DaitvyDropDownListModel> data = null;
    @SerializedName("status")
    @Expose
    private Integer status;

    public ArrayList<DaitvyDropDownListModel> getData() {
        return data;
    }

    public void setData(ArrayList<DaitvyDropDownListModel> data) {
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}

