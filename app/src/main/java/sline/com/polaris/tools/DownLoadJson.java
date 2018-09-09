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


    private String url, json, name, Method,data;
    private Handler handler;
    private ArrayList<String> jsonList;
    private int GET_JSON_SUCCEED, GET_JSON_FAIL;

    public DownLoadJson(String url,String name, String Method, Handler handler, int GET_JSON_SUCCEED, int GET_JSON_FAIL) {
        this.url = url;
        this.name = name;
        this.handler = handler;
        this.Method = Method;
        this.GET_JSON_FAIL = GET_JSON_FAIL;
        this.GET_JSON_SUCCEED = GET_JSON_SUCCEED;
    }
    public DownLoadJson(String url,String name,String data,String Method,  Handler handler, int GET_JSON_SUCCEED, int GET_JSON_FAIL) {
        this.url = url;
        this.name = name;
        this.handler = handler;
        this.Method = Method;
        this.data=data;
        this.GET_JSON_FAIL = GET_JSON_FAIL;
        this.GET_JSON_SUCCEED = GET_JSON_SUCCEED;
    }


    @Override
    public void run() {


        try {
            if (Method.equals("GET")) {
                json = getJson(url);
                jsonList = formatJson(json, name);
                mixJson(jsonList);
            }
            else if(Method.equals("POST")){
                json = getJson(url,data);
                jsonList = formatJson(json, name);
            }
            if (handler != null)
                new MakeMessage(GET_JSON_SUCCEED, 0, 0, jsonList, handler).makeMessage();//返回JSON
        } catch (Exception e) {
            e.printStackTrace();
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

    public String getJson(String url, String data) throws Exception {
        Log.i("tag",url+"data:"+data);
        HttpURLConnection connection;
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

    private ArrayList<String> formatJson(String json, String name) throws Exception {
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