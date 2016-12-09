package example.luojing.vitamiodemo;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import example.luojing.vitamiodemo.Utils.PixelUtils;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.ThumbnailUtils;
import io.vov.vitamio.provider.MediaStore;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

/**
 * Created by luojing on 2016/12/9.
 */
public class VideoActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String PATH = "http://bmob-cdn-5540.b0.upaiyun" +
            ".com/2016/09/09/d0fff44f40ffbc32808db91e4d0e3b4f" +
            ".mp4";
    private VideoView mVideoView;
    private MediaController mController;
    private ImageView mIvThumbnail;
    private ImageView mIvStart;
    private FrameLayout mFlVideoGroup;
    //当前是否为全屏
    private Boolean mIsFullScreen = false;
    /*需要隐藏显示的View*/
    private ArrayList<View> mViews;
    private TextView mTvFilmName;
    private TextView mTvDirector;
    private TextView mTvStory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!io.vov.vitamio.LibsChecker.checkVitamioLibs(this))
            return;
        setContentView(R.layout.activity_video);
        init();
        setData();
    }

    private void setData() {
        mTvFilmName.setText(getResources().getText(R.string.film_name));
        mTvDirector.setText(getResources().getText(R.string.film_director));
        mTvStory.setText(getResources().getText(R.string.film_story));
        new Thread() {
            @Override
            public void run() {
                //设置缩略图,Vitamio提供的工具类。
                final Bitmap videoThumbnail = ThumbnailUtils.createVideoThumbnail(
                        VideoActivity.this, PATH
                        , MediaStore.Video.Thumbnails.MINI_KIND);
                if (videoThumbnail != null) {
                    mIvThumbnail.post(new Runnable() {
                        @Override
                        public void run() {
                            mIvThumbnail.setImageBitmap(videoThumbnail);
                        }
                    });
                }
            }
        }.start();
    }

    private void init() {
        mFlVideoGroup = (FrameLayout) findViewById(R.id.fl_video_group);
        mController = new MediaController(this, true, mFlVideoGroup);
        mVideoView = (VideoView) findViewById(R.id.videoView);
        mIvStart = (ImageView) findViewById(R.id.iv_video_start);
        mTvFilmName = (TextView) findViewById(R.id.tv_film_name);
        mTvDirector = (TextView) findViewById(R.id.tv_director);
        mTvStory = (TextView) findViewById(R.id.tv_story);
        mIvThumbnail = (ImageView) findViewById(R.id.iv_video_thumbnail);
        mViews = new ArrayList<>();
        mViews.add(mTvFilmName);
        mViews.add(mTvDirector);
        mViews.add(mTvStory);
        //上来先隐藏controller
        mController.setVisibility(View.GONE);
        mIvStart.setOnClickListener(this);
    }

    //记得在activity中声明
    // android:screenOrientation="portrait" 强行设置为竖屏，关闭自动旋转屏幕
    //android:configChanges="orientation|keyboardHidden|screenLayout|screenSize"注册配置变化事件
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //横屏
            mIsFullScreen = true;
            //去掉系统通知栏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            hideViews(true);
            //调整mFlVideoGroup布局参数
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout
                    .LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            mFlVideoGroup.setLayoutParams(params);
            //原视频大小
//            public static final int VIDEO_LAYOUT_ORIGIN = 0;
            //最优选择，由于比例问题还是会离屏幕边缘有一点间距，所以最好把父View的背景设置为黑色会好一点
//            public static final int VIDEO_LAYOUT_SCALE = 1;
            //拉伸，可能导致变形
//            public static final int VIDEO_LAYOUT_STRETCH = 2;
            //会放大可能超出屏幕
//            public static final int VIDEO_LAYOUT_ZOOM = 3;
            //效果还是竖屏大小（字面意思是填充父View）
//            public static final int VIDEO_LAYOUT_FIT_PARENT = 4;
            mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
        } else {
            mIsFullScreen = false;
            /*清除flag,恢复显示系统状态栏*/
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            hideViews(false);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout
                    .LayoutParams.MATCH_PARENT,
                    PixelUtils.dip2px(this,220));
            mFlVideoGroup.setLayoutParams(params);
        }
    }

    public void hideViews(boolean hide) {
        if (hide) {
            for (int i = 0; i < mViews.size(); i++) {
                mViews.get(i).setVisibility(View.GONE);
            }
        } else {
            for (int i = 0; i < mViews.size(); i++) {
                mViews.get(i).setVisibility(View.VISIBLE);
            }
        }
    }

    //没有布局中没有设置返回键，只能响应硬件返回按钮，你可根据自己的意愿添加一个。若全屏就切换为小屏
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mIsFullScreen) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mController.setFullScreenIconState(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        mIvStart.setVisibility(View.GONE);
        mIvThumbnail.setVisibility(View.GONE);
        mVideoView.setVideoPath(PATH);
        mVideoView.setMediaController(mController);
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mVideoView.start();
                mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
            }
        });
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                //停止播放
                mVideoView.stopPlayback();
                mIvStart.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoView != null) {
            //清除缓存
            mVideoView.destroyDrawingCache();
            //停止播放
            mVideoView.stopPlayback();
            mVideoView = null;
        }
    }
}
