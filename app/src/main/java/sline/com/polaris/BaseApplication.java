package sline.com.polaris;

import android.app.Application;
import android.graphics.Typeface;
import android.util.Log;

import com.tencent.smtt.sdk.QbSdk;

public class BaseApplication extends Application {
    public static Typeface typeface;

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化X5内核
        QbSdk.initX5Environment(this, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                //x5内核初始化完成回调接口，此接口回调并表示已经加载起来了x5，有可能特殊情况下x5内核加载失败，切换到系统内核。
            }

            @Override
            public void onViewInitFinished(boolean b) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                if(b)
                    Log.i("Tag","加载内核成功");
            }
        });

        typeface=Typeface.createFromAsset(getAssets(),"fonts/polaris.ttf");
    }

}
