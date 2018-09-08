package sline.com.polaris.tools;

import android.os.Handler;
import android.os.Message;

/**
 * Created by dell on 2018/9/8.
 */

public class MakeMessage {
    private int what,arg1,arg2;
    private Object obj;
    private Handler handler;

    public MakeMessage(int what, int arg1, int arg2, Object obj, Handler handler) {
        this.what = what;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.obj = obj;
        this.handler = handler;
    }

    public boolean makeMessage(){
        boolean flag=false;
        Message message=Message.obtain();
        message.what=what;
        message.arg1=arg1;
        message.arg2=arg2;
        message.obj=obj;
        if(handler.sendMessage(message))
            flag=true;
        return flag;
    }
}
