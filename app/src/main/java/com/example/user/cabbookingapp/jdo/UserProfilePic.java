package com.example.user.cabbookingapp.jdo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 05/06/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfilePic {

    @JsonProperty("image")
    HashMap<String,String> UserImage;

    @JsonProperty("emails")
    ArrayList<HashMap<String,String>> UserEmailId;

    public ArrayList<HashMap<String, String>> getUserEmailId() {
        return UserEmailId;
    }

    public void setUserEmailId(ArrayList<HashMap<String, String>> userEmailId) {
        UserEmailId = userEmailId;
    }

    public HashMap<String, String> getUserImage() {
        return UserImage;
    }

    public void setUserImage(HashMap<String, String> userImage) {
        UserImage = userImage;
    }
}
