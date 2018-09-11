package sline.com.polaris;


import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import jp.wasabeef.glide.transformations.BlurTransformation;
import sline.com.polaris.utils.X5WebView;


/**
 * A simple {@link Fragment} subclass.
 */
public class VideoPlayFragment extends Fragment {
    private X5WebView webView;
    private String video, image;
    private ImageView imageView;
    private FrameLayout frameLayout;
    private SimpleTarget<GlideDrawable> myTarget;

    public VideoPlayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_video_play, container, false);
        initView(view,getArguments());
        return view;
    }

    private void initView(View view,Bundle bundle){
        imageView=view.findViewById(R.id.VideoPlay_x5_img);
        frameLayout=view.findViewById(R.id.body_fragment);
        video = bundle.getString("url") +bundle.getString("videoPath") + bundle.getString("video_name");
        image = bundle.getString("url") + bundle.getString("imagePath") +bundle.getString("image_name");
        webView = (X5WebView) view.findViewById(R.id.web_filechooser);
        myTarget=new MySimpleTarget<GlideDrawable>(imageView,webView);
        Glide.with(this)
                .load("http://"+image)
                .error(R.mipmap.background)
                .bitmapTransform(new BlurTransformation(getContext(),50))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE).into(myTarget);
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });
//        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        webView.getView().setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        webView.setWebViewClient(new MyClient());
        webView.loadDataWithBaseURL(null, html(), "text/html", "utf-8", null);
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
                "<body style=\"margin: 0px;padding: 0px\">\n" +
                "        <video style=\"margin: 0px;padding: 0px;width:100%; height: auto;display: block;\" controls  poster=\"http://" + image + "\">\n" +
                "           <source src=http://" + video + ">\n" +
                "        </video>\n" +
                "</body>\n" +
                "\n" +
                "</html>";
        return html;
    }

    class MyClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView webView, String s) {
            super.onPageFinished(webView, s);
            webView.getLayoutParams().height=webView.getHeight();
//            webView.setVisibility(View.VISIBLE);
        }
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
