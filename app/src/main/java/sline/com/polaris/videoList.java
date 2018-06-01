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
    ///////////////////////////////////////////////////////////handeler////////////////////////////////////////////
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0: {
                    myAdapter.notifyDataSetChanged();
                    Log.i("Tag", "更新");
                    break;
                }
                case 1: {
                    String name = (String) msg.obj;

                    if(name.endsWith("vdk1")){
                        Intent intent = new Intent();
                        intent.setClass(videoList.this, VideoPlay.class);
                        intent.putExtra("url", url);
                        intent.putExtra("videoPath", videoPath);
                        intent.putExtra("name", (String) msg.obj);
                        startActivity(intent);
                    }
                    else
                        Toast.makeText(videoList.this, "不支持格式", Toast.LENGTH_SHORT).show();
                    break;
                }
                case 2: {
                    Glide.with(videoList.this).load("http://" + url + doorImagePath + backGround[msg.arg1]).dontAnimate().thumbnail(0.1f).into(backGroundImage);
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
        new Change(listPort).start();
        Glide.with(this).load("http://" + url + doorImagePath + getIntent().getStringExtra("backGround")).thumbnail(0.2f).placeholder(R.mipmap.background).error(R.mipmap.background).into(backGroundImage);
        listView = findViewById(R.id.videoListView);
        myAdapter = new MyAdapter(this, list);
        listView.setAdapter(myAdapter);
        new videoListJSON(url).start();
    }


    ////////////////////////////////////////////内部类/////////////////////////////////////////////////////////////////////////

    ////////////////////////////                    定时更换背景     //////////////////////////////////////////////////////

    class Change extends Thread {
        private int flag, port;

        public Change(int port) {
            this.port = port;
            this.flag = port;
        }

        public void run() {
            change();
        }

        private void change() {
            Message message = Message.obtain();
            message.what = 2;
            message.arg1 = flag;
            handler.sendMessage(message);
        }
    }

    ////////////////////////////////////////////////////////////////       下载JSON       ///////////////////////////////////////////////////////////////

    class videoListJSON extends Thread {
        private String url;
        private List<String> listVideo = new ArrayList<>();

        public videoListJSON(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            listVideo.addAll(getJson(url));
            new BeanCreater(listVideo).start();

        }

        private List<String> getJson(String url) {
            List<String> list = new ArrayList<>();
            String videoJson = "";
            URLConnection urlConnection;
            try {
                urlConnection = new URL("http://" + url + jsonPath + "video.json").openConnection();
                InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream(), "utf-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    videoJson += line;
                }
                inputStreamReader.close();
                bufferedReader.close();
                JSONObject jsonObject = new JSONObject(videoJson);
                JSONArray jsonArray = jsonObject.getJSONArray("video");
                for (int i = 0; i < jsonArray.length(); i++) {
                    list.add(jsonArray.getString(i));
//                    Log.i("Tag", jsonArray.getString(i));
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("Tag", "网络失败");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return list;
        }

    }

    /////////////////////////////////////////////////////////////////////     BeanCreat              //////////////////////////////////////////////////////////////


    class BeanCreater extends Thread {
        List<String> listVideo = new ArrayList<>();

        public BeanCreater(List<String> videoList) {
            this.listVideo = videoList;
        }

        public void run() {
            list.addAll(creater());
            Message message = Message.obtain();
            message.what = 0;
            handler.sendMessage(message);
        }


        private List<VideoBean> creater() {
            List<VideoBean> list = new ArrayList<>();
            String nameLeft, nameRight;
            for (int i = 0; i < listVideo.size(); i += 2) {
                nameLeft = listVideo.get(i);
                if (i != listVideo.size() - 1)
                    nameRight = listVideo.get(i + 1);
                else
                    nameRight = "NO";
                list.add(new VideoBean(nameLeft, nameRight));
            }
            Log.i("Tag", "Bean数量：" + list.size());
            return list;
        }
    }


    //////////////////////////////////////////////////////////////////       适配器    /////////////////////////////////////////////////////
    public class MyAdapter extends BaseAdapter {
        private List<VideoBean> list;
        private LayoutInflater inflater;
        private Context context;

        public MyAdapter(Context context, List<VideoBean> list) {
            this.list = list;
            inflater = LayoutInflater.from(context);
            this.context = context;
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
//            view = inflater.inflate(R.layout.videoitem, null);
//            ImageView il=view.findViewById(R.id.ImageLift);
//            ImageView ir=view.findViewById(R.id.ImageRight);
//            Glide.with(context).load(R.mipmap.go).into(il);
//            Glide.with(context).load(R.mipmap.go).into(ir);
            final ViewHolder viewHolder;
            if (view == null) {
                viewHolder = new ViewHolder();
                view = inflater.inflate(R.layout.videoitem, null);
                viewHolder.imgLeft = view.findViewById(R.id.ImageLift);
                viewHolder.imgRight = view.findViewById(R.id.ImageRight);
                viewHolder.tvLeft = view.findViewById(R.id.TextLeft);
                viewHolder.tvRight = view.findViewById(R.id.TextRight);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }


            final VideoBean bean = list.get(i);
            Glide.with(context).load("http://" + url + imagePath + bean.getImageLeft()).into(viewHolder.imgLeft);
            //viewHolder.imgLeft.setImageResource(R.mipmap.test);
            viewHolder.tvLeft.setVisibility(View.INVISIBLE);
            viewHolder.tvLeft.setText(bean.getNameLeft().substring(0, bean.getNameLeft().lastIndexOf(".")));
            if (!"NO".equals(bean.getNameRight())) {
                Glide.with(context).load("http://" + url + imagePath + bean.getImageRight()).into(viewHolder.imgRight);
                //viewHolder.imgRight.setImageResource(R.mipmap.test);
                viewHolder.tvRight.setVisibility(View.INVISIBLE);
                viewHolder.tvRight.setText(bean.getNameRight().substring(0, bean.getNameRight().lastIndexOf(".")));
            } else {
                viewHolder.imgRight.setImageBitmap(null);
                viewHolder.tvRight.setText("");
            }


            viewHolder.imgLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (viewHolder.tvLeft.getVisibility() == View.VISIBLE)
                        viewHolder.tvLeft.setVisibility(View.INVISIBLE);
                    else {
                        viewHolder.tvLeft.setVisibility(View.VISIBLE);
                        viewHolder.tvLeft.startAnimation(AnimationUtils.loadAnimation(viewHolder.tvLeft.getContext(), R.anim.clickon));
                    }
                }
            });

            viewHolder.imgRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (viewHolder.tvRight.getVisibility() == View.VISIBLE)
                        viewHolder.tvRight.setVisibility(View.INVISIBLE);
                    else {
                        viewHolder.tvRight.setVisibility(View.VISIBLE);
                        viewHolder.tvRight.startAnimation(AnimationUtils.loadAnimation(viewHolder.tvRight.getContext(), R.anim.clickon));
                    }
                }
            });

            viewHolder.tvLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Message message = Message.obtain();
                    message.what = 1;
                    message.obj = bean.getNameLeft();
                    handler.sendMessage(message);
                }
            });

            viewHolder.tvRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!"NO".equals(bean.getNameRight())) {
                        Message message = Message.obtain();
                        message.what = 1;
                        message.obj = bean.getNameRight();
                        handler.sendMessage(message);
                    }
                }
            });

            return view;
        }
    }

    static class ViewHolder {
        ImageView imgLeft;
        ImageView imgRight;
        TextView tvLeft;
        TextView tvRight;
    }
}

