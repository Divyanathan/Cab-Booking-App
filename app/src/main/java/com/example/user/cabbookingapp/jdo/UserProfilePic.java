package com.example.user.cabbookingapp.jdo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

/**
 * Created by user on 05/06/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfilePic {

    @JsonProperty("image")
    HashMap<String,String> Image;

    public HashMap<String, String> getImage() {
        return Image;
    }

    public void setImage(HashMap<String, String> image) {
        Image = image;
    }
}
