package app.com.worldofwealth;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import app.com.worldofwealth.adapters.TopScoreAdapter;
import app.com.worldofwealth.models.Post;
import app.com.worldofwealth.models.User;
import app.com.worldofwealth.utils.CommonUtil;
import app.com.worldofwealth.utils.DBHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SavedAritcalActivity extends AppCompatActivity {
    ProgressDialog progressDialog;
    static ArrayList<Post> postlist;
    private RecyclerView recyclerView;
    private TopScoreAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    DBHelper dbHelper;
    User user;
    String userid;
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(this,MainActivity.class));
        return true;
    }
    @SuppressLint("RestrictedApi")
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interview);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.savedarticles);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        recyclerView = findViewById(R.id.post_recycler_view);

        postlist = new ArrayList<Post>();
        dbHelper=new DBHelper(getApplicationContext());
        user=dbHelper.getUserData();
        userid=user.getUid();

        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());

        mAdapter = new TopScoreAdapter(getApplicationContext(), postlist);
        recyclerView.setAdapter(mAdapter);
        getTheTrending();


    }

    private void getTheTrending() {
        String murl = CommonUtil.mbaseurl + "post/GetBookmarkByUser?userid="+userid;
        progressDialog = new ProgressDialog(SavedAritcalActivity.this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(getApplicationContext(), murl, null, "application/json",
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String response = new String(responseBody);
                        progressDialog.cancel();
                        postlist.clear();
                        try {
                            JSONArray jarr = new JSONArray(response);
                            for (int i = 0; i < jarr.length(); i++) {
                                JSONObject job = new JSONObject(jarr.get(i).toString());
                                Post post = ParseStringtoPost(job);
                                // System.out.println("post :" + job.toString());
                                postlist.add(post);
                            }
                            mAdapter.notifyDataSetChanged();

                        } catch (Exception e) {
                            e.printStackTrace();
                            CommonUtil.showToast(getApplicationContext(), getString(R.string.something_went_wrong));
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        progressDialog.cancel();
                        CommonUtil.showToast(getApplicationContext(), getString(R.string.something_went_wrong));
                    }

                });
    }

    private Post ParseStringtoPost(JSONObject job) {
        Post post = new Post();

        try {
            post.setId(job.getString("id"));
            post.setTitle(job.getString("title"));
            post.setDesc(job.getString("description"));
            post.setVideourl(job.getString("videourl"));
            post.setVideothumbnailurl(job.getString("videothumbnailurl"));
            post.setImagurl(job.getString("imageurl"));
            post.setAudiourl(job.getString("audiourl"));
            post.setUpvote(job.getString("upvote"));
            post.setDownvote(job.getString("downvote"));
            post.setComments(job.getString("comments"));
            post.setDatatype(job.getString("datatype"));
            post.setCreatedbyname(job.getString("createdbyname"));
            post.setCreatedbytype(job.getString("createdbytype"));
            post.setCreateddate(job.getString("createddate"));
            post.setSourcename(job.getString("sourcename"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return post;
    }
}




