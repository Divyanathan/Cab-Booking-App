package com.adaptavant.cabapp.httphelper;

import com.adaptavant.cabapp.jdo.HttpHeaderJDO;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by user on 01/06/17.
 */

public class HttpUrlHelper  implements Serializable{

    String Url;
    String HttpRequetMethod;
    String Payload;
    ArrayList<HttpHeaderJDO> HttpHeader;

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

    public ArrayList<HttpHeaderJDO> getHttpHeader() {
        return HttpHeader;
    }

    public void setHttpHeader(ArrayList<HttpHeaderJDO> httpHeader) {
        HttpHeader = httpHeader;
    }

}
