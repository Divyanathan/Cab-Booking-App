package com.adaptavant.cabapp.jdo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 11/06/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetTodayBookingJDO {

    @JsonProperty("data")
    ArrayList<HashMap<String,String>> BookingData =new ArrayList<>();

    public ArrayList<HashMap<String, String>> getBookingData() {
        return BookingData;
    }

    public void setBookingData(ArrayList<HashMap<String, String>> bookingData) {
        this.BookingData = bookingData;
    }
}
