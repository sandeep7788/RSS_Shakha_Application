package com.rss.suchi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class NkkDetailsModel {

    @SerializedName("nkk_id")
    @Expose
    private Integer nkkId;
    @SerializedName("nkk_name")
    @Expose
    private String nkkName;
    @SerializedName("nkk_last_name")
    @Expose
    private String nkkLastName;
    @SerializedName("nkk_mobile")
    @Expose
    private String nkkMobile;
    @SerializedName("nkk_occupation")
    @Expose
    private String nkkOccupation;
    @SerializedName("nkk_address")
    @Expose
    private String nkkAddress;
    @SerializedName("nkk_dob")
    @Expose
    private String nkkDob;
    @SerializedName("nkk_blood_group")
    @Expose
    private String nkkBloodGroup;
    @SerializedName("nkk_email")
    @Expose
    private String nkkEmail;
    @SerializedName("nkk_daitva")
    @Expose
    private String nkkDaitva;
    @SerializedName("nkk_shikshan")
    @Expose
    private String nkkShikshan;
    @SerializedName("nkk_shikshan_year")
    @Expose
    private Integer nkkShikshanYear;
    @SerializedName("nkk_shaka_id")
    @Expose
    private Integer nkkShakaId;

    public Integer getNkkId() {
        return nkkId;
    }

    public void setNkkId(Integer nkkId) {
        this.nkkId = nkkId;
    }

    public String getNkkName() {
        return nkkName;
    }

    public void setNkkName(String nkkName) {
        this.nkkName = nkkName;
    }

    public String getNkkLastName() {
        return nkkLastName;
    }

    public void setNkkLastName(String nkkLastName) {
        this.nkkLastName = nkkLastName;
    }

    public String getNkkMobile() {
        return nkkMobile;
    }

    public void setNkkMobile(String nkkMobile) {
        this.nkkMobile = nkkMobile;
    }

    public String getNkkOccupation() {
        return nkkOccupation;
    }

    public void setNkkOccupation(String nkkOccupation) {
        this.nkkOccupation = nkkOccupation;
    }

    public String getNkkAddress() {
        return nkkAddress;
    }

    public void setNkkAddress(String nkkAddress) {
        this.nkkAddress = nkkAddress;
    }

    public String getNkkDob() {
        return nkkDob;
    }

    public void setNkkDob(String nkkDob) {
        this.nkkDob = nkkDob;
    }

    public String getNkkBloodGroup() {
        return nkkBloodGroup;
    }

    public void setNkkBloodGroup(String nkkBloodGroup) {
        this.nkkBloodGroup = nkkBloodGroup;
    }

    public String getNkkEmail() {
        return nkkEmail;
    }

    public void setNkkEmail(String nkkEmail) {
        this.nkkEmail = nkkEmail;
    }

    public String getNkkDaitva() {
        return nkkDaitva;
    }

    public void setNkkDaitva(String nkkDaitva) {
        this.nkkDaitva = nkkDaitva;
    }

    public String getNkkShikshan() {
        return nkkShikshan;
    }

    public void setNkkShikshan(String nkkShikshan) {
        this.nkkShikshan = nkkShikshan;
    }

    public Integer getNkkShikshanYear() {
        return nkkShikshanYear;
    }

    public void setNkkShikshanYear(Integer nkkShikshanYear) {
        this.nkkShikshanYear = nkkShikshanYear;
    }

    public Integer getNkkShakaId() {
        return nkkShakaId;
    }

    public void setNkkShakaId(Integer nkkShakaId) {
        this.nkkShakaId = nkkShakaId;
    }

}
