package com.adaptavant.cabapp.jdo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by user on 01/06/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDetailJDO implements Serializable{

    String response;
    CustomerJDO customer;

//    HashMap <String,String> customer;

    public CustomerJDO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerJDO customer) {
        this.customer = customer;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

//    public HashMap<String, String> getCustomer() {
//        return customer;
//    }
//
//    public void setCustomer(HashMap<String, String> customer) {
//        this.customer = customer;
//    }
}
