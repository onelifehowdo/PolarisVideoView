package sline.com.polaris;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class ActionActivity extends AppCompatActivity {
    private String url,chose;
    private List<String> jsonList = new ArrayList();
    private ListView listView;
    private TextView count;
    private ArrayAdapter<String> arrayAdapter;

    @SuppressLint("HandlerLeak")
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:{
                    arrayAdapter.notifyDataSetChanged();
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
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_action);
        url=getIntent().getStringExtra("url");
        chose=getIntent().getStringExtra("chose");
        listView=findViewById(R.id.actionListView);
        arrayAdapter=new ArrayAdapter<String>(this,R.layout.action_listview_item,jsonList);
        listView.setAdapter(arrayAdapter);
        DownJson downJson=new DownJson("http://"+url+"/web/webpage/actionforapp.php?chose=",chose);
        downJson.start();
    }


    class DownJson extends Thread {


        private String url,chose;


        public DownJson(String url,String chose) {
            this.url = url;
            this.chose=chose;
        }


        @Override
        public void run() {
            getJson(url,chose);
        }

        private void getJson(String url,String chose) {
            String json = "";
            InputStreamReader inputStream;
            BufferedReader bufferedReader;
            URLConnection urlConnection;
            try {
                urlConnection = new URL(url+chose).openConnection();
                urlConnection.setConnectTimeout(5000);
                inputStream = new InputStreamReader(urlConnection.getInputStream(), "utf-8");
                bufferedReader = new BufferedReader(inputStream);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    json += line;
                }
                inputStream.close();
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(ActionActivity.this, "网络失败", Toast.LENGTH_SHORT).show();
            }

            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray jsonArray = jsonObject.getJSONArray(chose);
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonList.add(jsonArray.getString(i).substring(0,jsonArray.getString(i).lastIndexOf(".")));
                }

                Message message=Message.obtain();
                message.what=1;
                handler.sendMessage(message);

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(ActionActivity.this, "未获取JSON", Toast.LENGTH_SHORT).show();
            }
        }

    }



}
