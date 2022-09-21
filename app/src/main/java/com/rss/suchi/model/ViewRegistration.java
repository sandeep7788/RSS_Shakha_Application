package com.rss.suchi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ViewRegistration {
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("lastname")
    @Expose
    private String lastname;
    @SerializedName("occupation")
    @Expose
    private String occupation;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("whatsapp")
    @Expose
    private String whatsapp;
    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName("blood_group")
    @Expose
    private String bloodGroup;
    @SerializedName("shikshan")
    @Expose
    private String shikshan;
    @SerializedName("shikshan_year")
    @Expose
    private Integer shikshanYear;
    @SerializedName("pre_respon")
    @Expose
    private String preRespon;
    @SerializedName("oauth")
    @Expose
    private Integer oauth;
    @SerializedName("ghosh")
    @Expose
    private Integer ghosh;
    @SerializedName("vidhik_present")
    @Expose
    private String vidhikPresent;
    @SerializedName("vidhik_past")
    @Expose
    private String vidhikPast;
    @SerializedName("createdy")
    @Expose
    private Integer createdy;
    @SerializedName("updateddy")
    @Expose
    private Integer updateddy;
    @SerializedName("shakha")
    @Expose
    private Integer shakha;
    @SerializedName("other")
    @Expose
    private String other;
    @SerializedName("ss")
    @Expose
    private Integer ss;
    @SerializedName("uniform")
    @Expose
    private Boolean uniform;

    public Boolean getUniform() {
        return uniform;
    }

    public void setUniform(Boolean uniform) {
        this.uniform = uniform;
    }

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getShikshan() {
        return shikshan;
    }

    public void setShikshan(String shikshan) {
        this.shikshan = shikshan;
    }

    public Integer getShikshanYear() {
        return shikshanYear;
    }

    public void setShikshanYear(Integer shikshanYear) {
        this.shikshanYear = shikshanYear;
    }

    public String getPreRespon() {
        return preRespon;
    }

    public void setPreRespon(String preRespon) {
        this.preRespon = preRespon;
    }

    public Integer getOauth() {
        return oauth;
    }

    public void setOauth(Integer oauth) {
        this.oauth = oauth;
    }

    public Integer getGhosh() {
        return ghosh;
    }

    public void setGhosh(Integer ghosh) {
        this.ghosh = ghosh;
    }

    public String getVidhikPresent() {
        return vidhikPresent;
    }

    public void setVidhikPresent(String vidhikPresent) {
        this.vidhikPresent = vidhikPresent;
    }

    public String getVidhikPast() {
        return vidhikPast;
    }

    public void setVidhikPast(String vidhikPast) {
        this.vidhikPast = vidhikPast;
    }

    public Integer getCreatedy() {
        return createdy;
    }

    public void setCreatedy(Integer createdy) {
        this.createdy = createdy;
    }

    public Integer getUpdateddy() {
        return updateddy;
    }

    public void setUpdateddy(Integer updateddy) {
        this.updateddy = updateddy;
    }

    public Integer getShakha() {
        return shakha;
    }

    public void setShakha(Integer shakha) {
        this.shakha = shakha;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public Integer getSs() {
        return ss;
    }

    public void setSs(Integer ss) {
        this.ss = ss;
    }

}
