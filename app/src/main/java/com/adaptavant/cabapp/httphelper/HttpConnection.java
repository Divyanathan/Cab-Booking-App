package com.adaptavant.cabapp.httphelper;

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


    /**
     *
     * @param pHttpUrlHelper HttpHelperClass has the Url ,request Method, Request Body, and Header Key and Value
     * @return
     * It Returns Response String...
     */
    public String getHttpResponse(HttpUrlHelper pHttpUrlHelper) {

        URL mUrl;
        HttpURLConnection mHttpURLConnection;
        OutputStreamWriter mOutPutStremWriter;
        BufferedReader mBufferedReader;
        final String TAG = "HttpConnection";

        try {
            mUrl = new URL(pHttpUrlHelper.getUrl());
            mHttpURLConnection = (HttpURLConnection) mUrl.openConnection();
            mHttpURLConnection.setDoInput(true);
            mHttpURLConnection.setRequestMethod(pHttpUrlHelper.getHttpRequetMethod());
            if (!pHttpUrlHelper.getHttpRequetMethod().equals("GET")) {
                mHttpURLConnection.setDoOutput(true);
                for (int i = 0; i < pHttpUrlHelper.getHttpHeader().size(); i++) {
                    mHttpURLConnection.addRequestProperty(pHttpUrlHelper.getHttpHeader().get(i).getKey(), pHttpUrlHelper.getHttpHeader().get(i).getValue());
                }
                mOutPutStremWriter = new OutputStreamWriter(mHttpURLConnection.getOutputStream());
                mOutPutStremWriter.write(pHttpUrlHelper.getPayload());
                mOutPutStremWriter.flush();
                mOutPutStremWriter.close();

                Log.d(TAG, "getHttpResponse: for post");
            }
            mHttpURLConnection.connect();
            mBufferedReader = new BufferedReader(new InputStreamReader(mHttpURLConnection.getInputStream()));
            String lResponseData;
            StringBuilder lResponseStringBuilder=new StringBuilder();
            while ((lResponseData = mBufferedReader.readLine()) != null) {
                lResponseStringBuilder.append(lResponseData);
                Log.d(TAG, "getHttpResponse: "+lResponseStringBuilder.toString());
            }
            return lResponseStringBuilder.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }




}
