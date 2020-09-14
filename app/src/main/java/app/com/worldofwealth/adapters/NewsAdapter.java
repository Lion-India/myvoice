package app.com.worldofwealth.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import app.com.worldofwealth.CommentsActivity;
import app.com.worldofwealth.DescriptionActivity;


import app.com.worldofwealth.R;
import app.com.worldofwealth.models.Post;
import app.com.worldofwealth.models.User;
import app.com.worldofwealth.utils.CommonUtil;
import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {

    private List<Post> postList;
    private Context context;

    User user;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tpusername, tpusertype, tposttitle, tpostdesc, tpostdate, tscore, likecounttext, dislikecounttext, commentscounttext, nodatafoundtext;
        ImageView ipostimage, tipmenu, like, dislike, comment, share, puserimg;
        JCVideoPlayerStandard player_view;
        RelativeLayout relativeLayout;

        public MyViewHolder(View view) {
            super(view);
            tposttitle = (TextView) view.findViewById(R.id.posttitle);
            tpostdesc = (TextView) view.findViewById(R.id.postdesc);
            tpusername = (TextView) view.findViewById(R.id.pusername);
            tpusertype = (TextView) view.findViewById(R.id.pusertype);
            tipmenu = (ImageView) view.findViewById(R.id.ipmenu);
            tpostdate = (TextView) view.findViewById(R.id.postdate);
            ipostimage = (ImageView) view.findViewById(R.id.postimage);
            player_view = (JCVideoPlayerStandard) view.findViewById(R.id.player_view);
            like = (ImageView) view.findViewById(R.id.ilike);
            dislike = (ImageView) view.findViewById(R.id.idislike);
            comment = (ImageView) view.findViewById(R.id.icomments);
            share = (ImageView) view.findViewById(R.id.ishare);
            puserimg = (ImageView) view.findViewById(R.id.puserimg);
            //tscore = (TextView) view.findViewById(R.id.tscore);
            relativeLayout = (RelativeLayout) view.findViewById(R.id.layout1);
            likecounttext = (TextView) view.findViewById(R.id.likecounttext);
            dislikecounttext = (TextView) view.findViewById(R.id.dislikecounttext);
            commentscounttext = (TextView) view.findViewById(R.id.commentscounttext);

        }
    }


    public NewsAdapter(Context ctx, List<Post> postList, User user) {
        this.postList = postList;
        this.context = ctx;
        this.user = user;
    }

    @Override
    public NewsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_row, parent, false);

        return new NewsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final NewsAdapter.MyViewHolder holder, final int position) {
        final Post post = postList.get(position);
        System.out.println("postList :" + postList.size());
        if(postList.size()>0) {
            holder.tposttitle.setText(post.getTitle());
            holder.tpostdesc.setText(post.getDesc());
            holder.puserimg.setVisibility(View.GONE);
            holder.tpusername.setVisibility(View.GONE);
            holder.ipostimage.setVisibility(View.GONE);
            holder.player_view.setVisibility(View.GONE);
            holder.tpostdate.setText(post.getSourcename().equals("null") ? "---" : post.getSourcename());
            //for Image posts
            if (!post.getImagurl().equals("null")) {
                Glide.with(this.context)
                        .load(post.getImagurl()) // image url
                        //   .placeholder(R.drawable.background)
                        // .error(R.drawable.background)
                        //.override(200, 200); // resizing
                        //  .centerCrop()
                        .into(holder.ipostimage);
                holder.ipostimage.setVisibility(View.VISIBLE);
            }


            //user likes
            JSONArray upvotearr = null;
            if (post.getUpvote() != null && !post.getUpvote().equals("null")) {
                try {
                    upvotearr = new JSONArray(post.getUpvote());
                    for (int i = 0; i < upvotearr.length(); i++) {
                        JSONObject jsonObject = new JSONObject(upvotearr.getJSONObject(i).toString());
                        String likeduser_id = jsonObject.getString("userid");
                        if (likeduser_id.equals(user.getUid())) {
                            holder.like.setImageResource(R.drawable.icon_like_blue);
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
                        if (dislikeuser_id.equals(user.getUid())) {
                            holder.dislike.setImageResource(R.drawable.icon_dislike_blue);
                        }
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
                    likeDislikeClicked(context, position, post, "upvote", user.getUid());

                }
            });

            holder.dislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.dislike.setImageResource(R.drawable.icon_dislike_blue);
                    holder.like.setImageResource(R.drawable.icon_like_grey);
                    likeDislikeClicked(context, position, post, "downvote", user.getUid());
                }
            });

            holder.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent yourIntent = new Intent(context, CommentsActivity.class);
                    Bundle b = new Bundle();
                    b.putString("Type", "News");
                    b.putSerializable(CommentsActivity.KEY, post);
                    yourIntent.putExtras(b);
                    context.startActivity(yourIntent);
                }
            });
            holder.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(android.content.Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject test");
                    i.putExtra(android.content.Intent.EXTRA_TEXT, "extra text that you want to put");
                    context.startActivity(Intent.createChooser(i, "Share via"));
                }
            });
            JSONArray commentarray = null;
            if (post.getComments() != null && !post.getComments().equals("null")) {
                try {
                    commentarray = new JSONArray(post.getComments());


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

      /*  if(upvotearr==null&&downvotearr==null&&commentarray==null){
            holder.tscore.setText("The LION’s Score:\n" +
                    "0");
        }
        else {
            int topscore = upvotearr.length() + downvotearr.length() + commentarray.length();
            if (topscore > 0)
                holder.tscore.setText("The LION’s Score:\n" +
                        topscore);
        }*/
            String likecount = String.valueOf(upvotearr.length());
            if (likecount != null && !likecount.equals("null"))
                holder.likecounttext.setText(likecount);
            String dislikecount = String.valueOf(downvotearr.length());
            if (dislikecount != null && !dislikecount.equals("null"))
                holder.dislikecounttext.setText(dislikecount);
            String commentscount = String.valueOf(commentarray.length());
            if (commentscount != null && !commentscount.equals("null"))
                holder.commentscounttext.setText(commentscount);

            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DescriptionActivity.class);
                    intent.putExtra(DescriptionActivity.KEY, post);
                    context.startActivity(intent);
                }
            });
        }else {
            holder.nodatafoundtext.setVisibility(View.VISIBLE);
        }
    }

    private void likeDislikeClicked(final Context context, final int position, Post post, String type, String uid) {
        String url = "Opinion/PostVote?userid=" + user.getUid() + "&vote=" + type + "&doctypeid=" + post.getId() + "&doctype=News";

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
                                    Post op = CommonUtil.ParseNewsStringtoPost(job);
                                    postList.set(position, op);
                                    notifyDataSetChanged();
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


    @Override
    public int getItemCount() {
        return postList.size();
    }
}