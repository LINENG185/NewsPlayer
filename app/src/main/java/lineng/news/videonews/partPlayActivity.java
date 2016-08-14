package lineng.news.videonews;

import android.os.Binder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import butterknife.BindView;
import butterknife.ButterKnife;
import lineng.news.videonews.Util.UrlUtil;
import lineng.news.videoplayer.full.part.SimpleVideoView;

public class partPlayActivity extends AppCompatActivity {
@BindView(R.id.simpleVideoView)
SimpleVideoView simpleVideoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_part_play);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        ButterKnife.bind(this);
        simpleVideoView.setVideoPath(UrlUtil.getTestVideo3());
    }

    @Override
    protected void onResume() {
        super.onResume();
        simpleVideoView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        simpleVideoView.onPasuse();
    }
}
