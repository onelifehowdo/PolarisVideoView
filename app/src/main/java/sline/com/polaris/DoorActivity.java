package sline.com.polaris;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sline.com.polaris.tools.DownLoadJson;
import sline.com.polaris.tools.ImageCache;
import sline.com.polaris.tools.MakeMessage;

public class DoorActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, View.OnTouchListener {

    private String url;
    private String doorImagePath = "/web/image/doorimage/";
    private String jsonPath = "/web/js/";
    private List<String> doorList = new ArrayList();
    private ImageView DoorImage, switcher, delCache, index, cloud, setting, updata, shutdown, restart, cancel, returndata;
    private RelativeLayout firstLayout, doorLayout;
    private LinearLayout toolsBar;
    private Animation animationIcon, animationOpen, animationClose,
            animationCloud, animationSettingOpen, animationSettingClose, animationOpenDoor, animationInput;
    private EditText input;
    private int DoorImageSize, drawablePort = 1, downPort = 0;
    private boolean softInput = false, skipActivity = false;
    private ProgressBar progressBar;
    static boolean reading = false;
    private long date, lastBackTime;
    private ImageCache imageCache;
    private Typeface typeface;
    private static final int MAKE_TOAST = 0, GET_JSON_SUCCEED = 1, GET_JSON_FAIL = 2, GET_FirstIMAGE_SUCCEED = 3, GET_IMAGE_FAIL = 4, CHANGE_IMG = 5;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MAKE_TOAST: {
                    Toast.makeText(DoorActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                }
                case GET_JSON_SUCCEED: {
                    initDoorLayout(msg);
                    break;
                }
                case GET_JSON_FAIL: {
                    if (input.getText().toString().equals("Linking...")) {
                        ((Vibrator) getSystemService(Service.VIBRATOR_SERVICE)).vibrate(100);
                        input.setText("Not Found");
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    break;
                }
                case GET_FirstIMAGE_SUCCEED: {
                    if(input.getText().toString().equals("Linking...")){//判断是否还在连接状态
                        DoorImage.setImageDrawable(new getImage().readImage(imageCache.imageFile[0]));
                        input.setText("");
                        doorLayout.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                        firstLayout.setVisibility(View.GONE);
                    }
                    break;
                }
                case GET_IMAGE_FAIL: {
                    drawablePort++;
                    drawablePort = drawablePort % imageCache.length;
                    break;
                }

                case CHANGE_IMG: {
                    DoorImage.setImageDrawable((Drawable) msg.obj);
                    break;
                }
            }
        }
    };


    @Override
    protected void onStop() {
        if (skipActivity) {
            skipActivity = false;
            input.setText("");
            progressBar.setVisibility(View.INVISIBLE);
            firstLayout.setVisibility(View.VISIBLE);
            doorLayout.setVisibility(View.GONE);
        }

        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.DoorLater);
        System.gc();
        setContentView(R.layout.dooractivity);
        index = findViewById(R.id.indexImage);
        index.setImageResource(R.mipmap.index);

        animationCloud = AnimationUtils.loadAnimation(this, R.anim.cloud);
        animationOpenDoor = AnimationUtils.loadAnimation(this, R.anim.opendooranim);
        animationOpenDoor.setFillAfter(true);
        animationInput = AnimationUtils.loadAnimation(this, R.anim.inputalpha);
        animationInput.setFillAfter(true);

        DoorImage = findViewById(R.id.doorImage);
        index.startAnimation(animationOpenDoor);
        cloud = findViewById(R.id.cloud);
        cloud.startAnimation(animationCloud);
        input = findViewById(R.id.input);
        typeface=BaseApplication.typeface;
        input.setTypeface(typeface);
        progressBar = findViewById(R.id.progressbar);
        firstLayout = findViewById(R.id.firstLayout);
        doorLayout = findViewById(R.id.doorLayout);
        date = System.currentTimeMillis();


        index.setOnLongClickListener(this);
        index.setOnTouchListener(this);
        input.setLongClickable(false);
        input.setOnClickListener(this);


        //动画监听
        animationCloud.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cloud.setVisibility(View.GONE);
                input.setVisibility(View.VISIBLE);
                input.setAnimation(animationInput);
                animationCloud.setAnimationListener(null);
                cloud = null;
                animationCloud = null;
                System.gc();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animationOpenDoor.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animationOpenDoor.setAnimationListener(null);
                animationOpenDoor = null;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animationInput.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animationInput.setAnimationListener(null);
                animationInput = null;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }


    ////////////////////////////////////  点击事件  ////////////////////////////////////

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.switcher: {
                if ((System.currentTimeMillis() - date) > 400 && !reading) {
                    if (toolsBar.getVisibility()==View.VISIBLE) {
                        setting.startAnimation(animationSettingClose);
                        toolsBar.startAnimation(animationClose);
                        System.gc();
                    }
                    if (imageCache.imageCanUse[drawablePort]) {
                        changeImage(drawablePort, downPort);
                    } else {
                        Toast.makeText(DoorActivity.this, "正在加载", Toast.LENGTH_SHORT).show();
                    }
                    date = System.currentTimeMillis();
                }
                break;
            }
            case R.id.input: {
                if (input.getText().toString().equals("Not Found") || input.getText().toString().equals("Linking...")) {
                    input.setText("");
                    progressBar.setVisibility(View.INVISIBLE);
                }
                softInput = true;
                break;
            }
            case R.id.setting: {
                    if (toolsBar.getVisibility()==View.VISIBLE) {
                        setting.startAnimation(animationSettingClose);
                        toolsBar.startAnimation(animationClose);
                    } else if (toolsBar.getVisibility()==View.GONE) {
                        setting.startAnimation(animationSettingOpen);
                        toolsBar.startAnimation(animationOpen);
                    }
                break;
            }
            default:
                break;

        }
    }


    @Override
    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case R.id.switcher: {
                if (toolsBar.getVisibility()==View.VISIBLE) {
                    setting.startAnimation(animationSettingClose);
                    toolsBar.startAnimation(animationClose);
                }
                Intent intent = new Intent(DoorActivity.this, videoList.class);
                Bundle bundle = new Bundle();
                bundle.putString("jsonPath", jsonPath);
                bundle.putString("doorImagePath", doorImagePath);
                bundle.putStringArray("doorList", doorList.toArray(new String[doorList.size()]));
                intent.putExtra("data", bundle);
                startActivity(intent);
                skipActivity = true;
                emptyDoorLayout();
                break;
            }
            case R.id.delete: {
                new Thread(new delCach()).start();
                break;
            }
            case R.id.indexImage: {
                String temp = input.getText().toString();
                if (temp.equals("Not Found") || temp.equals("Linking...") || temp.equals("")) {
                    break;
                }
                progressBar.setVisibility(View.VISIBLE);
                int flag = ip_test(temp);
                if (flag == 0) {
                    BaseApplication.url = temp;
                    url=BaseApplication.url;
                    new Thread(new DownLoadJson("http://" + BaseApplication.url + jsonPath + "doorImage.json", "getDoorImage", handler, GET_JSON_SUCCEED, GET_JSON_FAIL)).start();
                    input.setText("Linking...");
                } else if (flag == 1) {
                    Intent intent = new Intent(DoorActivity.this, WebPage.class);
                    intent.putExtra("url", "http://" + temp);
                    skipActivity = true;
                    startActivity(intent);
                } else if (flag == 2) {
                    Intent intent = new Intent(DoorActivity.this, WebPage.class);
                    intent.putExtra("url", "https://m.baidu.com/s?from=1086k&tn=baidulocal&word=" + temp);
                    skipActivity = true;
                    startActivity(intent);
                }
                break;
            }
            case R.id.updata: {
                Intent intent = new Intent(DoorActivity.this, ActionActivity.class);
                intent.putExtra("chose", "updata");
                startActivity(intent);
                break;
            }
            case R.id.shutdown: {
                Intent intent = new Intent(DoorActivity.this, ActionActivity.class);
                intent.putExtra("chose", "shutdown");
                startActivity(intent);
                break;
            }
            case R.id.restart: {
                Intent intent = new Intent(DoorActivity.this, ActionActivity.class);
                intent.putExtra("chose", "restart");
                startActivity(intent);
                break;
            }
            case R.id.cancel: {
                Intent intent = new Intent(DoorActivity.this, ActionActivity.class);
                intent.putExtra("chose", "cancel");
                startActivity(intent);
                break;
            }
            case R.id.returndata: {
                Intent intent = new Intent(DoorActivity.this, ActionActivity.class);
                intent.putExtra("chose", "return");
                startActivity(intent);
                break;
            }
        }

        return true;
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()) {
            case R.id.indexImage: {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(getApplication().INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(input.getWindowToken(), 0);
                softInput = false;
                break;
            }
            case R.id.doorImage: {
                if (toolsBar.getVisibility()==View.VISIBLE) {
                    setting.startAnimation(animationSettingClose);
                    toolsBar.startAnimation(animationClose);
                }
                break;
            }

        }
        return false;
    }


    @Override
    public void onBackPressed() {
        softInput = false;
        if (firstLayout.getVisibility() == View.GONE) {
            emptyDoorLayout();
            firstLayout.setVisibility(View.VISIBLE);
            doorLayout.setVisibility(View.GONE);
            return;
        }
        if (!softInput && System.currentTimeMillis() - lastBackTime > 2000) {
            lastBackTime = System.currentTimeMillis();
            Toast toast = Toast.makeText(DoorActivity.this, "再按一次退出", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }
        super.onBackPressed();
    }

    ////////////////////////////////////  内部类  ////////////////////////////////////

    class delCach implements Runnable {
        @Override
        public void run() {
            delcache();
        }

        private void delcache() {
            try {
                File cache = new File(getCacheDir().toString() + "/image_manager_disk_cache");
                String[] list = cache.list();
                for (int i = 0; i < list.length; i++) {
                    new File(getCacheDir().toString() + "/image_manager_disk_cache/" + list[i].toString()).delete();
                }
                new MakeMessage(MAKE_TOAST, 0, 0, new String("清除" + list.length + "个文件"), handler).makeMessage();
            } catch (NullPointerException e) {
                new MakeMessage(MAKE_TOAST, 0, 0, new String("清除成功"), handler).makeMessage();
            }
        }
    }//删除缓存

    class DoorImageDown implements Runnable {

        private String url;
        private int bitMapNum;


        public DoorImageDown() {

        }

        public DoorImageDown(String url, int bitMapNum) {
            this.url = url;
            this.bitMapNum = bitMapNum;
        }


        @Override
        public void run() {
            try {
                imageCache.imageCanUse[bitMapNum] = false;
                downImage();
                if (firstLayout.getVisibility() == View.VISIBLE && bitMapNum == 0) {
                    new MakeMessage(GET_FirstIMAGE_SUCCEED, 0, 0, null, handler).makeMessage();
                }
                Log.i("Tag", "下载：" + url);

            } catch (Exception e) {
                e.printStackTrace();
                new MakeMessage(GET_IMAGE_FAIL, 0, 0, null, handler).makeMessage();
            } finally {
                if (imageCache != null) {
                    imageCache.imageCanUse[bitMapNum] = true;
                    downPort++;
                    downPort = downPort % DoorImageSize;
                }
            }
        }

        private void downImage() throws Exception {
            HttpURLConnection connection = null;
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            try {
                connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setConnectTimeout(2000);
                bis = new BufferedInputStream(connection.getInputStream());
                bos = new BufferedOutputStream(new FileOutputStream(imageCache.imageFile[bitMapNum]));
                int temp;
                while ((temp = bis.read()) != -1)
                    bos.write(temp);
            } catch (Exception e) {
                throw e;
            } finally {
                if (bis != null)
                    bis.close();
                if (bos != null)
                    bos.close();
                connection.disconnect();
            }

        }

    }//下载图片

    class getImage implements Runnable {
        int Drawableport;

        public getImage() {
        }

        public getImage(int Drawableport) {
            this.Drawableport = Drawableport;
        }

        @Override
        public void run() {
            reading = true;
            Drawable drawable;
            drawable = readImage(imageCache.imageFile[Drawableport]);
            new MakeMessage(CHANGE_IMG, 0, 0, drawable, handler).makeMessage();
            reading = false;
        }

        private Drawable readImage(File file) {
            Drawable drawable = null;
            BufferedInputStream bis = null;
            try {
                bis = new BufferedInputStream(new FileInputStream(file));
                drawable = new BitmapDrawable(getResources(), BitmapFactory.decodeStream(bis));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (bis != null)
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                return drawable;
            }
        }
    }//读取本地图片


    ////////////////////////////////////  内部方法  ////////////////////////////////////
    private int ip_test(String a) {
        String regex_link = "(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(:([0-9]|[1-9]\\d{1,3}|[1-5]\\d{4}|6[0-4]\\d{4}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5]))?/";
        String regex_ip = "(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(:([0-9]|[1-9]\\d{1,3}|[1-5]\\d{4}|6[0-4]\\d{4}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5]))?";
        Pattern pattern = Pattern.compile(regex_link);
        Matcher matcher = pattern.matcher(a);
        if (a.matches(regex_ip))
            return 0;
        else if (matcher.lookingAt())
            return 1;
        else
            return 2;
    } // IP 状态测试


    private void changeImage(int DrawablePort, int downPort) {//更换图片

        new Thread(new getImage(DrawablePort)).start();
        drawablePort++;
        drawablePort = drawablePort % imageCache.length;
        int testPort = (drawablePort + imageCache.length - 2) % imageCache.length;
        new Thread(new DoorImageDown("http://" + url + doorImagePath + doorList.get(downPort), testPort)).start();
    }

    private List<String> decodeDoorImageJson(String json) throws JSONException {
        List<String> jsonList=new ArrayList<>();
        JSONArray jsonArray = new JSONArray(json);
        for (int i = 0; i < jsonArray.length(); i++) {
            jsonList.add(jsonArray.getString(i));
        }
        for (int i = 0; i < jsonList.size() - 1; i++) {
            int index;
            String temp;
            index = (int) (Math.random() * (jsonList.size() - i)) + i;
            temp = jsonList.get(i);
            jsonList.set(i, jsonList.get(index));
            jsonList.set(index, temp);
        }
        return jsonList;
    }
    private void initDoorLayout(Message msg) {
        imageCache = new ImageCache(getApplicationContext(), 8);
        try {
            doorList.addAll(decodeDoorImageJson((String) msg.obj));
        } catch (JSONException e) {
            e.printStackTrace();
            new MakeMessage(GET_JSON_FAIL, 0, 0, null, handler).makeMessage();//网络失败改变文字
            return;
        }
        DoorImageSize = doorList.size();
        for (int i = 0; i < imageCache.length; i++) {
            new Thread(new DoorImageDown("http://" + url+ doorImagePath + doorList.get(i), i)).start();
        }
        animationOpen = AnimationUtils.loadAnimation(this, R.anim.toolsbaropen);
        animationClose = AnimationUtils.loadAnimation(this, R.anim.toolsbarclose);
        animationSettingOpen = AnimationUtils.loadAnimation(this, R.anim.setting_rotate_open);
        animationSettingClose = AnimationUtils.loadAnimation(this, R.anim.setting_rotate_close);
        animationIcon = AnimationUtils.loadAnimation(this, R.anim.animation);
        animationClose.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                toolsBar.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animationOpen.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                toolsBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        switcher = findViewById(R.id.switcher);
        switcher.startAnimation(animationIcon);
        delCache = findViewById(R.id.delete);
        setting = findViewById(R.id.setting);
        toolsBar = findViewById(R.id.toolsBar);
        updata = findViewById(R.id.updata);
        shutdown = findViewById(R.id.shutdown);
        restart = findViewById(R.id.restart);
        cancel = findViewById(R.id.cancel);
        returndata = findViewById(R.id.returndata);
        lastBackTime = date;
        switcher.setOnClickListener(this);
        setting.setOnClickListener(this);
        DoorImage.setOnTouchListener(this);
        switcher.setOnLongClickListener(this);
        delCache.setOnLongClickListener(this);
        updata.setOnLongClickListener(this);
        shutdown.setOnLongClickListener(this);
        restart.setOnLongClickListener(this);
        cancel.setOnLongClickListener(this);
        returndata.setOnLongClickListener(this);
    }

    private void emptyDoorLayout() {
        if (toolsBar.getVisibility()==View.VISIBLE) {
            toolsBar.setVisibility(View.GONE);
        }
        imageCache = null;
        drawablePort = 1;
        downPort = 0;
        softInput = false;
        doorList.clear();
        animationClose.setAnimationListener(null);
        animationOpen.setAnimationListener(null);
        animationOpen = null;
        animationClose = null;
        animationSettingOpen = null;
        animationSettingClose = null;
        animationIcon = null;

        switcher.setOnClickListener(null);
        setting.setOnClickListener(null);
        DoorImage.setOnTouchListener(null);
        switcher.setOnLongClickListener(null);
        delCache.setOnLongClickListener(null);
        updata.setOnLongClickListener(null);
        shutdown.setOnLongClickListener(null);
        restart.setOnLongClickListener(null);
        cancel.setOnLongClickListener(null);
        returndata.setOnLongClickListener(null);
        switcher = null;
        delCache = null;
        setting = null;
        toolsBar = null;
        updata = null;
        shutdown = null;
        restart = null;
        cancel = null;
        returndata = null;
        System.gc();
    }

}

