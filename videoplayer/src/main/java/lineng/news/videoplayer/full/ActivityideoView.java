package lineng.news.videoplayer.full;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;
import lineng.news.videoplayer.R;

public class ActivityideoView extends AppCompatActivity {
    private static final String KEY_VIDEO_PATH = "KEY_VIDEO_PATH";

    public static void open(Context context, String videoPath) {
        Intent intent = new Intent(context, ActivityideoView.class);
        intent.putExtra(KEY_VIDEO_PATH, videoPath);
        context.startActivity(intent);
    }

    private VideoView videoview;
    private MediaPlayer mediaplayer;

    private ImageView ivLoading; // 缓冲信息(图像)
    private TextView tvBufferInfo; // 缓冲信息(文本信息,显示78kb/s, 67%)
    private int downloadSpeed; // 当前缓冲速度
    private int bufferPercent; // 当前缓冲百分比

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setBackgroundDrawableResource(android.R.color.black);
        setContentView(R.layout.activity_video_view);


    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        ininBufferViews();
        initVideoView();
        Vitamio.isInitialized(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoview.setVideoPath(getIntent().getStringExtra(KEY_VIDEO_PATH));
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoview.stopPlayback();
    }

    private void ininBufferViews() {
        tvBufferInfo = (TextView) findViewById(R.id.tvBufferInfo);
        ivLoading = (ImageView) findViewById(R.id.ivLoading);
        tvBufferInfo.setVisibility(View.INVISIBLE);
        ivLoading.setVisibility(View.INVISIBLE);
    }

    private void initVideoView() {
        videoview = (VideoView) findViewById(R.id.videoView);
        //控制快进 后退之类的
        videoview.setMediaController(new CustomMediaController(this));
        videoview.setKeepScreenOn(true);
        videoview.requestFocus();
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaplayer = mp;
                //缓存区大小
                mediaplayer.setBufferSize(512 * 1024);
            }
        });
        // 缓冲更新监听
        videoview.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                // percent:当前缓冲的百分比
                bufferPercent = percent;
                updateBufferViewInfo();
            }
        });
        //"状态"信息监听
        videoview.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START: // 开始缓冲
                        showBufferViews();
                        if (videoview.isPlaying()) {
                            videoview.pause();
                        }
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END: // 结束缓冲
                        hideBufferViews();
                        videoview.start(); // 开始播放视频
                        break;
                    case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED: // 缓冲时下载速率
                        downloadSpeed = extra;
                        updateBufferViewInfo();
                        break;
                }
                return true;
            }
        });
    }

    private void showBufferViews() {
        tvBufferInfo.setVisibility(View.VISIBLE);
        ivLoading.setVisibility(View.VISIBLE);
        downloadSpeed = 0;
        bufferPercent = 0;
    }

    // 在结束缓冲时调用的
    private void hideBufferViews() {
        tvBufferInfo.setVisibility(View.INVISIBLE);
        ivLoading.setVisibility(View.INVISIBLE);
    }

    // 缓冲时，速度变化时调用的
    private void updateBufferViewInfo() {
        String info = String.format(Locale.CHINA, "%d%%,%dkb/s", bufferPercent, downloadSpeed);
        tvBufferInfo.setText(info);
    }
}
