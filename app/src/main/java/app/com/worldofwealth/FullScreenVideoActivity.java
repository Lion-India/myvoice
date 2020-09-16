package app.com.worldofwealth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import app.com.worldofwealth.models.Post;
import cn.jzvd.JzvdStd;
import hb.xvideoplayer.MxVideoPlayer;
import hb.xvideoplayer.MxVideoPlayerWidget;


public class FullScreenVideoActivity extends AppCompatActivity {

    JzvdStd jzvdStd;
    Post videoPost, imagePost;
    ImageView imageView;
    MxVideoPlayer mpw_video_player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_video);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        imageView = findViewById(R.id.fullscreenImageView);

        jzvdStd = findViewById(R.id.jzvdVideoPlayer);
        mpw_video_player = findViewById(R.id.mp_video_player);
        Intent intent = getIntent();
        videoPost = (Post) intent.getSerializableExtra("VIDEO_DETAILS");
        imagePost = (Post) intent.getSerializableExtra("IMAGE_DETAILS");

        if (videoPost != null) {
            //  System.out.println("Testing: " + videoPost.getVideourl());
            if (videoPost.getVideourl().contains("104.211")) {
                //  mpw_video_player.startPlay(videoPost.getVideourl(), MxVideoPlayer.SCREEN_LAYOUT_NORMAL, videoPost.getTitle());
                MxVideoPlayerWidget.startFullscreen(this, MxVideoPlayerWidget.class, videoPost.getVideourl(), videoPost.getTitle());

                mpw_video_player.setVisibility(View.VISIBLE);
            } else {
                jzvdStd.setUp(videoPost.getVideourl(), videoPost.getTitle(), JzvdStd.SCREEN_FULLSCREEN);
                jzvdStd.backButton.setVisibility(View.GONE);

                jzvdStd.startVideo();
                jzvdStd.setVisibility(View.VISIBLE);
            }
        } else {
            jzvdStd.setVisibility(View.GONE);
        }
        if (imagePost != null) {

            Glide.with(this)
                    .load(imagePost.getImagurl()) // image url
                    .placeholder(R.drawable.background)
                    .error(R.drawable.background)

                    .into(imageView);
            imageView.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public void onBackPressed() {
//            if (JzvdStd.backPress()) {
//                return;
//            }
        MxVideoPlayerWidget.releaseAllVideos();
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JzvdStd.releaseAllVideos();
    }
}