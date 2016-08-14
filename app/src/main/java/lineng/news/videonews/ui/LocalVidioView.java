package lineng.news.videonews.ui;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lineng.news.videonews.R;
import lineng.news.videoplayer.full.ActivityideoView;

public class LocalVidioView extends FrameLayout{

    @BindView(R.id.ivPreview)ImageView ivPreview; // 视频预览图
    @BindView(R.id.tvVideoName)TextView tvVideoName; // 视频名称

    private String filePath; // 本地视频文件路径

    public LocalVidioView(Context context) {
        this(context, null);
    }

    public LocalVidioView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LocalVidioView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.item_local_video, this, true);
        ButterKnife.bind(this);
    }

    public void bind(Cursor cursor){
        filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        String videoName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
        tvVideoName.setText(videoName);
        // 清除old预览图
        ivPreview.setImageBitmap(null);
    }

    @OnClick
    public void onClick(){
        // 全屏播放
        ActivityideoView.open(getContext(), filePath);
    }

    @UiThread
    public void setPreview(@NonNull Bitmap bitmap){
        ivPreview.setImageBitmap(bitmap);
    }

    public void setPreview(final String filePath,final Bitmap bitmap){
        if(!filePath.equals(this.filePath))return;
        post(new Runnable() {
            @Override public void run() {
                // 二次确认
                if(!filePath.equals(LocalVidioView.this.filePath))return;
                ivPreview.setImageBitmap(bitmap);
            }
        });
    }

    public String getFilePath() {
        return filePath;
    }
}
