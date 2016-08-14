package lineng.news.videoplayer.full.part;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.nio.channels.Pipe;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.CenterLayout;
import lineng.news.videoplayer.R;
import lineng.news.videoplayer.full.ActivityideoView;

/**
 * Created by 繁华丶落尽 on 2016/8/11.
 */
public class SimpleVideoView extends FrameLayout {
    public SimpleVideoView(Context context) {
        this(context, null);
    }

    public SimpleVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private String videopath;
    private ImageView ivpreview;
    private ImageButton btnToggle;
    private ProgressBar progressBar;

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;

    private MediaPlayer mediaPlayer;

    private void init() {
        Vitamio.isInitialized(getContext());
        LayoutInflater.from(getContext()).inflate(R.layout.view_simple_video_player, this, true);
        initSurfaceView();
        initControllerViews();
    }

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isPlaying) {
                long current = mediaPlayer.getCurrentPosition();
                long duration = mediaPlayer.getDuration();
                int proess = (int) (current * 1000 / duration);
                progressBar.setProgress(proess);
                handler.sendEmptyMessageDelayed(0, 200);
            }
        }
    };

    private void initSurfaceView() {
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setFormat(PixelFormat.RGBA_8888);
    }

    private void initControllerViews() {
        ivpreview = (ImageView) findViewById(R.id.ivPreview);
        btnToggle = (ImageButton) findViewById(R.id.btnToggle);
        btnToggle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放
                if (mediaPlayer.isPlaying()) {
                    pauseMediaPlayer();
                } else if (isprepare) {
                    startMediaPlayer();
                } else {
                    Toast.makeText(getContext(), "暂无法播放", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //进度条
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(1000);
        //全屏按钮
        ImageButton btnFull = (ImageButton) findViewById(R.id.btnFullScreen);
        btnFull.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityideoView.open(getContext(), videopath);
            }
        });
    }

    public void setVideoPath(String videopath) {
        this.videopath = videopath;
    }

    public void onResume() {
        initMediaPlayer();
        prepareMediaPlayer();
    }

    private void prepareMediaPlayer() {

        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(videopath);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private boolean isprepare;
    private boolean isPlaying;

    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer(getContext());
        mediaPlayer.setDisplay(surfaceHolder);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                startMediaPlayer();
            }


        });
        mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                int videoWidth=surfaceView.getWidth();
                int videoHeight=videoWidth*height/width;
                ViewGroup.LayoutParams layoutParams=surfaceView.getLayoutParams();
                layoutParams.height=videoHeight;
                layoutParams.width=videoWidth;
                surfaceView.setLayoutParams(layoutParams);
            }
        });
        mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                if (what == mediaPlayer.MEDIA_INFO_FILE_OPEN_OK) {
                    long bufferSize = mediaPlayer.audioTrackInit();
                    mediaPlayer.audioInitedOk(bufferSize);
                    return true;
                }
                return false;
            }
        });

    }

    private void startMediaPlayer() {
        isprepare = true;
        if (isprepare) {
            mediaPlayer.start();
        }
        isPlaying = true;
        handler.sendEmptyMessage(0);
        btnToggle.setImageResource(R.drawable.ic_pause);
    }

    public void onPasuse() {
        pauseMediaPlayer();
        releaseMediaPlayer();
    }

    private void releaseMediaPlayer() {
        mediaPlayer.release();
        isprepare = false;
        isPlaying = false;
        mediaPlayer = null;
    }

    private void pauseMediaPlayer() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        isPlaying = false;
        handler.removeMessages(0);
        btnToggle.setImageResource(R.drawable.ic_play_arrow);
    }
}
