package app.com.worldofwealth.fragments;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import app.com.worldofwealth.CreatePostActivity;
import app.com.worldofwealth.R;
import app.com.worldofwealth.Services.MyFirebaseMessagingService;
import app.com.worldofwealth.adapters.NgoAdapter;
import app.com.worldofwealth.models.Post;
import app.com.worldofwealth.utils.CommonUtil;
import cz.msebera.android.httpclient.Header;

public class NGOFragment extends    Fragment {
    ProgressDialog progressDialog;
    static ArrayList<Post> postlist;
    private RecyclerView recyclerView;
    private NgoAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    TextView alertinfo;
    private SwipeRefreshLayout refreshView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ngo_fragment, container, false);
        postlist = new ArrayList<Post>();
        registerEventReceiver();
        recyclerView = (RecyclerView) view.findViewById(R.id.post_recycler_view);
        alertinfo=(TextView)view.findViewById(R.id.alertinfo);
        refreshView = (SwipeRefreshLayout) view.findViewById(R.id.refreshView);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());

        FloatingActionButton fab = view.findViewById(R.id.fabscholar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), CreatePostActivity.class);
                startActivity(intent);
            }
        });

        mAdapter = new NgoAdapter(getContext(),postlist);
        recyclerView.setAdapter(mAdapter);
        getNgosPost();
        refreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Load data to your RecyclerView
                getNgosPost();
                refreshView.setRefreshing(false);
                /*new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        // Stop animation (This will be after 3 seconds)
                        refreshView.setRefreshing(false);
                    }
                }, 4000);*/
            }
        });
        return view;
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
            getNgosPost();
        }
    };


    private void getNgosPost() {
        String murl = CommonUtil.mbaseurl + "post/GetPost?usertype=Myvoice";
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        AsyncHttpClient client = new AsyncHttpClient();
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
                                System.out.println("post :" + job.toString());
                                if(status.contains("true")){
                                    postlist.add(post);
                                }
                            }
                            if(jarr.length()>0) {
                                alertinfo.setVisibility(View.GONE);
                            }

                            else if (response== null ||response.contains("")){
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
            //post.setSourcename(job.getString("sourcename"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return post;
    }
}


