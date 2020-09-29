package app.com.worldofwealth.fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import app.com.worldofwealth.CreatePostActivity;
import app.com.worldofwealth.R;
import app.com.worldofwealth.Services.MyFirebaseMessagingService;
import app.com.worldofwealth.adapters.OngoingInterViewAdapter;
import app.com.worldofwealth.adapters.TrendingAdapter;
import app.com.worldofwealth.models.Interview;
import app.com.worldofwealth.utils.CommonUtil;
import app.com.worldofwealth.models.Post;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class TopScoreFragment extends Fragment {
    ProgressDialog progressDialog;
    static ArrayList<Post> postlist;
    private RecyclerView recyclerView;
    private TrendingAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    TextView alertinfo;
    private SwipeRefreshLayout refreshView;

    static  ArrayList<Interview> ongoinginterviewlist;
    private RecyclerView interviewrecyclerView;
    private OngoingInterViewAdapter ongoingInterViewAdapter;
    private RecyclerView.LayoutManager ongoinglayoutManager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        registerEventReceiver();

        postlist = new ArrayList<Post>();
        ongoinginterviewlist = new ArrayList<Interview>();
        View view = inflater.inflate(R.layout.topscore_fragment, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.post_recycler_view);
        alertinfo=(TextView)view.findViewById(R.id.alertinfo);
        refreshView = (SwipeRefreshLayout) view.findViewById(R.id.refreshView);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());

        FloatingActionButton fab = view.findViewById(R.id.fabtop);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), CreatePostActivity.class);
                startActivity(intent);
            }
        });

        mAdapter = new TrendingAdapter(getContext(),postlist);
        recyclerView.setAdapter(mAdapter);
        getTheTrending();
        refreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Load data to your RecyclerView
                getTheTrending();
                refreshView.setRefreshing(false);
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        // Stop animation (This will be after 3 seconds)
                        refreshView.setRefreshing(false);
                    }
                }, 4000);
            }
        });

        interviewrecyclerView = (RecyclerView) view.findViewById(R.id.interview_recycler_view);
        //ongoinglayoutManager = new LinearLayoutManager(getActivity());
        ongoinglayoutManager= new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        interviewrecyclerView.setLayoutManager(ongoinglayoutManager);
        interviewrecyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        ongoingInterViewAdapter = new OngoingInterViewAdapter(getContext(),ongoinginterviewlist);
        interviewrecyclerView.setAdapter(ongoingInterViewAdapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ongoinginterviews();
    }

    private void ongoinginterviews() {
        try {
            JSONObject jsonParams = new JSONObject();
            StringEntity entity = null;
            AsyncHttpClient client = new AsyncHttpClient();
            entity = new StringEntity(jsonParams.toString());
            client.post(getActivity(),
                    CommonUtil.mbaseurl + "Interview/GetOnGoingInterview" , entity,
                    "application/json", getResponseHandler("ongoinginterviews"));
            System.out.println("inpu :" + jsonParams.toString());

        } catch (Exception e) {
            System.err.println("Error in inserting" + e.toString());

        }
    }

    private ResponseHandlerInterface getResponseHandler(final String connectionfor) {
        return new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                progressDialog.dismiss();
                progressDialog = ProgressDialog.show(getActivity(),
                        "In progress", "Please wait");
            }

            @Override
            public void onFinish() {
                // Completed the request (either success or failure)
                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] resp) {
                if (connectionfor.contains("ongoinginterviews")) {
                    String response = new String(resp);
                    progressDialog.cancel();
                    ongoinginterviewlist.clear();
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject job = new JSONObject(jsonArray.get(i).toString());
                            Interview interview = ParseStringtoOpinion(job);
                            ongoinginterviewlist.add(interview);
                        }
                        if (ongoinginterviewlist.size() > 0){
                            interviewrecyclerView.setVisibility(View.VISIBLE);
                        }
                        ongoingInterViewAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.cancel();
                CommonUtil.showToast(getActivity(), getString(R.string.something_went_wrong));
            }
        };
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

    private void registerEventReceiver() {
        IntentFilter eventFilter = new IntentFilter();
        eventFilter.addAction(MyFirebaseMessagingService.NEW_DATA_EVENT);
        getActivity().registerReceiver(eventReceiver, eventFilter);
    }

    private BroadcastReceiver eventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //This code will be executed when the broadcast in activity B is launched
         getTheTrending();
        }
    };

    private void getTheTrending() {
        String murl = CommonUtil.mbaseurl + "post/GetTrending?";
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.dismiss();
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        AsyncHttpClient client = new AsyncHttpClient();
        client.setConnectTimeout(20000);
        //System.out.println("urltest :"+ murl);
        client.post(getActivity(), murl, null, "application/json",
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
                                String status = job.getString("status");
                                Post post = ParseStringtoPost(job);
                                 //System.out.println("posttest :" + job.toString());
                                if(status.contains("true")){
                                    postlist.add(post);
                                }

                            }
                            if(jarr.length()>0) {
                                alertinfo.setVisibility(View.GONE);
                            }

                           else if (response== null || response.contains("")){
                                alertinfo.setVisibility(View.VISIBLE);
                            }
                            mAdapter.notifyDataSetChanged();

                        } catch (Exception e) {
                            e.printStackTrace();
                            CommonUtil.showToast(getActivity(), getString(R.string.something_went_wrong));
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        progressDialog.cancel();
                        CommonUtil.showToast(getActivity(), getString(R.string.something_went_wrong));
                    }

                });
    }

    private Post ParseStringtoPost(JSONObject job) {
        //System.out.println("vimal:"+job.toString());
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
            post.setCreatedbyimage(job.getString("createdbyimage"));
            post.setShareurl(job.getString("shareurl"));
           // post.setSourcename(job.getString("sourcename"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return post;
    }

    /* @Override
    public void onPause() {
        super.onPause();
        StarAdapter.ViewHolder.closeAllVideoes();
    }*/
}
