package com.rss.suchi.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ShakhaDropDownMainListModel {

    @SerializedName("data")
    @Expose
    private ArrayList<ShakhaDropDownListModel> data = null;
    @SerializedName("status")
    @Expose
    private Integer status;

    public ArrayList<ShakhaDropDownListModel> getData() {
        return data;
    }

    public void setData(ArrayList<ShakhaDropDownListModel> data) {
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}

