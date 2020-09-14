package app.com.worldofwealth;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import app.com.worldofwealth.models.User;
import app.com.worldofwealth.utils.CommonUtil;
import app.com.worldofwealth.utils.DBHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;


import org.json.JSONObject;

import java.io.File;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;


public class PostPreview extends AppCompatActivity {

    String imageurl, videourl, audiourl, posttitle, postdesc,localimageurl,posttype;
    ImageView img, editprofile;
    TextView txttitle, txtdesc;
    Button cancel, edit, publish;
    DBHelper dbHelper;
    User user;
    String uid;
    ProgressDialog progressDialog;
    JCVideoPlayerStandard videoplayer,audioplayer;
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(this,MainActivity.class));
        return true;
    }
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_preview);
        Intent intent = getIntent();
        posttitle = intent.getStringExtra("posttitle");
        postdesc = intent.getStringExtra("postdesc");
        uid = intent.getStringExtra("uid");
        localimageurl = intent.getStringExtra("localimageurl");
        imageurl = intent.getStringExtra("imageurl");
        videourl = intent.getStringExtra("videourl");
        audiourl = intent.getStringExtra("audiourl");
        posttype = intent.getStringExtra("posttype");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(posttitle);


         editprofile = (ImageView) findViewById(R.id.editprofile1);
        ImageView back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed1();

            }
        });
        publish = (Button) findViewById(R.id.publish);
        cancel = (Button) findViewById(R.id.cancel);
        txttitle = (TextView) findViewById(R.id.textView5);
        txtdesc = (TextView) findViewById(R.id.textView6);
     //   edit = (Button) findViewById(R.id.editprofile1);
        videoplayer = (JCVideoPlayerStandard) findViewById(R.id.videoplayer);
        audioplayer = (JCVideoPlayerStandard) findViewById(R.id.player_view);
        img = (ImageView) findViewById(R.id.img);


        if (imageurl != null) {
            img.setVisibility(View.VISIBLE);
            videoplayer.setVisibility(View.GONE);
            // File imgFile = new File("/storage/sdcard0/Pictures/Screenshots/Screenshot_2019-05-13-23-20-41.png");
            if (!img.equals("null")) {
                File imgFile = new File(localimageurl);
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    img.setImageBitmap(myBitmap);

                }
            }
        } else if (videourl != null) {
            videoplayer.setVisibility(View.VISIBLE);
            img.setVisibility(View.GONE);
            audioplayer.setVisibility(View.GONE);
            videoplayer.setUp(videourl, posttitle);
        }
        else if (audiourl != null) {
            audioplayer.setVisibility(View.VISIBLE);
            img.setVisibility(View.GONE);
            videoplayer.setVisibility(View.GONE);
            audioplayer.setUp(audiourl, posttitle);
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed1();
            }
        });
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject jsonParams = new JSONObject();
                    StringEntity entity = null;
                    AsyncHttpClient client = new AsyncHttpClient();
                    jsonParams.put("title", posttitle);
                    jsonParams.put("description", postdesc);
                    jsonParams.put("videourl", videourl);
                    jsonParams.put("videothumbnailurl", videourl);
                    jsonParams.put("imageurl", imageurl);
                    jsonParams.put("audiourl", audiourl);
                    jsonParams.put("createdby", uid);
                    jsonParams.put("posttype","Trending");
                    jsonParams.put("status", true);
                    entity = new StringEntity(jsonParams.toString());
                    client.post(PostPreview.this,
                            CommonUtil.mbaseurl + "Post/PostPost", entity,
                            "application/json", getResponseHandler("post"));
                    System.out.println("inpu :" + jsonParams.toString());

                } catch (Exception e) {
                    System.err.println("Error in inserting" + e.toString());

                }
            }
        });

        txttitle.setText(posttitle);
        txtdesc.setText(postdesc);

        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed1();
            }
        });

    }

    private ResponseHandlerInterface getResponseHandler(final String connectionfor) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                progressDialog = ProgressDialog.show(PostPreview.this,
                        "In progress", "Please wait");
            }

            @Override
            public void onFinish() {
                // Completed the request (either success or failure)
                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] resp) {
                if (connectionfor.contains("post")) {
                    String response = new String(resp);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String message = jsonObject.getString("Message");
                        if (message.contains("Successfull")) {
                            Toast.makeText(PostPreview.this, "Post Published successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(PostPreview.this, MainActivity.class);
                            startActivity(intent);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        };
    }


    private void onBackPressed1() {
        super.onBackPressed();
        return;
    }

}
