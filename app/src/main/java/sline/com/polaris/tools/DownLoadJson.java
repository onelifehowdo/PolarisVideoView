package sline.com.polaris.tools;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import sline.com.polaris.DoorActivity;

/**
 * Created by dell on 2018/9/8.
 */

public class DownLoadJson implements Runnable {


    private String url, json,name;
    private Handler handler;
    private ArrayList<String> jsonList;
    private int GET_JSON_SUCCEED,GET_JSON_FAIL;

    public DownLoadJson(String url,String name, Handler handler,int GET_JSON_SUCCEED,int GET_JSON_FAIL) {
        this.url = url;
        this.name=name;
        this.handler = handler;
        this.GET_JSON_FAIL=GET_JSON_FAIL;
        this.GET_JSON_SUCCEED=GET_JSON_SUCCEED;
    }


    @Override
    public void run() {
        try {
            json = getJson(url);
            jsonList = formatJson(json,name);
            mixJson(jsonList);
            if(handler!=null)
            new MakeMessage(GET_JSON_SUCCEED, 0, 0, jsonList, handler).makeMessage();//返回JSON
        } catch (Exception e) {
            e.printStackTrace();
            if(handler!=null)
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
            Log.i("Tag", "网络失败");
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

    private void mixJson(ArrayList<String> jsonList) {
        for (int i = 0; i < jsonList.size() - 1; i++) {
            int index;
            String temp;
            index = (int) (Math.random() * (jsonList.size() - i)) + i;
            temp = jsonList.get(i);
            jsonList.set(i, jsonList.get(index));
            jsonList.set(index, temp);
        }
    }

    private ArrayList<String> formatJson(String json,String name) throws Exception {
        ArrayList<String> jsonList = new ArrayList<>();
        JSONObject jsonObject;
        jsonObject = new JSONObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray(name);
        for (int i = 0; i < jsonArray.length(); i++) {
            jsonList.add(jsonArray.getString(i));
        }
        return jsonList;
    }
}