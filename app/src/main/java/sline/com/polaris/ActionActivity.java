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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

public class ActionActivity extends AppCompatActivity implements View.OnTouchListener{
    private String url, chose;
    private List<String> jsonList = new ArrayList<String>();
    private ListView listView;
    private ProgressBar wait;
    private MyAdapter myAdapter;
    private ImageView logo;
    private boolean VIBRATOR_done=false;
    private float lastBottom=0;
    private final int GET_JSON_SUCCEED = 1, GET_JSON_FAIL = 0;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_JSON_SUCCEED: {
                    jsonList.addAll((List) msg.obj);
                    wait.setVisibility(View.GONE);
                    logo.setImageResource(R.mipmap.logo);
                    myAdapter.notifyDataSetChanged();
                    break;
                }
                case GET_JSON_FAIL: {
                    jsonList.add("No Service");
                    wait.setVisibility(View.GONE);
                    logo.setImageResource(R.mipmap.logo);
                    myAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_action);
        url = getIntent().getStringExtra("url");
        chose = getIntent().getStringExtra("chose");
        wait = findViewById(R.id.wait);
        logo=findViewById(R.id.logo);
        listView = findViewById(R.id.actionListView);
        myAdapter = new MyAdapter(this, jsonList);
        listView.setAdapter(myAdapter);
        listView.setOnTouchListener(this);
        new Thread(new DownLoadJson("http://" + url + "/web/webpage/actionforapp.php", chose, "chose=" + chose, "POST", handler, GET_JSON_SUCCEED, GET_JSON_FAIL, false)).start();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_MOVE:{
                if(isListViewLast()){
                    if(lastBottom==0)
                        lastBottom=motionEvent.getY();
                    RelativeLayout.LayoutParams LP= (RelativeLayout.LayoutParams) listView.getLayoutParams();
                    float trans=lastBottom-motionEvent.getY(),logoAlpha,logoHeight=logo.getHeight();
                    if(trans<=logoHeight){
                        LP.bottomMargin = (int) trans;
                        logoAlpha=trans/logoHeight;
                    }
                    else{
                        LP.bottomMargin = (int)logoHeight;
                        logoAlpha=1;
                    }
                    listView.setLayoutParams(LP);
                    logo.setAlpha(logoAlpha);
                }
                break;
            }
            case MotionEvent.ACTION_UP:{
                lastBottom=0;
                RelativeLayout.LayoutParams LP= (RelativeLayout.LayoutParams) listView.getLayoutParams();
                LP.bottomMargin=0;
                listView.setLayoutParams(LP);
                logo.setAlpha(0.0f);
                break;
            }
        }
        return false;
    }

    private boolean isListViewLast(){
        boolean flag=false;
        if(listView.getLastVisiblePosition()==(listView.getCount()-1)){
            if((listView.getChildAt(listView.getLastVisiblePosition()-listView.getFirstVisiblePosition()).getBottom())<=listView.getBottom()){
                flag=true;
            }
        }
        return flag;
    }


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
                textHolder.textView.setTypeface(BaseApplication.typeface);
                view.setTag(textHolder);
            } else {
                textHolder = (TextHolder) view.getTag();
            }
            if (list.get(i).toString().contains("Error") || list.get(i).toString().contains("错误") || list.get(i).toString().contains("No Service")) {
                textHolder.textView.setTextColor(Color.parseColor("#ff0000"));
                if(!VIBRATOR_done){
                    ((Vibrator) getSystemService(Service.VIBRATOR_SERVICE)).vibrate(100);
                    VIBRATOR_done=true;
                }
            }
            if(list.get(i).contains(".")) {
                textHolder.textView.setText(list.get(i).substring(0, list.get(i).lastIndexOf(".")));
            }else{
                textHolder.textView.setText(list.get(i));
            }
            return view;
        }
    }

    static class TextHolder {
        TextView textView;
    }
}
