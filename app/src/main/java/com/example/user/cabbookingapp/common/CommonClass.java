package com.example.user.cabbookingapp.common;

import android.net.ConnectivityManager;

import com.example.user.cabbookingapp.httphelper.HttpUrlHelper;
import com.example.user.cabbookingapp.jdo.ContentTypeJDO;

import java.util.ArrayList;

/**
 * Created by user on 02/06/17.
 */

public class CommonClass {

    public static HttpUrlHelper getTheHttpUrlHelper(String pUrl, String pRequestMethod, String pPayLoad) {

        ArrayList<ContentTypeJDO> lContentType = new ArrayList<>();
        ContentTypeJDO lContentTypeJDo = new ContentTypeJDO();
        lContentTypeJDo.setKey("Content-Type");
        lContentTypeJDo.setValue("application/json");
        lContentType.add(lContentTypeJDo);

        HttpUrlHelper lHttpHelper = new HttpUrlHelper();
        lHttpHelper.setUrl(pUrl);
        lHttpHelper.setHttpRequetMethod(pRequestMethod);
        lHttpHelper.setPayload(pPayLoad);
        lHttpHelper.setContentType(lContentType);

        return lHttpHelper;
    }
}
