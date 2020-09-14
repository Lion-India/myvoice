package app.com.worldofwealth.adapters;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import app.com.worldofwealth.CommentsActivity;
import app.com.worldofwealth.R;
import app.com.worldofwealth.agora.openlive.ui.MainActivity;
import app.com.worldofwealth.models.Interview;
import app.com.worldofwealth.models.User;
import app.com.worldofwealth.utils.CommonUtil;
import app.com.worldofwealth.utils.DBHelper;
import cz.msebera.android.httpclient.Header;
import hb.xvideoplayer.MxVideoPlayer;
import hb.xvideoplayer.MxVideoPlayerWidget;

public class OngoingInterViewAdapter extends RecyclerView.Adapter<OngoingInterViewAdapter.MyViewHolder> {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private List<Interview> ongoinginterviewlist;
    private Context context;
    String userid;
    DBHelper dbHelper;
    User user;


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView ttitle, tdesc, tcreatedby, tusertype,tpostdate, tscore, likecounttext, dislikecounttext, commentscounttext;
        ImageView ipostimage, tipmenu, like, dislike, comment, share;;
        FloatingActionButton ifab;
        static MxVideoPlayer mpw_video_player;

        public MyViewHolder(View view) {
            super(view);
            ttitle = (TextView) view.findViewById(R.id.posttitle);
            tdesc = (TextView) view.findViewById(R.id.postdesc);
            tcreatedby = (TextView) view.findViewById(R.id.pusername);
            tusertype = (TextView) view.findViewById(R.id.pusertype);
            ifab = (FloatingActionButton) view.findViewById(R.id.ifab);
            mpw_video_player = (MxVideoPlayerWidget) view.findViewById(R.id.mpw_video_player);
            tipmenu = (ImageView) view.findViewById(R.id.ipmenu);
            tpostdate = (TextView) view.findViewById(R.id.postdate);
            ipostimage = (ImageView) view.findViewById(R.id.postimage);
            like = (ImageView) view.findViewById(R.id.ilike);
            dislike = (ImageView) view.findViewById(R.id.idislike);
            comment = (ImageView) view.findViewById(R.id.icomments);
            share = (ImageView) view.findViewById(R.id.ishare);
            //tscore = (TextView) view.findViewById(R.id.tscore);
            likecounttext = (TextView) view.findViewById(R.id.likecounttext);
            dislikecounttext = (TextView) view.findViewById(R.id.dislikecounttext);
            commentscounttext = (TextView) view.findViewById(R.id.commentscounttext);

        }

        public static void closeAllVideoes() {
            mpw_video_player.releaseAllVideos();
        }
    }


    public OngoingInterViewAdapter(Context ctx, List<Interview> ongoinginterviewlist) {
        this.ongoinginterviewlist = ongoinginterviewlist;
        this.context = ctx;
        dbHelper = new DBHelper(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Interview interview = ongoinginterviewlist.get(position);
        user = dbHelper.getUserData();
        userid = user.getUid();
        String mstartTime = "";
        String mstartdate = "";
        String mendTime = "";
        String menddate = "";
        try {
            Date starttime = dateFormat.parse(interview.getStarttime().replace("T", " "));
            Date startdate = dateFormat.parse(interview.getStartdate().replace("T", " "));
            Date endtime = dateFormat.parse(interview.getEndtime().replace("T", " "));
            Date enddate = dateFormat.parse(interview.getEnddate().replace("T", " "));
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd-MMM");
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("hh:mm a");
            SimpleDateFormat dateFormat3 = new SimpleDateFormat("dd-MMM");
            mstartTime = dateFormat.format(starttime.getTime());
            mstartdate = dateFormat1.format(startdate);
            mendTime = dateFormat2.format(endtime.getTime());
            menddate = dateFormat3.format(enddate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.ttitle.setText(interview.getTitle());
        holder.tdesc.setText(interview.getDesc() + "\n \n" +
                "Start Time: " + mstartdate+" "+mstartTime+"\n" +
                "End Time: " + menddate+" "+mendTime);
        holder.tdesc.setMaxLines(5);
        holder.tusertype.setText(interview.getCreatedbytype());
        holder.tcreatedby.setText(interview.getCreatedbyname());
       /* try {
            JSONArray jarr = new JSONArray(interview.getInvitedusers());
            String username = jarr.getJSONObject(0).getString("firstname") + " " +
                    jarr.getJSONObject(0).getString("lastname");
            holder.tcreatedby.setText(username);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        // Showing join button based on the timings
        try {

            Date starttime = dateFormat.parse(interview.getStarttime().replace("T", " "));
            Date endtime = dateFormat.parse(interview.getEndtime().replace("T", " "));
            Date currentdate = new Date();
            if (currentdate.after(starttime) && currentdate.before(endtime)) {
                holder.ifab.setVisibility(View.VISIBLE);
                holder.ifab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent yourIntent = new Intent(context, MainActivity.class);
                        yourIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Bundle b = new Bundle();
                        b.putSerializable(MainActivity.InterViewKey, interview);
                        yourIntent.putExtras(b);
                        context.startActivity(yourIntent);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (interview.isIspublished()) {
            System.out.println("Video url : "+CommonUtil.GetMP4fileName(CommonUtil.mvideourl + interview.getVideoid()));
            holder.mpw_video_player.startPlay(CommonUtil.GetMP4fileName(CommonUtil.mvideourl + interview.getVideoid()),
                    MxVideoPlayer.SCREEN_LAYOUT_NORMAL, "");
            holder.mpw_video_player.setVisibility(View.VISIBLE);
        }

        //user likes
        JSONArray upvotearr = null;
        if (interview.getUpvote() != null && !interview.getUpvote().equals("null")) {
            try {
                upvotearr = new JSONArray(interview.getUpvote());
                for (int i = 0; i < upvotearr.length(); i++) {
                    JSONObject jsonObject = new JSONObject(upvotearr.getJSONObject(i).toString());
                    String likeduser_id = jsonObject.getString("userid");
                    if (likeduser_id.equals(userid)) {
                        holder.like.setImageResource(R.drawable.icon_like_blue);
                    }
                    /*else
                        {
                        holder.like.setImageResource(R.drawable.icon_like_grey);
                    }*/
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //user dislike
        JSONArray downvotearr = null;
        if (interview.getDownvote() != null && !interview.getDownvote().equals("null")) {
            try {
                downvotearr = new JSONArray(interview.getDownvote());
                for (int i = 0; i < downvotearr.length(); i++) {
                    JSONObject jsonObject = new JSONObject(downvotearr.getJSONObject(i).toString());
                    String dislikeuser_id = jsonObject.getString("userid");
                    if (dislikeuser_id.equals(userid)) {
                        holder.dislike.setImageResource(R.drawable.icon_dislike_blue);
                    }
                    /*else{
                        holder.dislike.setImageResource(R.drawable.icon_dislike_grey);
                    }*/
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.like.setImageResource(R.drawable.icon_like_blue);
                holder.dislike.setImageResource(R.drawable.icon_dislike_grey);
                likeDislikeClicked(context, position, interview, "upvote");
            }
        });

        holder.dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.dislike.setImageResource(R.drawable.icon_dislike_blue);
                holder.like.setImageResource(R.drawable.icon_like_grey);
                likeDislikeClicked(context, position, interview, "downvote");
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent yourIntent = new Intent(context, CommentsActivity.class);
                Bundle b = new Bundle();
                b.putString("Type", "InterView");
                b.putSerializable(CommentsActivity.KEY, interview);
                yourIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                yourIntent.putExtras(b);
                context.startActivity(yourIntent);
            }
        });
        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Subject test");
                i.putExtra(Intent.EXTRA_TEXT, "extra text that you want to put");
                context.startActivity(Intent.createChooser(i, "Share via"));
            }
        });

        JSONArray commentarray = null;
        if (interview.getComments() != null && !interview.getComments().equals("null")) {
            try {
                commentarray = new JSONArray(interview.getComments());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        String likecount = String.valueOf(upvotearr.length());
        if (likecount != null && !likecount.equals("null"))
            holder.likecounttext.setText(likecount);
        String dislikecount = String.valueOf(downvotearr.length());
        if (dislikecount != null && !dislikecount.equals("null"))
            holder.dislikecounttext.setText(dislikecount);
        String commentscount = String.valueOf(commentarray.length());
        if (commentscount != null && !commentscount.equals("null"))
            holder.commentscounttext.setText(commentscount);

    }




    private void likeDislikeClicked(final Context context, final int position, Interview interview, String type) {
        String url = "Opinion/PostVote?userid=" + user.getUid() + "&vote=" + type + "&doctypeid=" + interview.getId() + "&doctype=Interview";
        System.out.println("url :" + url);
        try {
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(context.getString(R.string.please_wait));
            progressDialog.setTitle(context.getString(R.string.app_name));
            progressDialog.show();
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            AsyncHttpClient client = new AsyncHttpClient();
            client.post(context, CommonUtil.mbaseurl + url,
                    null, "application/json", new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            String res = new String(responseBody);
                            progressDialog.cancel();
                            try {
                                JSONObject job = new JSONObject(res);
                                if (job.has("Message")) {
                                    CommonUtil.showToast(context, context.getString(R.string.something_went_wrong));
                                } else {
                                    Interview op = ParseStringtoOpinion(job);
                                    ongoinginterviewlist.set(position, op);
                                    notifyDataSetChanged();
                                    //CommonUtil.showToast(mContext, mContext.getString(R.string.success));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                CommonUtil.showToast(context, context.getString(R.string.something_went_wrong));
                            }

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            progressDialog.cancel();
                            CommonUtil.showToast(context, context.getString(R.string.something_went_wrong));
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return interview;
    }

    @Override
    public int getItemCount() {
        return ongoinginterviewlist.size();
    }
}
