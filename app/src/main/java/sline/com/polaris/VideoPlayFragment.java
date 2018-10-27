package sline.com.polaris;


import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import java.text.SimpleDateFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import jp.wasabeef.glide.transformations.BlurTransformation;
import sline.com.polaris.utils.X5WebView;



public class VideoPlayFragment extends Fragment {
    private X5WebView webView;
    private String video, image;
    private ImageView imageView;
    private RelativeLayout startPlay;
    private SimpleTarget<GlideDrawable> myTarget;
    private TextView info,inforTitle;

    public VideoPlayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video_play, container, false);
        initView(view, getArguments());
        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView(View view, Bundle bundle) {
        imageView = view.findViewById(R.id.VideoPlay_x5_img);
        startPlay = view.findViewById(R.id.startplay);
//        playIcon = view.findViewById(R.id.playIcon);
        info=view.findViewById(R.id.info);
        inforTitle=view.findViewById(R.id.infoTitle);
        video = bundle.getString("url") + bundle.getString("videoPath") + bundle.getString("videoName");
        image = bundle.getString("url") + bundle.getString("imagePath") + bundle.getString("imageName");
        info.setTypeface(BaseApplication.infoTypeface);
        inforTitle.setTypeface(BaseApplication.infoTitleTypeface);
        info.setText(bundle.getString("videoName").substring(0,bundle.getString("videoName").lastIndexOf("."))+"\r\n"+getTime(bundle.getLong("downloadTime")*1000)+"\r\n"+getSize(bundle.getDouble("videoSize")));
        webView = (X5WebView) view.findViewById(R.id.webview);
        myTarget = new MySimpleTarget<GlideDrawable>(imageView, startPlay);
        Glide.with(this)
                .load("http://" + image)
                .error(R.mipmap.background)
                .bitmapTransform(new BlurTransformation(getContext(), 50))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE).into(myTarget);
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });
        webView.getView().setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        webView.loadDataWithBaseURL(null, html(), "text/html", "utf-8", null);

        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
//                playIcon.setVisibility(View.GONE);
                webView.getLayoutParams().height=webView.getHeight();
                webView.setOnTouchListener(null);
                return false;
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        try {
            super.onConfigurationChanged(newConfig);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public String getTime(Long date) {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        return (simpleDateFormat.format(date));
    }
    public String getSize(Double num) {
        String[] size= {"B","KB","MB","GB","TB","PB"};
        int i=0;
        while(num>=1024) {
            num=num/1024;
            i++;
        }
        return String.format("%.2f ",num)+size[i];
    }
    public String html() {
        String html;
        html = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<style type=\"text/css\">\n" +
                "        html,body{\n" +
                "            height:100%;\n" +
                "            margin: 0px;\n" +
                "            padding:0px;\n" +
                "        }\n" +
                "    </style>" +
                "<head lang=\"en\">\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, maximum-scale=1, minimum-scale=1, user-scale=1\">\n" +
                "    <title>" + video.substring(video.lastIndexOf("/") + 1, video.lastIndexOf(".")) + "</title>\n" +
                "</head>\n" +
                "<body style=\"margin: 0px;padding: 0px;background:#000000\">\n" +
                "        <video id=\"video\" onclick=\"play()\" style=\"margin: 0px;padding: 0px;width:100%; height: auto;display: block;\"poster=\"http://" + image + "\">\n" +
                "           <source src=http://" + video + ">\n" +
                "        </video>\n" +
                "\t<script type=\"text/javascript\">\n" +
                "window.onload=function() {\n" +
                "\tDocument.getElementById('video').play();\n" +
                "}\n" +
                "\t</script>" +
                "</body>\n" +
                "\n" +
                "</html>";
        return html;
    }

    class MySimpleTarget<GlideDrawable> extends SimpleTarget {
        private ImageView imageView;
        private View view;

        public MySimpleTarget(ImageView imageView, View view) {
            this.imageView = imageView;
            this.view = view;
        }

        @Override
        public void onResourceReady(Object resource, GlideAnimation glideAnimation) {
            imageView.setImageDrawable((Drawable) resource);
            view.setVisibility(View.VISIBLE);
        }
    }
}
