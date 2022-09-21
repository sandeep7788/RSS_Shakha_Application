package com.rss.suchi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ShakaUserSearch {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("lastname")
    @Expose
    private String lastname;
    @SerializedName("occupation")
    @Expose
    private String occupation;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("whatsapp")
    @Expose
    private String whatsapp;
    @SerializedName("pre_respon")
    @Expose
    private String preRespon;

    public String getShakha() {
        return shakha;
    }

    public void setShakha(String shakha) {
        this.shakha = shakha;
    }

    @SerializedName("shakha")
    @Expose
    private String shakha;
    @SerializedName("ss_id")
    @Expose
    private Integer ssId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getPreRespon() {
        return preRespon;
    }

    public void setPreRespon(String preRespon) {
        this.preRespon = preRespon;
    }

    public Integer getSsId() {
        return ssId;
    }

    public void setSsId(Integer ssId) {
        this.ssId = ssId;
    }

}
