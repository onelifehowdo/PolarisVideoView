package sline.com.polaris;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoPlay extends AppCompatActivity {
    private String url,name,videoPath;
    private VideoView mVideoView;
    private Uri mUri;
    private int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_play);
        mVideoView =findViewById(R.id.videoView);
        getData();
        playVideo();
    }

    @Override
    protected void onPause() {
        super.onPause();
        position=mVideoView.getCurrentPosition();
        Log.i("Tag","播放至："+position);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mVideoView.seekTo(position);
        mVideoView.start();
    }

    private void getData(){
        url=getIntent().getStringExtra("url");
        name=getIntent().getStringExtra("name");
        videoPath=getIntent().getStringExtra("videoPath");
    }

    private void playVideo(){
        mUri = Uri.parse("http://" + url+videoPath+name);
        mVideoView.setVideoURI(mUri);
        MediaController mediaController=new MediaController(this);
        mVideoView.setMediaController(mediaController);
        mVideoView.start();
    }

}
