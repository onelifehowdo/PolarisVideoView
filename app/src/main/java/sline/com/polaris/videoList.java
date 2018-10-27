package sline.com.polaris;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

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
    private Bundle bundle;
    private VideoPlayFragment videoPlayFragment;
    private SimpleTarget<GlideDrawable> backgroundImageDrawable;
    private RelativeLayout wait;
    private Long lastBackTime;

    private static final int GET_JSON_SUCCEED = 0, GET_JSON_FAIL = 1, BEAN_DONE = 2, ITEM_CLICK = 3;

    /////////////////////////////////////////////////////////////////          Activity                ////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_video_list);
        bundle = getIntent().getBundleExtra("data");
        initview(bundle);
    }

    private void initview(Bundle bundle) {
        lastBackTime=System.currentTimeMillis();
        url = bundle.getString("url");
        imagePath = bundle.getString("imagePath");
        videoPath = bundle.getString("videoPath");
        jsonPath = bundle.getString("jsonPath");
        doorImagePath = bundle.getString("doorImagePath");
        backGround = bundle.getStringArray("doorList");
        backGroundImage = findViewById(R.id.backGround);
        int listPort = (int) (Math.random() * backGround.length);
        listView = findViewById(R.id.videoListView);
        wait=findViewById(R.id.loading);
        ((TextView)findViewById(R.id.loadingtext)).setTypeface(BaseApplication.typeface);
        backgroundImageDrawable = new MySimpleTarget<GlideDrawable>(backGroundImage, listView);
        Glide.with(this)
                .load("http://" + url + doorImagePath + backGround[listPort])
                .bitmapTransform(new BlurTransformation(this, 40))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(null)
                .error(R.mipmap.background)
                .into(backgroundImageDrawable);
        myAdapter = new MyAdapter(this, list, url, imagePath, ITEM_CLICK, handler);
        listView.setAdapter(myAdapter);
        new Thread(new DownLoadJson("http://" + url + jsonPath + "video.json", "getVideo",handler, GET_JSON_SUCCEED, GET_JSON_FAIL)).start();
    }

    private void IntentSkip(EMS ems) {
        wait.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
        backGroundImage.setVisibility(View.GONE);
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("videoPath", videoPath);
        bundle.putString("imagePath", imagePath);
        bundle.putString("videoName", ems.getVideo());
        bundle.putString("imageName", ems.getImage());
        bundle.putDouble("videoSize", ems.getSize());
        bundle.putLong("downloadTime",ems.getDownloadTime());
        videoPlayFragment = new VideoPlayFragment();
        videoPlayFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.showFragment, videoPlayFragment).commit();
    }


    @Override
    public void onBackPressed() {
        if(videoPlayFragment != null){
            wait.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            backGroundImage.setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction().remove(videoPlayFragment).commit();
            videoPlayFragment = null;
            System.gc();
            return;
        }
        if (System.currentTimeMillis() - lastBackTime > 2000) {
            lastBackTime = System.currentTimeMillis();
            Toast toast = Toast.makeText(videoList.this, "再按一次退出", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }
        super.onBackPressed();
    }



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

    class MySimpleTarget<GlideDrawable> extends SimpleTarget {
        private ImageView imageView;
        private View view;

        public MySimpleTarget(ImageView imageView, View view) {
            this.imageView = imageView;
            this.view = view;
        }

        @Override
        public void onResourceReady(Object resource, GlideAnimation glideAnimation) {
            wait.setVisibility(View.GONE);
            imageView.setImageDrawable((Drawable) resource);
            view.setVisibility(View.VISIBLE);
        }
    }
}

