package com.example.user.cabbookingapp.jdo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by user on 17/05/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class TimingsJDO implements Serializable {


    @JsonProperty("Key")
    String Key;
    @JsonProperty("id")
    String Id;
    @JsonProperty("status")
    String Status;
    @JsonProperty("serviceName")
    String ServiceName;
    @JsonProperty("startDateString")
    String startDateString;
    @JsonProperty("endDateString")
    String endDateString;
    @JsonProperty("colorcode")
    String colorcode;
    @JsonProperty("f_Key")
    String f_Key;
    @JsonProperty("serviceCost")
    String serviceCost;
    @JsonProperty("providersList")
    ArrayList<String> providersList;
    @JsonProperty("startDateLong")
    int startDateLong;
    @JsonProperty("endDateLong")
    int endDateLong;


    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getServiceName() {
        return ServiceName;
    }

    public void setServiceName(String serviceName) {
        ServiceName = serviceName;
    }

    public String getStartDateString() {
        return startDateString;
    }

    public void setStartDateString(String startDateString) {
        this.startDateString = startDateString;
    }

    public String getEndDateString() {
        return endDateString;
    }

    public void setEndDateString(String endDateString) {
        this.endDateString = endDateString;
    }

    public String getColorcode() {
        return colorcode;
    }

    public void setColorcode(String colorcode) {
        this.colorcode = colorcode;
    }

    public String getF_Key() {
        return f_Key;
    }

    public void setF_Key(String f_Key) {
        this.f_Key = f_Key;
    }

    public String getServiceCost() {
        return serviceCost;
    }

    public void setServiceCost(String serviceCost) {
        this.serviceCost = serviceCost;
    }

    public ArrayList<String> getProvidersList() {
        return providersList;
    }

    public void setProvidersList(ArrayList<String> providersList) {
        this.providersList = providersList;
    }

    public int getStartDateLong() {
        return startDateLong;
    }

    public void setStartDateLong(int startDateLong) {
        this.startDateLong = startDateLong;
    }

    public int getEndDateLong() {
        return endDateLong;
    }

    public void setEndDateLong(int endDateLong) {
        this.endDateLong = endDateLong;
    }
}
