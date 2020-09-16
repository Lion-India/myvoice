package app.com.worldofwealth.agora.openlive.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import app.com.worldofwealth.InterviewActivity;


import app.com.worldofwealth.R;
import app.com.worldofwealth.utils.CommonUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import hb.xvideoplayer.MxVideoPlayer;
import hb.xvideoplayer.MxVideoPlayerWidget;


public class VideoPublishActivity extends AppCompatActivity implements View.OnClickListener {
    TextView publish;
    String sid, id;
    Button bpublish, bcancel;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_publish);
        publish = (TextView) findViewById(R.id.publish);
        bpublish = (Button) findViewById(R.id.bpublish);
        bcancel = findViewById(R.id.bcancel);

        bpublish.setOnClickListener(this);
        bcancel.setOnClickListener(this);

        sid = getIntent().getStringExtra("sid");
        id = getIntent().getStringExtra("id");
        String finalurl = CommonUtil.GetMP4fileName(CommonUtil.mvideourl + sid);
        MxVideoPlayerWidget videoPlayerWidget = (MxVideoPlayerWidget) findViewById(R.id.mpw_video_player);
        videoPlayerWidget.startPlay(finalurl, MxVideoPlayer.SCREEN_LAYOUT_NORMAL, "");

    }


    @Override
    public void onClick(View v) {
        if (v == bcancel) {
            callInterViewList();
        } else if (v == bpublish) {
            PublishInterView();
        }
    }

    private void PublishInterView() {
        String url = CommonUtil.mbaseurl + "Interview/UpdateInterviewVideoStatus?doctypeid=" + id + "&status=true";
        progressDialog = new ProgressDialog(VideoPublishActivity.this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                 progressDialog.cancel();
             callInterViewList();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                CommonUtil.showToast(VideoPublishActivity.this, getString(R.string.something_went_wrong));
                progressDialog.cancel();
            }


            //   forwardToLiveRoom(role) ;
        });
    }

    private void callInterViewList() {
        startActivity(new Intent(this, InterviewActivity.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        MxVideoPlayer.releaseAllVideos();
    }

    @Override
    public void onBackPressed() {
        if (MxVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }
}
