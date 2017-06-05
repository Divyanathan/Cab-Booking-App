package com.example.user.cabbookingapp.httphelper;

import com.example.user.cabbookingapp.jdo.ContentTypeJDO;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 01/06/17.
 */

public class HttpUrlHelper {

    String Url;
    String HttpRequetMethod;
    String Payload;
    ArrayList<ContentTypeJDO> ContentType;

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getHttpRequetMethod() {
        return HttpRequetMethod;
    }

    public void setHttpRequetMethod(String httpRequetMethod) {
        HttpRequetMethod = httpRequetMethod;
    }

    public String getPayload() {
        return Payload;
    }

    public void setPayload(String payload) {
        Payload = payload;
    }

    public ArrayList<ContentTypeJDO> getContentType() {
        return ContentType;
    }

    public void setContentType(ArrayList<ContentTypeJDO> contentType) {
        ContentType = contentType;
    }

}
