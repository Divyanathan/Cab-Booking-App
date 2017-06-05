package com.example.user.cabbookingapp.httphelper;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by user on 01/06/17.
 */

public class HttpConnection {

    URL mUrl;
    HttpURLConnection mHttpURLConnection;
    OutputStreamWriter mOutPutStremWriter;
    BufferedReader mBufferedReader;
    String mResponse="";
    private static final String TAG = "HttpConnection";

    public String getTheResponse(HttpUrlHelper pHttpUrlHelper) {

        try {
            mUrl = new URL(pHttpUrlHelper.getUrl());
            mHttpURLConnection = (HttpURLConnection) mUrl.openConnection();
            mHttpURLConnection.setDoInput(true);
            mHttpURLConnection.setRequestMethod(pHttpUrlHelper.getHttpRequetMethod());
            if (!pHttpUrlHelper.getHttpRequetMethod().equals("GET")) {
                mHttpURLConnection.setDoOutput(true);
                for (int i = 0; i < pHttpUrlHelper.getContentType().size(); i++) {
                    mHttpURLConnection.addRequestProperty(pHttpUrlHelper.getContentType().get(i).getKey(), pHttpUrlHelper.getContentType().get(i).getValue());
                }
                mOutPutStremWriter = new OutputStreamWriter(mHttpURLConnection.getOutputStream());
                mOutPutStremWriter.write(pHttpUrlHelper.getPayload());
                mOutPutStremWriter.flush();
                mOutPutStremWriter.close();

                Log.d(TAG, "getTheResponse: for post");
            }
            mHttpURLConnection.connect();
            mBufferedReader = new BufferedReader(new InputStreamReader(mHttpURLConnection.getInputStream()));
            String data;
            while ((data = mBufferedReader.readLine()) != null) {
                 mResponse=mResponse+data;
                Log.d(TAG, "getTheResponse: "+mResponse);
            }
            return mResponse;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


}
