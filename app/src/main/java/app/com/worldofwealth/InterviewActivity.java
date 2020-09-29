package app.com.worldofwealth;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import app.com.worldofwealth.adapters.InterViewAdapter;
import app.com.worldofwealth.models.Interview;
import app.com.worldofwealth.models.User;
import app.com.worldofwealth.utils.CommonUtil;
import app.com.worldofwealth.utils.DBHelper;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class InterviewActivity extends AppCompatActivity {
    ProgressDialog progressDialog;
    static ArrayList<Interview> interviewlist;
    RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    InterViewAdapter mAdapter;
    User user;
    DBHelper dbHelper;
    String usertype;

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(this, MainActivity.class));
        return true;
    }

    @SuppressLint("RestrictedApi")
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interview);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.interviews);

        dbHelper = new DBHelper(getApplicationContext());
        user = dbHelper.getUserData();
        usertype = user.getUsertype();

        recyclerView = findViewById(R.id.post_recycler_view);

        interviewlist = new ArrayList<Interview>();

        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());

        mAdapter = new InterViewAdapter(this, interviewlist);
        recyclerView.setAdapter(mAdapter);
        FloatingActionButton fab = findViewById(R.id.fab);
        if ((usertype.contains("User")) || (usertype.contains("Lawmakers")) || (usertype.contains("Enterprises")) || (usertype.contains("Myvoice")) || (usertype.contains("Scholars"))) {
            fab.setVisibility(View.GONE);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InterviewActivity.this, CreateInterviewActivity.class);
                startActivity(intent);
            }
        });


        getInterViews();
    }

    private BroadcastReceiver eventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //This code will be executed when the broadcast in activity B is launched
            getInterViews();
        }
    };

    private void getInterViews() {
        String murl = CommonUtil.mbaseurl + "Interview/GetInterview";
        progressDialog = new ProgressDialog(InterviewActivity.this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        AsyncHttpClient client = new AsyncHttpClient();
        client.setConnectTimeout(20000);
        client.post(InterviewActivity.this, murl, null, "application/json",
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String response = new String(responseBody);
                        progressDialog.cancel();
                        interviewlist.clear();
                        try {
                            JSONArray jarr = new JSONArray(response);
                            for (int i = 0; i < jarr.length(); i++) {
                                JSONObject job = new JSONObject(jarr.get(i).toString());
                                Interview interview = ParseStringtoOpinion(job);
                                if (interview.isIspublished()) {
                                    interviewlist.add(interview);
                                }
                                //interviewlist.add(interview);
                            }

                            // set the adapter object to the Recyclerview
                            mAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                            CommonUtil.showToast(InterviewActivity.this, getString(R.string.something_went_wrong));
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        progressDialog.cancel();
                        CommonUtil.showToast(InterviewActivity.this, getString(R.string.something_went_wrong));
                    }
                });
    }

    private Interview ParseStringtoOpinion(JSONObject job) {
        Interview interview = new Interview();
        try {
            interview.setId(job.getString("id"));
            interview.setTitle(job.getString("title"));
            interview.setDesc(job.getString("description"));
            interview.setStartdate(job.getString("startdate"));
            interview.setStarttime(job.getString("starttime"));
            interview.setEnddate(job.getString("enddate"));
            interview.setEndtime(job.getString("endtime"));
            interview.setCreatedby(job.getString("createdby"));
            interview.setInvitedusers(job.getString("invitedusers"));
            interview.setVideoid(job.getString("videoid"));
            interview.setUpvote(job.getString("upvote"));
            interview.setDownvote(job.getString("downvote"));
            interview.setComments(job.getString("comments"));
            interview.setDatatype(job.getString("datatype"));
            interview.setStatus(job.getString("status"));
            interview.setVideourl(job.getString("videourl"));
            interview.setIspublished(job.getBoolean("ispublished"));
            interview.setCreateddate(job.getString("createddate"));
            interview.setCreatedbytype(job.getString("createdbytype"));
            interview.setCreatedbyname(job.getString("createdbyname"));
            interview.setCreatedbyimage(job.getString("createdbyimage"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return interview;
    }


   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @Override
    protected void onPause() {
        super.onPause();
        InterViewAdapter.MyViewHolder.closeAllVideoes();
    }
}
