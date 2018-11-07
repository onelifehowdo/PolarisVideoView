package sline.com.polaris.tools;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by dell on 2018/9/22.
 */

public class ImageCache {
    public int length;
    public File[] imageFile;
    public boolean[] imageCanUse;
    private Context context;

    public ImageCache(Context context,int length) {
        this.length = length;
        this.context= context;
        this.imageFile=new File[length];
        this.imageCanUse=new boolean[length];
        File dir=new File(this.context.getCacheDir().toString()+"/imageCache");
        if(!dir.exists()){
            dir.mkdir();
        }
        delcache();
        for(int i=0;i<length;i++){
            imageCanUse[i]=false;
            imageFile[i]=new File(this.context.getCacheDir().toString()+"/imageCache"+"/image"+i+".cache");
        }
    }

    private void delcache() {
        try {
            File cache = new File(this.context.getCacheDir().toString()+"/imageCache");
            String[] list = cache.list();
            for (int i = 0; i < list.length; i++) {
                new File(this.context.getCacheDir().toString()+"/imageCache/" + list[i]).delete();
            }
        } catch (NullPointerException e) {
           Log.i("tag","没有缓存清理");
        }
    }
}
