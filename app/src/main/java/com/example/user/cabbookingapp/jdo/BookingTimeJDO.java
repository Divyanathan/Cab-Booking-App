package com.example.user.cabbookingapp.jdo;

import java.io.Serializable;

/**
 * Created by user on 06/06/17.
 */

public class BookingTimeJDO implements Serializable {

    String TimingID;
    String DisplayTime;
    int BookingTime;
    String status;

    public BookingTimeJDO(String pTimingID, String displayTime, int bookingTime, String status) {
        TimingID = pTimingID;
        DisplayTime = displayTime;
        BookingTime = bookingTime;
        this.status = status;
    }

    public String getTimingID() {
        return TimingID;
    }

    public void setTimingID(String timingID) {
        TimingID = timingID;
    }

    public String getDisplayTime() {
        return DisplayTime;
    }

    public void setDisplayTime(String displayTime) {
        DisplayTime = displayTime;
    }

    public int getBookingTime() {
        return BookingTime;
    }

    public void setBookingTime(int bookingTime) {
        BookingTime = bookingTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
