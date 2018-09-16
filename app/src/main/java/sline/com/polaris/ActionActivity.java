package sline.com.polaris;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import sline.com.polaris.tools.DownLoadJson;
import sline.com.polaris.tools.MakeMessage;

public class ActionActivity extends AppCompatActivity {
    private String url, chose;
    private List<String> jsonList = new ArrayList();
    private ListView listView;
    private ProgressBar wait;
    private MyAdapter myAdapter;
//    private Vibrator vibrator;
    private final int GET_JSON_SUCCEED=1,GET_JSON_FAIL=0;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_JSON_SUCCEED: {
                    jsonList.addAll((List)msg.obj);
                    wait.setVisibility(View.GONE);
                    myAdapter.notifyDataSetChanged();
                    break;
                }
                case GET_JSON_FAIL: {
                    jsonList.add("No Service");
                    wait.setVisibility(View.GONE);
                    myAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_action);
//        vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        url = getIntent().getStringExtra("url");
        chose = getIntent().getStringExtra("chose");
        wait = findViewById(R.id.wait);
        listView = findViewById(R.id.actionListView);
        myAdapter = new MyAdapter(this,jsonList);
        listView.setAdapter(myAdapter);
//        new Thread(new DownJson("http://" + url + "/web/webpage/actionforapp.php?chose=", chose)).start();
        new Thread(new DownLoadJson("http://" + url + "/web/webpage/actionforapp.php",chose,"chose="+chose,"POST",handler,GET_JSON_SUCCEED,GET_JSON_FAIL,false)).start();
    }


//    class DownJson implements Runnable {
//
//
//        private String url, chose;
//
//
//        public DownJson(String url, String chose) {
//            this.url = url;
//            this.chose = chose;
//        }
//
//
//        @Override
//        public void run() {
//            getJson(url, chose);
//        }
//
//        private void getJson(String url, String chose) {
//            String json = "";
//            BufferedReader bufferedReader=null;
//            URLConnection urlConnection;
//            try {
//                urlConnection = new URL(url + chose).openConnection();
//                urlConnection.setConnectTimeout(5000);
////                inputStreamReader = new InputStreamReader(urlConnection.getInputStream(), "utf-8");
//                bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
//                String line;
//                while ((line = bufferedReader.readLine()) != null) {
//                    json += line;
//                }
//                bufferedReader.close();
//                JSONObject jsonObject = new JSONObject(json);
//                JSONArray jsonArray = jsonObject.getJSONArray(chose);
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    jsonList.add(jsonArray.getString(i).substring(0, jsonArray.getString(i).lastIndexOf(".")));
//                }
//            } catch (Exception e) {
//                jsonList.add("No Service");
//            } finally {
//                if(bufferedReader!=null)
//                    try {
//                        bufferedReader.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                new MakeMessage(ITEM_DOWN,0,0,null,handler).makeMessage();
//            }
//
//        }
//
//    }

/////////////////////////////////////////适配器///////////////////////////////////////////

    private class MyAdapter extends BaseAdapter {
        private List<String> list;
        private LayoutInflater inflater;

        public MyAdapter(Context context, List<String> list) {
            this.list = list;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final TextHolder textHolder;
            if (view == null) {
                textHolder = new TextHolder();
                view = inflater.inflate(R.layout.action_listview_item, null);
                textHolder.textView = view.findViewById(R.id.action_listview_item);
                view.setTag(textHolder);
            } else {
                textHolder = (TextHolder) view.getTag();
            }
            if(list.get(i).toString().contains("Error")||list.get(i).toString().contains("错误")||list.get(i).toString().contains("No Service")){
                ((Vibrator) getSystemService(Service.VIBRATOR_SERVICE)).vibrate(100);
//                vibrator.vibrate(100);
                textHolder.textView.setTextColor(Color.parseColor("#ff0000"));}
            textHolder.textView.setText(list.get(i));
            return view;
        }
    }

    static class TextHolder {
        TextView textView;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////
}
