package sline.com.polaris;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sline.com.polaris.tools.DownLoadJson;
import sline.com.polaris.tools.MakeMessage;

public class DoorActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, View.OnTouchListener {

    private String url;
    private String videoPath = "/web/video/";
    private String imagePath = "/web/image/videoimage/";
    private String doorImagePath = "/web/image/doorimage/";
    private String jsonPath = "/web/js/";
    private List<String> doorList = new ArrayList<String>();
    private ImageView DoorImage, switcher, delCache, index, cloud, setting, updata, shutdown, restart, cancel, returndata;
    private RelativeLayout firstLayout, doorLayout;
    private LinearLayout toolsBar;
    private Animation animationIcon, animationOpen, animationClose,
            animationCloud, animationSettingOpen, animationSettingClose, animationOpenDoor, animationInput;
    private EditText input;
    private int DoorImageSize, drawablePort = 1, downPort = 0, flag = 2;
    private boolean softInput = false;
    private ProgressBar progressBar;
    private Drawable[] drawables;
    private boolean[] bitMapLock;
    private long date, lastBackTime;
    private static final int MAKE_TOAST = 0, GET_JSON_SUCCEED = 1, GET_JSON_FAIL = 2, GET_FirstIMAGE_SUCCEED = 3, GET_IMAGE_FAIL = 4;

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
                    iniDoorLayout(msg);
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
                    if(drawables!=null){
                        DoorImage.setImageDrawable(drawables[0]);
                        input.setText("");
                        doorLayout.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                        firstLayout.setVisibility(View.GONE);
                    }
                    break;
                }
                case GET_IMAGE_FAIL: {
                    drawablePort++;
                    drawablePort = drawablePort % drawables.length;
                    break;
                }
            }
        }
    };


    @Override
    protected void onPause() {
        input.setText("");
        progressBar.setVisibility(View.INVISIBLE);
        super.onPause();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.DoorLater);
        System.gc();
        setContentView(R.layout.dooractivity);

        animationCloud = AnimationUtils.loadAnimation(this, R.anim.cloud);
        animationOpenDoor = AnimationUtils.loadAnimation(this, R.anim.opendooranim);
        animationOpenDoor.setFillAfter(true);
        animationInput = AnimationUtils.loadAnimation(this, R.anim.inputalpha);
        animationInput.setFillAfter(true);

        DoorImage = findViewById(R.id.doorImage);
        index = findViewById(R.id.indexImage);
        index.startAnimation(animationOpenDoor);
        cloud = findViewById(R.id.cloud);
        cloud.startAnimation(animationCloud);
        input = findViewById(R.id.input);
        progressBar = findViewById(R.id.progressbar);
        firstLayout = findViewById(R.id.firstLayout);
        doorLayout = findViewById(R.id.doorLayout);
        date = System.currentTimeMillis();


        index.setOnLongClickListener(this);
        index.setOnTouchListener(this);
        input.setLongClickable(false);
        input.setOnClickListener(DoorActivity.this);


        //动画
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
                if ((System.currentTimeMillis() - date) > 400) {
                    if (flag == 1) {
                        setting.startAnimation(animationSettingClose);
                        toolsBar.startAnimation(animationClose);
                        System.gc();
                    }
                    if (bitMapLock[drawablePort]) {
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
                if (flag == 1) {
                    setting.startAnimation(animationSettingClose);
                    toolsBar.startAnimation(animationClose);
                } else if (flag == 2) {
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
                if (flag == 1) {
                    toolsBar.startAnimation(animationClose);
                }
                Intent intent = new Intent(DoorActivity.this, videoList.class);
                Bundle bundle = new Bundle();
                bundle.putString("url", url);
                bundle.putString("imagePath", imagePath);
                bundle.putString("videoPath", videoPath);
                bundle.putString("jsonPath", jsonPath);
                bundle.putString("doorImagePath", doorImagePath);
                bundle.putStringArray("doorList", doorList.toArray(new String[doorList.size()]));
                intent.putExtra("data", bundle);
                startActivity(intent);
                emptyDoorLayout();
                firstLayout.setVisibility(View.VISIBLE);
                doorLayout.setVisibility(View.GONE);
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
                    url = temp;
                    new Thread(new DownLoadJson("http://" + url + jsonPath + "doorImage.json", "doorImage", "GET", handler, GET_JSON_SUCCEED, GET_JSON_FAIL)).start();
                    input.setText("Linking...");
                } else if (flag == 1) {
                    Intent intent = new Intent(DoorActivity.this, WebPage.class);
                    intent.putExtra("url", "http://" + temp);
                    startActivity(intent);
                } else if (flag == 2) {
                    Intent intent = new Intent(DoorActivity.this, WebPage.class);
                    intent.putExtra("url", "https://m.baidu.com/s?from=1086k&tn=baidulocal&word=" + temp);
                    startActivity(intent);
                }
                break;
            }
            case R.id.updata: {
                Intent intent = new Intent(DoorActivity.this, ActionActivity.class);
                intent.putExtra("url", url);
                intent.putExtra("chose", "updata");
                startActivity(intent);
                break;
            }
            case R.id.shutdown: {
                Intent intent = new Intent(DoorActivity.this, ActionActivity.class);
                intent.putExtra("url", url);
                intent.putExtra("chose", "shutdown");
                startActivity(intent);
                break;
            }
            case R.id.restart: {
                Intent intent = new Intent(DoorActivity.this, ActionActivity.class);
                intent.putExtra("url", url);
                intent.putExtra("chose", "restart");
                startActivity(intent);
                break;
            }
            case R.id.cancel: {
                Intent intent = new Intent(DoorActivity.this, ActionActivity.class);
                intent.putExtra("url", url);
                intent.putExtra("chose", "cancel");
                startActivity(intent);
                break;
            }
            case R.id.returndata: {
                Intent intent = new Intent(DoorActivity.this, ActionActivity.class);
                intent.putExtra("url", url);
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
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(input.getWindowToken(), 0);
                softInput = false;
                break;
            }
            case R.id.doorImage: {
                if (flag == 1) {
                    setting.startAnimation(animationSettingClose);
                    toolsBar.startAnimation(animationClose);
                }
                break;
            }

        }

        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && (firstLayout.getVisibility() == View.GONE)) {
            emptyDoorLayout();
            firstLayout.setVisibility(View.VISIBLE);
            doorLayout.setVisibility(View.GONE);
            return true;
        }
        if (!softInput && System.currentTimeMillis() - lastBackTime > 1000) {
            lastBackTime = System.currentTimeMillis();
            Toast toast = Toast.makeText(DoorActivity.this, "再按一次退出", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
            bitMapLock[bitMapNum] = false;
            drawables[bitMapNum] = null;
            try {
                if ((drawables[bitMapNum] = downImage()) != null) {
                    if (firstLayout.getVisibility() == View.VISIBLE && bitMapNum == 0) {
                        new MakeMessage(GET_FirstIMAGE_SUCCEED, 0, 0, null, handler).makeMessage();
                    }
                    Log.i("Tag", "完成下载Drawable" + "[" + bitMapNum + "]：" + url);
                }

            } catch (Exception e) {
                e.printStackTrace();
                new MakeMessage(GET_IMAGE_FAIL, 0, 0, null, handler).makeMessage();
            } finally {
                if (bitMapLock != null) {
                    bitMapLock[bitMapNum] = true;
                    downPort++;
                    downPort = downPort % DoorImageSize;
                }
            }
        }

        private Drawable downImage() throws Exception {
            Drawable drawable = null;
            HttpURLConnection connection = null;
            BufferedInputStream bis = null;
            try {
                connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setConnectTimeout(2000);
                bis = new BufferedInputStream(connection.getInputStream());
                if (drawables != null)
                    drawable = new BitmapDrawable(getResources(), BitmapFactory.decodeStream(bis));
                return drawable;
            } catch (Exception e) {
                throw e;
            } finally {
                if (bis != null)
                    bis.close();
                connection.disconnect();
            }

        }

    }//下载图片

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

        DoorImage.setImageDrawable(drawables[DrawablePort]);
        Log.i("Tag", "使用Drawable----[" + DrawablePort + "]");
        drawablePort++;
        drawablePort = drawablePort % drawables.length;
        int testPort = (drawablePort + drawables.length - 2) % drawables.length;
        new Thread(new DoorImageDown("http://" + url + doorImagePath + doorList.get(downPort).toString(), testPort)).start();
    }

    private void iniDoorLayout(Message msg) {
        drawables = new Drawable[4];
        bitMapLock = new boolean[drawables.length];
        doorList.addAll((List<String>) msg.obj);
        DoorImageSize = ((List) msg.obj).size();
        for (int i = 0; i < drawables.length; i++) {
            new Thread(new DoorImageDown("http://" + url + doorImagePath + ((List) msg.obj).get(i).toString(), i)).start();
        }
        animationOpen = AnimationUtils.loadAnimation(this, R.anim.toolsbaropen);
        animationClose = AnimationUtils.loadAnimation(this, R.anim.toolsbarclose);
        animationSettingOpen = AnimationUtils.loadAnimation(this, R.anim.setting_rotate_open);
        animationSettingClose = AnimationUtils.loadAnimation(this, R.anim.setting_rotate_close);
        animationIcon = AnimationUtils.loadAnimation(this, R.anim.animation);
        animationClose.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                flag = 3;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                toolsBar.setVisibility(View.GONE);
                flag = 2;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animationOpen.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                flag = 3;
                toolsBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                flag = 1;
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
        drawablePort = 1;
        downPort = 0;
        drawables = null;
        bitMapLock = null;
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

