package com.adaptavant.cabapp.jdo;

import java.io.Serializable;

/**
 * Created by user on 01/06/17.
 */

public class HttpHeaderJDO implements Serializable {
    String Key;
    String Value;

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }
}
