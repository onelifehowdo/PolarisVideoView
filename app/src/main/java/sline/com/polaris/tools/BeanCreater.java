package sline.com.polaris.tools;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 2018/9/8.
 */

public class BeanCreater implements Runnable {
    private List<JSONObject> listVideo;
    private Handler handler;
    private int what;

    public BeanCreater(List videoList, Handler handler,int what) {
        this.listVideo = videoList;
        this.handler=handler;
        this.what=what;
    }

    public void run() {
//        list.addAll(creater());
//        Message message = Message.obtain();
//        message.what = 0;
//        handler.sendMessage(message);
        try {
            new MakeMessage(what,0,0,creater(),handler).makeMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private List<VideoBean> creater() throws Exception {
        List<VideoBean> list = new ArrayList<>();
        String nameLeft, nameRight,imageLeft,imageRight;
        Double sizeLeft,sizeRight;
        Long videoTimeLeft,videoTimeRight;
        for(int i=0;i<listVideo.size();i+=2){
            nameLeft=listVideo.get(i).getString("videoName");
            imageLeft=listVideo.get(i).getString("imageName");
            sizeLeft=listVideo.get(i).getDouble("videoSize");
            videoTimeLeft=listVideo.get(i).getLong("downloadTime");
            if(i != listVideo.size() - 1){
                nameRight=listVideo.get(i+1).getString("videoName");
                imageRight=listVideo.get(i+1).getString("imageName");
                sizeRight=listVideo.get(i+1).getDouble("videoSize");
                videoTimeRight=listVideo.get(i+1).getLong("downloadTime");
            }
            else {
                nameRight=null;
                imageRight=null;
                sizeRight=null;
                videoTimeRight=null;
            }
            list.add(new VideoBean(imageLeft,imageRight,nameLeft,nameRight,sizeLeft,sizeRight,videoTimeLeft,videoTimeRight));
        }
//        for (int i = 0; i < listVideo.size(); i += 2) {
//            nameLeft = listVideo.get(i);
//            if (i != listVideo.size() - 1)
//                nameRight = listVideo.get(i + 1);
//            else
//                nameRight = "NO";
//            list.add(new VideoBean(nameLeft, nameRight));
//        }
//        Log.i("Tag", "Bean数量：" + list.size());
        return list;
    }
}
