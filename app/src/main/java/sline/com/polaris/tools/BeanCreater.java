package sline.com.polaris.tools;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 2018/9/8.
 */

public class BeanCreater implements Runnable {
    private List<String> listVideo = new ArrayList<>();
    private Handler handler;
    private int what;

    public BeanCreater(List<String> videoList, Handler handler,int what) {
        this.listVideo = videoList;
        this.handler=handler;
        this.what=what;
    }

    public void run() {
//        list.addAll(creater());
//        Message message = Message.obtain();
//        message.what = 0;
//        handler.sendMessage(message);
        new MakeMessage(what,0,0,creater(),handler).makeMessage();
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
//        Log.i("Tag", "Bean数量：" + list.size());
        return list;
    }
}
