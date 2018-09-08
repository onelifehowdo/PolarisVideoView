package sline.com.polaris;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

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

import jp.wasabeef.glide.transformations.BlurTransformation;
import sline.com.polaris.tools.BeanCreater;
import sline.com.polaris.tools.DownLoadJson;
import sline.com.polaris.tools.EMS;
import sline.com.polaris.tools.MakeMessage;
import sline.com.polaris.tools.MyAdapter;
import sline.com.polaris.tools.VideoBean;

public class videoList extends AppCompatActivity {
    private List<VideoBean> list = new ArrayList<>();
    private String url;
    private String videoPath;
    private String imagePath;
    private String doorImagePath;
    private String jsonPath;
    private ListView listView;
    private MyAdapter myAdapter;
    private String[] backGround;
    private ImageView backGroundImage;

    private final int GET_JSON_SUCCEED = 0, GET_JSON_FAIL = 1, BEAN_DONE = 2, ITEM_CLICK = 3;
    ///////////////////////////////////////////////////////////handeler////////////////////////////////////////////
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BEAN_DONE: {
                    list.addAll((List<VideoBean>) msg.obj);
                    myAdapter.notifyDataSetChanged();
                    break;
                }
                case ITEM_CLICK: {
                    IntentSkip((EMS) msg.obj);
                    break;
                }
                case GET_JSON_SUCCEED: {
                    new Thread(new BeanCreater((List) msg.obj, handler, BEAN_DONE)).start();
                    break;
                }
                case GET_JSON_FAIL: {
                    Toast.makeText(videoList.this, "网络错误", Toast.LENGTH_SHORT).show();
                    break;
                }
                default:
                    break;
            }
        }
    };


    /////////////////////////////////////////////////////////////////          Activity                ////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_video_list);
        url = getIntent().getStringExtra("url");
        imagePath = getIntent().getStringExtra("imagePath");
        videoPath = getIntent().getStringExtra("videoPath");
        jsonPath = getIntent().getStringExtra("jsonPath");
        doorImagePath = getIntent().getStringExtra("doorImagePath");
        backGround = getIntent().getStringArrayExtra("doorList");
        backGroundImage = findViewById(R.id.backGround);
        int listPort = (int) (Math.random() * backGround.length);
        Glide.with(this).load("http://" + url + doorImagePath + backGround[listPort]).bitmapTransform(new BlurTransformation(this, 10)).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.RESULT).placeholder(null).error(R.mipmap.background).into(backGroundImage);
        listView = findViewById(R.id.videoListView);
        myAdapter = new MyAdapter(this, list, url, imagePath, ITEM_CLICK, handler);
        listView.setAdapter(myAdapter);
        new Thread(new DownLoadJson("http://" + url + jsonPath + "video.json", "video", handler, GET_JSON_SUCCEED, GET_JSON_FAIL)).start();
    }

    private void IntentSkip(EMS ems){
        Intent intent = new Intent();
        intent.setClass(videoList.this, VideoPlay_X5.class);
        intent.putExtra("url", url);
        intent.putExtra("videoPath", videoPath);
        intent.putExtra("imagePath", imagePath);
        intent.putExtra("video_name", ems.getVideo());
        intent.putExtra("image_name", ems.getImage());
        startActivity(intent);
    }

}

