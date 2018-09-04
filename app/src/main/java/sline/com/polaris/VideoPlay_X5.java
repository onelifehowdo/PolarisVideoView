package sline.com.polaris;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import jp.wasabeef.glide.transformations.BlurTransformation;
import sline.com.polaris.utils.WebViewJavaScriptFunction;
import sline.com.polaris.utils.X5WebView;

public class VideoPlay_X5 extends Activity {


    private X5WebView webView;
    private String video, image;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play__x5);
        imageView=findViewById(R.id.VideoPlay_x5_img);
        video = getIntent().getStringExtra("url") + getIntent().getStringExtra("videoPath") + getIntent().getStringExtra("video_name");
        image = getIntent().getStringExtra("url") + getIntent().getStringExtra("imagePath") + getIntent().getStringExtra("image_name");
        Glide.with(this).load("http://"+image).thumbnail(0.2f).error(R.mipmap.background).bitmapTransform(new BlurTransformation(this,14)).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(imageView);
        webView = (X5WebView) findViewById(R.id.web_filechooser);
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
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
            webView.setVisibility(View.VISIBLE);
        }
    }
}
