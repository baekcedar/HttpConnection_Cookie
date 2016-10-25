package com.baekcedar.android.remote_httpurlconnection_cookie;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by HM on 2016-10-25.
 */

public class Remote {
    private static final String TAG = "ResponseCode : ";
    static CookieManager cookieManager = new CookieManager();
    static final String COOKIE_URL = "http://192.168.0.171";
    public static String getData (String webURL) throws Exception{
        StringBuilder result = new StringBuilder();
        String dataLine;
        URL url = new URL(webURL);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();

        conn.setRequestMethod("get");
        int responseCode = conn.getResponseCode();

        // 200
        if(responseCode == HttpsURLConnection.HTTP_OK){
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            while( (dataLine = br.readLine()) != null){
                result.append(dataLine);
            }
            br.close();
        }else{
            Log.i(TAG,""+responseCode );
        }


        return result.toString();
    }
    public static String postData (String webURL, Map params) throws Exception {

        StringBuilder result = new StringBuilder();
        String dataLine;
        URL url = new URL(webURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        OutputStream os = conn.getOutputStream();
        ArrayList<String> keyset = new ArrayList<>(params.keySet());

        for(String key : keyset){
            String param = key + "=" + params.get(key)+"&";
            os.write(param.getBytes());
        }
        os.flush();
        os.close();

        int responseCode = conn.getResponseCode();
        // 200
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            while ((dataLine = br.readLine()) != null) {
                result.append(dataLine);
            }
            br.close();
        } else {
            Log.i(TAG, "" + responseCode);
        }

        Map<String, List<String>> headers =  conn.getHeaderFields();
        List<String> cookies =  headers.get("Set-Cookie");


        if( cookies != null && cookies.size() > 0 ){
            for( String cookie : cookies){
                cookieManager.getCookieStore().add(URI.create(COOKIE_URL),HttpCookie.parse(cookie).get(0));
            }

        }

        // 헤더 전체 로그 보기
        Map<String, List<String>> headers2 =  conn.getHeaderFields();
        ArrayList<String> logkeyset = new ArrayList<>(headers2.keySet());
        for( String key : logkeyset){
            for(String value : headers.get(key)) {
                Log.i("HEADERS", key + ":" + value);
            }
        }

//        List<String> cookie = headers.get("Set-Cookie");
        return result.toString();
    }

}