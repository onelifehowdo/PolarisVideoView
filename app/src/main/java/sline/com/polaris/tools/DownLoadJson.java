package sline.com.polaris.tools;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import sline.com.polaris.DoorActivity;

/**
 * Created by dell on 2018/9/8.
 */

public class DownLoadJson implements Runnable {


    private String url, use, postData = null;
    private Handler handler;
    //    private ArrayList data;
    private String json;
    private int GET_JSON_SUCCEED, GET_JSON_FAIL;

    public DownLoadJson(String url, String use, Handler handler, int GET_JSON_SUCCEED, int GET_JSON_FAIL) {
        this.url = url;
        this.use = use;
        this.handler = handler;
        this.GET_JSON_FAIL = GET_JSON_FAIL;
        this.GET_JSON_SUCCEED = GET_JSON_SUCCEED;
    }

    public DownLoadJson(String url, String use, String postData, Handler handler, int GET_JSON_SUCCEED, int GET_JSON_FAIL) {
        this.url = url;
        this.use = use;
        this.postData = postData;
        this.handler = handler;
        this.GET_JSON_FAIL = GET_JSON_FAIL;
        this.GET_JSON_SUCCEED = GET_JSON_SUCCEED;
    }

    @Override
    public void run() {
        try {
            if (use.equals("getDoorImage")) {
                json = getJson(url);
            } else if (use.equals("getVideo")) {
                json = getJson(url);
            } else if (use.equals("getdata")) {
                json = getJson(url, "chose=" + postData);
            }
            if (handler != null)
                new MakeMessage(GET_JSON_SUCCEED, 0, 0, json, handler).makeMessage();//返回JSON
        } catch (Exception e) {
            if (handler != null)
                new MakeMessage(GET_JSON_FAIL, 0, 0, null, handler).makeMessage();//网络失败改变文字
        }
    }

    private String getJson(String url) throws Exception {
        String json = "";
        BufferedReader bufferedReader = null;
        URLConnection urlConnection;
        try {
            Log.i("Tag", "尝试下载json");
            urlConnection = new URL(url).openConnection();
            urlConnection.setConnectTimeout(5000);
            bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                json += line;
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (bufferedReader != null)
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            return json;
        }


    }

    public String getJson(String url, String data) throws Exception {
        HttpURLConnection connection = null;
        BufferedWriter bufferedWriter = null;
        BufferedReader bufferedReader = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(2000);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String temp = "", a;
            while ((a = bufferedReader.readLine()) != null)
                temp += a;
            return temp;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            throw e;
        } finally {
            if (bufferedReader != null)
                bufferedReader.close();
            if (bufferedWriter != null)
                bufferedWriter.close();
            connection.disconnect();
        }


    }


}