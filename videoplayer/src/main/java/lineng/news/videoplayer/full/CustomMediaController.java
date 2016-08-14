package lineng.news.videoplayer.full;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import io.vov.vitamio.widget.MediaController;
import lineng.news.videoplayer.R;

/**
 * Created by 繁华丶落尽 on 2016/8/10.
 */
public class CustomMediaController extends MediaController {
    private MediaPlayerControl mediaPlayerControl;
    private final AudioManager audioManager;//调整音量
    private Window window;//亮度

    private final int maxVolume;//最大音量
    private int currentVolume;//当前音量
    private float currentBrightness;//当前亮度

    public CustomMediaController(Context context) {
        super(context);
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        window = ((Activity) context).getWindow();
        // 初始设置一个默认音量 50%
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume / 2, AudioManager.FLAG_SHOW_UI);
        // 初始设置一个默认亮度 50%
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.screenBrightness = 0.5f;
        window.setAttributes(layoutParams);
    }

    @Override
    protected View makeControllerView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_custom_video_controller, this, true);
        initView(view);
        return view;
    }

    @Override
    public void setMediaPlayer(MediaPlayerControl player) {
        super.setMediaPlayer(player);
        this.mediaPlayerControl = player;
    }

    private void initView(View view) {
        ImageButton btnFastForWard = (ImageButton) findViewById(R.id.btnFastForward);
        //快进
        btnFastForWard.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取当前进度
                long position = mediaPlayerControl.getCurrentPosition();
                position += 10000;
                mediaPlayerControl.seekTo(position);
            }
        });
        ImageButton btnFastReWind = (ImageButton) findViewById(R.id.btnFastRewind);
        //快退
        btnFastReWind.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                long position = mediaPlayerControl.getCurrentPosition();
                position -= 10000;
                mediaPlayerControl.seekTo(position);
            }
        });
        final View adjustView = view.findViewById(R.id.adjustView);
        final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(
                    MotionEvent e1,
                    MotionEvent e2,
                    float distanceX,
                    float distanceY) {

                float startX = e1.getX();
                float startY = e1.getY();
                float endX = e2.getX();
                float endY = e2.getY();
                float width = adjustView.getWidth();
                float height = adjustView.getHeight();
                float percentage = (startY - endY) / height;
                if (startX < width / 5) {
                    adjustBrightness(percentage);
                    return true;
                } else if (startX > width * 4 / 5) {
                    adjustVolume(percentage);
                    return true;
                }

                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        });

        adjustView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
                    currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    currentBrightness = window.getAttributes().screenBrightness;
                }
                gestureDetector.onTouchEvent(event);
                CustomMediaController.this.show();
                return true;
            }
        });
    }

    private void adjustVolume(float percentage) {
        int targetVolume = (int) (percentage * maxVolume) + currentVolume;
        targetVolume = targetVolume > maxVolume ? maxVolume : targetVolume;
        targetVolume = targetVolume < 0 ? 0 : targetVolume;
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetVolume, AudioManager.FLAG_SHOW_UI);
    }

    private void adjustBrightness(float percentage) {
        float targetBrightness = percentage + currentBrightness;
        targetBrightness = targetBrightness > 1.0f ? 1.0f : targetBrightness;
        targetBrightness = targetBrightness < 0.01f ? 0.01f : targetBrightness;
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.screenBrightness = targetBrightness;
        window.setAttributes(layoutParams);
    }
}
