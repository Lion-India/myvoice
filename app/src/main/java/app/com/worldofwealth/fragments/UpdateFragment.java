package app.com.worldofwealth.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;

import app.com.worldofwealth.R;
import app.com.worldofwealth.models.ResponseModal;
import app.com.worldofwealth.models.User;
import app.com.worldofwealth.utils.CommonUtil;
import app.com.worldofwealth.utils.DBHelper;
import cn.jzvd.JzvdStd;
import cz.msebera.android.httpclient.Header;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


public class UpdateFragment extends Fragment {

    private static final String PARM1 = "data";

    //MxVideoPlayer mpw_video_player;
    CallsAdapter callsAdapter;
    ProgressDialog progressDialog;
    AsyncHttpClient client;
    ResponseModal object;
    JzvdStd jzvdStd;
    PlayerView playerView;
    SimpleExoPlayer player;
    TextView postdesc;
    TextView posttitle;
    ImageView imageView;
    RecyclerView recyclerView;

    public UpdateFragment() {
    }


    // TODO: Rename and change types and number of parameters
    public static UpdateFragment newInstance(String objectResponseInString) {
        UpdateFragment fragment = new UpdateFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PARM1, objectResponseInString);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update, container, false);
         postdesc = view.findViewById(R.id.postdesc);
         posttitle = view.findViewById(R.id.posttitle);
         recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        imageView = view.findViewById(R.id.imageView);

        playerView = view.findViewById(R.id.player_view);
        player = new SimpleExoPlayer.Builder(getActivity()).build();
        playerView.requestFocus();
        playerView.setPlayer(player);

        initializePlayer();


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {

            if (playerView != null) {
                initializePlayer();
                playerView.onResume();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23 || player == null) {

            if (playerView != null) {
                initializePlayer();
                playerView.onResume();
            }
        }
    }


    private void initializePlayer() {
        player = new SimpleExoPlayer.Builder(getActivity()).build();
        playerView.requestFocus();
        playerView.setPlayer(player);

        if (getArguments() != null) {
            String respString = getArguments().getString(PARM1);
            if (respString != null) {
                object = new Gson().fromJson(respString, ResponseModal.class);

                if(object != null) {
                    // Produces DataSource instances through which media data is loaded.
                    DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getActivity(),
                            Util.getUserAgent(getActivity(), "lion"));
// This is the MediaSource representing the media to be played.
                    MediaSource videoSource =
                            new ProgressiveMediaSource.Factory(dataSourceFactory)
                                    .createMediaSource(Uri.parse(object.mediaurl));
// Prepare the player with the source.
                    player.prepare(videoSource);


//                    jzvdStd = view.findViewById(R.id.jzvdVideoPlayerupdate);
//                    MxVideoPlayer mpw_video_player = view.findViewById(R.id.mpw_video_player);

                    callsAdapter = new CallsAdapter(getActivity(),object.questions);
                    recyclerView.setAdapter(callsAdapter);

                    postdesc.setText(object.description);
                    posttitle.setText(object.title);
                    if (object.title.length() > 0) {
                        posttitle.setVisibility(View.VISIBLE);
                    }
                    if (object.description.length() > 0) {
                        postdesc.setVisibility(View.VISIBLE);
                    }
                    if (object.mediatype.equals("Video")) {
                        /*mpw_video_player.startPlay(("https://myvoiceblob.blob.core.windows.net/myvoiceblobcontainer/VID-20200508-WA0096.mp4"),
                                MxVideoPlayer.SCREEN_LAYOUT_NORMAL, "");
                        mpw_video_player.setVisibility(View.VISIBLE);*/
//                        jzvdStd.setUp(object.mediaurl, object.title, JzvdStd.SCREEN_NORMAL);

                        //jzvdStd.startVideo();
                        playerView.setVisibility(View.VISIBLE);
                    } else if (object.mediatype.equals("Image")) {
                        imageView.setVisibility(View.VISIBLE);
                        Glide.with(getActivity())
                                .load(object.mediaurl)
                                .into(imageView);
                    }
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("TEST","onPause");
        if (Util.SDK_INT <= 23) {
            if (playerView != null) {
                playerView.onPause();
            }
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("TEST","onStop");
        if (Util.SDK_INT > 23) {
            if (playerView != null) {
                playerView.onPause();
            }
            releasePlayer();
        }
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if (this.isVisible())
        {
            if (!isVisibleToUser)   // If we are becoming invisible, then...
            {
                //pause or stop video
                if (Util.SDK_INT > 23) {
                    if (playerView != null) {
                        playerView.onPause();
                    }
                    releasePlayer();
                }
            }

            if (isVisibleToUser) // If we are becoming visible, then...
            {
                //play your video
            }
        }
    }

    class CallsAdapter extends RecyclerView.Adapter<CallsAdapter.ViewHolder> {
        DBHelper dbHelper;
        User user;
        private List<ResponseModal.Questions> questionsModel = new ArrayList();
        // Context is not used here but may be required to
        // perform complex operations or call methods from outside
        private Context context;

        // Constructor
        public CallsAdapter(Context context, List<ResponseModal.Questions> questionsModel) {
            this.context = context;
            this.questionsModel = questionsModel;
        }



        // Invoked by layout manager to replace the contents of the views
        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            ResponseModal.Questions question = questionsModel.get(position);
            holder.showCallDetails(question,position);
        }

        @Override
        public int getItemCount() {
            return questionsModel.size();
        }

        // Invoked by layout manager to create new views
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // Attach layout for single cell
            dbHelper = new DBHelper(context);
            user = dbHelper.getUserData();
            int layout = R.layout.update_like_dislike;
            View v = LayoutInflater
                    .from(parent.getContext())
                    .inflate(layout, parent, false);
            return new ViewHolder(v);
        }

        // Reference to the views for each items to display desired information
        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView ilike, idislike;
            private TextView questionTitle, likecounttext, dislikecounttext;

            public ViewHolder(View itemView) {
                super(itemView);
                // Initiate view
                questionTitle = itemView.findViewById(R.id.callerName);
                likecounttext = itemView.findViewById(R.id.likecounttext);
                dislikecounttext = itemView.findViewById(R.id.dislikecounttext);

                ilike = itemView.findViewById(R.id.ilike);
                idislike = itemView.findViewById(R.id.idislike);


            }


            public void showCallDetails(final ResponseModal.Questions question,final int position) {
                // Attach values for each item
                String callerName = question.questiontitle;
                questionTitle.setText(callerName);
                if (question.upvote.contains(user.getUid())) {
                    idislike.setImageResource(R.drawable.icon_dislike_blue);
                }
                if (question.downvote.contains(user.getUid())) {
                    ilike.setImageResource(R.drawable.icon_like_blue);
                }
                JSONArray upArray = new JSONArray(question.upvote);
                JSONArray downArray = new JSONArray(question.downvote);
                likecounttext.setText(String.valueOf(upArray.length()));
                dislikecounttext.setText(String.valueOf(downArray.length()));
                ilike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String url = CommonUtil.mbaseurl + "Interview/PostHomeUpdateVote?userid=" + user.getUid() + "&vote=upvote&doctypeid=" + object.id + "&questionid=" + question.questionid;
                        LikeDislikeClick(url,position);
                    }
                });
                idislike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String url = CommonUtil.mbaseurl + "Interview/PostHomeUpdateVote?userid=" + user.getUid() + "&vote=downvote&doctypeid=" + object.id + "&questionid=" + question.questionid;
                        LikeDislikeClick(url,position);
                    }
                });
            }

            private void LikeDislikeClick(String url, int position) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage(getString(R.string.please_wait));
                progressDialog.setTitle(getString(R.string.app_name));
                progressDialog.show();
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                client = new AsyncHttpClient();
                client.post(getActivity(),
                        url, null,
                        "application/json", getResponseHandler("LikeDisLike",position));
            }

            private ResponseHandlerInterface getResponseHandler(final String connectionfor,final int position) {
                return new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        progressDialog.cancel();
                        if (connectionfor.contains("Update") || connectionfor.contains("LikeDisLike")) {
                            String response = new String(responseBody);
                            //likecounttext.setText(String.valueOf(upArray.length()));
                            try {
                                //JSONObject jsonObject = new JSONObject(response);
                                ResponseModal responseQuestions = new Gson().fromJson(response, ResponseModal.class);
                                List<ResponseModal.Questions> updatedQuestions = responseQuestions.questions;
                                questionsModel = updatedQuestions;
                                notifyDataSetChanged();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        progressDialog.cancel();
                        System.out.println("Error");
                    }
                };
            }

        }

    }




}