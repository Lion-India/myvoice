package app.com.worldofwealth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import app.com.worldofwealth.models.Post;
import app.com.worldofwealth.models.User;
import app.com.worldofwealth.utils.CommonUtil;
import app.com.worldofwealth.utils.DBHelper;
import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

public class DescriptionActivity extends AppCompatActivity {
    public static final String KEY = "Postkey";
    public TextView dpusername, dpusertype, dposttitle, dpostdesc, dpostdate, dpviewmore, dpsourcename,tscore;
    ImageView dpostimage, dipmenu, like, dislike, comment, share, puserimg;
    JCVideoPlayerStandard player_view;
    DBHelper dbHelper;
    User user;
    String userid;
    Post post;
    String doc_type;
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(this,MainActivity.class));
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        Intent intent = getIntent();
        post = (Post) intent.getSerializableExtra(KEY);
        dbHelper = new DBHelper(getApplicationContext());
        user = dbHelper.getUserData();

        userid = user.getUid();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(post.getTitle());


        dpusername = (TextView) findViewById(R.id.pusername);
        dpusertype = (TextView) findViewById(R.id.pusertype);
        dposttitle = (TextView) findViewById(R.id.posttitle);
        dpostdesc = (TextView) findViewById(R.id.postdesc);
        dpostdate = (TextView) findViewById(R.id.postdate);
        dpostimage = (ImageView) findViewById(R.id.postimage);
        dipmenu = (ImageView) findViewById(R.id.ipmenu);
        player_view = (JCVideoPlayerStandard) findViewById(R.id.player_view);
        dpsourcename = (TextView) findViewById(R.id.postdate);
        dpviewmore = (TextView) findViewById(R.id.view);
        like = (ImageView) findViewById(R.id.ilike);
        dislike = (ImageView) findViewById(R.id.idislike);
        comment = (ImageView) findViewById(R.id.icomments);
        share = (ImageView) findViewById(R.id.ishare);
        //tscore=(TextView)findViewById(R.id.tscore);
        puserimg = (ImageView) findViewById(R.id.puserimg);

        dpusername.setText(CommonUtil.isNullCheck(post.getCreatedbyname()) ? post.getCreatedbyname(): " --");
        dpusertype.setText(CommonUtil.isNullCheck(post.getCreatedbytype()) ? post.getCreatedbytype(): " --");
        //dpusername.setText(post.getCreatedbyname());
        //dpusertype.setText(post.getCreatedbytype());
        dposttitle.setText(post.getTitle());
        dpostdesc.setText(post.getDesc());

        //for like and dislike

        JSONArray upvotearr = null;
        if (post.getUpvote() != null && !post.getUpvote().equals("null")) {
            try {
                upvotearr = new JSONArray(post.getUpvote());
                for (int i = 0; i < upvotearr.length(); i++) {
                    JSONObject jsonObject = new JSONObject(upvotearr.getJSONObject(i).toString());
                    String likeduser_id = jsonObject.getString("userid");
                    if (likeduser_id.equals(userid)) {
                        like.setImageResource(R.drawable.icon_like_blue);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //user dislike
        JSONArray downvotearr = null;
        if (post.getDownvote() != null && !post.getDownvote().equals("null")) {
            try {
                downvotearr = new JSONArray(post.getDownvote());
                for (int i = 0; i < downvotearr.length(); i++) {
                    JSONObject jsonObject = new JSONObject(downvotearr.getJSONObject(i).toString());
                    String dislikeuser_id = jsonObject.getString("userid");
                    if (dislikeuser_id.equals(userid)) {
                        dislike.setImageResource(R.drawable.icon_dislike_blue);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONArray commentarray = null;
        if (post.getComments() != null && !post.getComments().equals("null")) {
            try {
                commentarray = new JSONArray(post.getComments());


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /*if (!post.getSourcename().equals("null")) {
            dpsourcename.setVisibility(View.VISIBLE);
            dpviewmore.setVisibility(player_view.VISIBLE);
            dpsourcename.setText(post.getSourcename());
        }*/

        dpviewmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewsWebViewActivity.class);
                intent.putExtra(DescriptionActivity.KEY, post);
                startActivity(intent);
            }
        });

        //for userprofile image
        String userImageUrl = post.getCreatedbyimage();
        if (userImageUrl != null && !userImageUrl.equals("null") && !userImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(post.getCreatedbyimage()) // image url
                    .placeholder(R.drawable.background)
                    .error(R.drawable.background)
                    //.override(200, 200); // resizing
                    //  .centerCrop()
                    .into(puserimg);
        }

        //for Image posts
        if (!post.getImagurl().equals("null")) {
            Glide.with(this)
                    .load(post.getImagurl()) // image url
                    .placeholder(R.drawable.background)
                    .error(R.drawable.background)
                    //.override(200, 200); // resizing
                    //  .centerCrop()
                    .into(dpostimage);
            dpostimage.setVisibility(View.VISIBLE);
        }
        //for Video
        if (!post.getVideourl().equals("null")) {
            player_view.setUp(post.getVideourl(), post.getTitle());
            player_view.setVisibility(View.VISIBLE);
        }

        //for Audio
        if (!post.getAudiourl().equals("null")) {
            player_view.setUp(post.getAudiourl(), post.getTitle());
            player_view.setVisibility(View.VISIBLE);
        }

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                like.setImageResource(R.drawable.icon_like_blue);
                dislike.setImageResource(R.drawable.icon_dislike_grey);
                likeDislikeClicked( "upvote");

            }
        });

        dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dislike.setImageResource(R.drawable.icon_dislike_blue);
                like.setImageResource(R.drawable.icon_like_grey);
                likeDislikeClicked(  "downvote");
            }
        });

       /* if(upvotearr==null&&downvotearr==null&&commentarray==null){
            tscore.setText("The LION Score:\n" +
                    "0");
        }
        else {
            int topscore = upvotearr.length() + downvotearr.length() + commentarray.length();
            if (topscore > 0)
                tscore.setText("The LION Score:\n" +
                        topscore);
        }*/

    }

    private void likeDislikeClicked(String type) {
        String url = "Opinion/PostVote?userid=" + user.getUid() + "&vote=" + type + "&doctypeid=" + post.getId() + "&doctype=News";

        try {
            final ProgressDialog progressDialog = new ProgressDialog(DescriptionActivity.this);
            progressDialog.setMessage(DescriptionActivity.this.getString(R.string.please_wait));
            progressDialog.setTitle(DescriptionActivity.this.getString(R.string.app_name));
            progressDialog.show();
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            AsyncHttpClient client = new AsyncHttpClient();
            client.post(DescriptionActivity.this, CommonUtil.mbaseurl + url,
                    null, "application/json", new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            String res = new String(responseBody);
                            progressDialog.cancel();
                            try {
                                JSONObject job = new JSONObject(res);
                                if (job.has("Message")) {
                                    CommonUtil.showToast(DescriptionActivity.this, DescriptionActivity.this.getString(R.string.something_went_wrong));
                                } else {
                                    Post op = CommonUtil.ParseNewsStringtoPost(job);


                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                CommonUtil.showToast(DescriptionActivity.this, DescriptionActivity.this.getString(R.string.something_went_wrong));
                            }

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            progressDialog.cancel();
                            CommonUtil.showToast(DescriptionActivity.this, DescriptionActivity.this.getString(R.string.something_went_wrong));
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();

    }
}