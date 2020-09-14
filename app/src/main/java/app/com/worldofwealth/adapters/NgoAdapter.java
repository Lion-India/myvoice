package app.com.worldofwealth.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import app.com.worldofwealth.CommentsActivity;
import app.com.worldofwealth.DescriptionActivity;
import app.com.worldofwealth.FullScreenVideoActivity;
import app.com.worldofwealth.R;
import app.com.worldofwealth.models.Post;
import app.com.worldofwealth.models.User;
import app.com.worldofwealth.utils.CommonUtil;
import app.com.worldofwealth.utils.DBHelper;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;


public class NgoAdapter extends RecyclerView.Adapter<NgoAdapter.ViewHolder> {
    String userid;
    DBHelper dbHelper;
    User user;
    Post post;
    private List<Post> postList;
    private Context context;


    public NgoAdapter(Context context, ArrayList<Post> postlist) {
        this.postList = postlist;
        this.context = context;
        dbHelper = new DBHelper(context);
    }

    @Override
    public NgoAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.post_row, null);
        NgoAdapter.ViewHolder viewHolder = new NgoAdapter.ViewHolder(itemLayoutView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final NgoAdapter.ViewHolder holder, final int position) {

        final Post post = postList.get(position);
        user = dbHelper.getUserData();
        userid = user.getUid();
        System.out.println("postlist :" + postList.size());
        if (postList.size() > 0) {
            holder.tposttitle.setText(post.getTitle());
            holder.tpostdesc.setText(post.getDesc());
            holder.tpusername.setText(post.getCreatedbyname().equals("null") ? "---" : post.getCreatedbyname());
            holder.tpusertype.setText(post.getCreatedbytype().equals("null") ? "---" : post.getCreatedbytype());
            holder.tpostdate.setText(CommonUtil.parseDate(post.getCreateddate()));
            holder.ipostimage.setVisibility(View.GONE);
            holder.player_view.setVisibility(View.GONE);

            //for userprofile image
            String userImageUrl = post.getCreatedbyimage();
            if (userImageUrl != null && !userImageUrl.equals("null") && !userImageUrl.isEmpty()) {
                Glide.with(this.context).load(post.getCreatedbyimage())
                        .apply(RequestOptions.circleCropTransform())
                        .into(holder.puserimg);
//                Glide.with(this.context)
//                        .load(post.getCreatedbyimage()) // image url
//                        .placeholder(R.drawable.background)
//                        .error(R.drawable.background)
//                        //.override(200, 200); // resizing
//                        //  .centerCrop()
//                        .into(holder.puserimg);
            }

            //for Image posts
            if (!post.getImagurl().equals("null")) {
                Glide.with(this.context)
                        .load(post.getImagurl()) // image url
                        .placeholder(R.drawable.background)
                        .error(R.drawable.background)
                        //.override(200, 200); // resizing
                        //  .centerCrop()
                        .into(holder.ipostimage);
                holder.ipostimage.setVisibility(View.VISIBLE);
            }

            //for Video
            if (!post.getVideourl().equals("null")) {
                /*holder.player_view.setUp(post.getVideourl(), post.getTitle());
                holder.player_view.setVisibility(View.VISIBLE);*/

                long interval = 2000 * 1000;
                RequestOptions options = new RequestOptions().frame(interval);
                Glide.with(context).asBitmap()
                        .load(post.getVideourl())
                        .apply(options)
                        .centerCrop()
                        .placeholder(R.drawable.background)
                        .into(holder.videoImageView);
                holder.videoImageView.setVisibility(View.VISIBLE);
                holder.playImageView.setVisibility(View.VISIBLE);
            }
            //for  Audio
            if (!post.getAudiourl().equals("null")) {
                holder.player_view.setUp(post.getAudiourl(), post.getTitle());
                holder.player_view.setVisibility(View.VISIBLE);
            }

            //user likes
            JSONArray upvotearr = null;
            if (post.getUpvote() != null && !post.getUpvote().equals("null")) {
                try {
                    upvotearr = new JSONArray(post.getUpvote());
                    for (int i = 0; i < upvotearr.length(); i++) {
                        JSONObject jsonObject = new JSONObject(upvotearr.getJSONObject(i).toString());
                        String likeduser_id = jsonObject.getString("userid");
                        if (likeduser_id.equals(userid)) {
                            holder.like.setImageResource(R.drawable.icon_like_blue);
                        }
                    /*else{
                        holder.like.setImageResource(R.drawable.icon_like_grey);
                    }*/
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

            holder.videoImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                JzvdStd.startFullscreenDirectly(context, JzvdStd.class, post.getVideourl(), post.getTitle());
                    Intent intent = new Intent(context, FullScreenVideoActivity.class);
                    intent.putExtra("VIDEO_DETAILS", post);
                    context.startActivity(intent);


//                JzvdStd.startFullscreenDirectly(context, JzvdStd.class, "http://2449.vod.myqcloud.com/2449_22ca37a6ea9011e5acaaf51d105342e3.f20.mp4", "TEsting");
//                JZVideoPlayerStandard.startFullscreen(this,
//                        JZVideoPlayerStandard.class,
//                        "http://2449.vod.myqcloud.com/2449_22ca37a6ea9011e5acaaf51d105342e3.f20.mp4",
//                        "Video title");
                }
            });
            holder.ipostimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, FullScreenVideoActivity.class);
                    intent.putExtra("IMAGE_DETAILS", post);
                    context.startActivity(intent);
                }
            });

            holder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.like.setImageResource(R.drawable.icon_like_blue);
                    holder.dislike.setImageResource(R.drawable.icon_dislike_grey);
                    likeDislikeClicked(context, position, post, "upvote");

                }
            });

            holder.dislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.dislike.setImageResource(R.drawable.icon_dislike_blue);
                    holder.like.setImageResource(R.drawable.icon_like_grey);
                    likeDislikeClicked(context, position, post, "downvote");
                }
            });

            holder.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent yourIntent = new Intent(context, CommentsActivity.class);
                    Bundle b = new Bundle();
                    b.putString("Type", "Post");
                    b.putSerializable(CommentsActivity.KEY, post);
                    yourIntent.putExtras(b);
                    context.startActivity(yourIntent);
                }
            });
            holder.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String mediaUrl ="";

                    if(!post.getImagurl().equals("null")){
                        mediaUrl=post.getShareurl();
                    } else if (!post.getVideourl().equals("null")){
                        mediaUrl=post.getShareurl();
                    }
                    String texttoShare=Html.fromHtml("<b>" + "Title  : " + "</b>" + post.getTitle())
                            + "\n" + (Html.fromHtml("<b>" + "" + "</b>" ))
                            + "\n" + (Html.fromHtml("<b>" + "Description  : " + "</b>" + post.getDesc()))
                            + "\n" + (Html.fromHtml("<b>" + "" + "</b>" ))
                            + "\n" + (Html.fromHtml("<b>" + "Media URL  : " + "</b>" +mediaUrl))
                            + "\n" + (Html.fromHtml("<b>" + "" + "</b>" ))
                            + "\n" + "click here to download "+context.getString(R.string.app_name)+" : " + context.getString(R.string.appUrl);
                    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.logo_like_it_or_not);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, texttoShare);
                    intent.setType("image/*");
                    String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "logo_like_it_or_not", null);
                    Uri imageUri = Uri.parse(path);
                    intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    context.startActivity(Intent.createChooser(intent , "Share"));
                }
            });

            holder.report.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Report(post);
                }
            });

            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DescriptionActivity.class);
                    intent.putExtra(DescriptionActivity.KEY, post);
                    context.startActivity(intent);
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

        /*String topscore = String.valueOf(upvotearr.length() + downvotearr.length() + commentarray.length());
        if (topscore != null && !topscore.equals("null"))
            holder.tscore.setText("The LION’s Score:\n" +
                    topscore);*/
            String likecount = String.valueOf(upvotearr.length());
            if (likecount != null && !likecount.equals("null"))
                holder.likecounttext.setText(likecount);
            String dislikecount = String.valueOf(downvotearr.length());
            if (dislikecount != null && !dislikecount.equals("null"))
                holder.dislikecounttext.setText(dislikecount);
            String commentscount = String.valueOf(commentarray.length());
            if (commentscount != null && !commentscount.equals("null"))
                holder.commentscounttext.setText(commentscount);
            holder.tipmenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopupWindow(v);
                }
            });
        } else {
            holder.nodatafoundtext.setVisibility(View.VISIBLE);
        }

    }

    private void showPopupWindow(View v) {

        PopupMenu popup = new PopupMenu(context, v);
        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        popup.getMenuInflater().inflate(R.menu.poupup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().equals("Book Mark")) {
                    bookMarkPost();

                }
                return true;
            }
        });
        popup.show();
    }

    private void Report(Post post) {
        try {
            JSONObject jsonParams = new JSONObject();
            StringEntity entity = null;
            AsyncHttpClient client = new AsyncHttpClient();
            entity = new StringEntity(jsonParams.toString());
            String url = CommonUtil.mbaseurl + "Post/PostReport?id=" + post.getId() + "&userid=" + user.getUid();
            Log.d("URL",url);
            client.post(context,
                    url, entity,
                    "application/json", getResponseHandler("reportstatus"));
            System.out.println("reporttest :" + jsonParams.toString());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in inserting" + e.toString());

        }
    }

    private void bookMarkPost() {
        try {
            JSONObject jsonParams = new JSONObject();
            StringEntity entity = null;
            AsyncHttpClient client = new AsyncHttpClient();
            entity = new StringEntity(jsonParams.toString());
            String url = CommonUtil.mbaseurl + "Post/BookmarkPost?PostId=" + post.getId() + "&userid=" + user.getUid() + "&Bookmark=bookmark";
            //Log.d("URL",url);
            client.post(context,
                    url, entity,
                    "application/json", getResponseHandler("bookmark"));
            //System.out.println("inputtest :" + jsonParams.toString());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in inserting" + e.toString());

        }
    }

    private ResponseHandlerInterface getResponseHandler(final String connectionfor) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (connectionfor.contains("bookmark")) {
                    String response = new String(responseBody);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray ja_data = jsonObject.getJSONArray("bookmark");
                        if (ja_data.length() >= 1) {
                            CommonUtil.showToast(context, "success");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (connectionfor.contains("reportstatus")){
                    String response = new String(responseBody);
                    String Message;
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Message = jsonObject.getString("Message");
                        if (Message.contains("Successfull")) {
                            CommonUtil.showToast(context, "Post reported");
                        } else if (Message.contains("Data Already Exists!")) {
                            CommonUtil.showToast(context, "Already reported");
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                System.out.println("Error");
            }
        };
    }


    private void likeDislikeClicked(final Context context, final int position, Post post, String type) {
        String url = "Opinion/PostVote?userid=" + user.getUid() + "&vote=" + type + "&doctypeid=" + post.getId() + "&doctype=Post";
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
                                    Post op = ParseStringtoOpinion(job);
                                    postList.set(position, op);
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

    private Post ParseStringtoOpinion(JSONObject job) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return post;

    }


    @Override
    public int getItemCount() {
        return postList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tpusername, tpusertype, tposttitle, tpostdesc, tpostdate, tscore, likecounttext, dislikecounttext, commentscounttext, nodatafoundtext;
        ImageView ipostimage, tipmenu, like, dislike, comment, share, videoImageView, playImageView, puserimg, report;
        JCVideoPlayerStandard player_view;
        RelativeLayout relativeLayout;

        public ViewHolder(View view) {
            super(view);

            tposttitle = view.findViewById(R.id.posttitle);
            tpostdesc = view.findViewById(R.id.postdesc);
            tpusername = view.findViewById(R.id.pusername);
            tpusertype = view.findViewById(R.id.pusertype);
            tipmenu = view.findViewById(R.id.ipmenu);
            tpostdate = view.findViewById(R.id.postdate);
            ipostimage = view.findViewById(R.id.postimage);
            player_view = view.findViewById(R.id.player_view);
            like = view.findViewById(R.id.ilike);
            dislike = view.findViewById(R.id.idislike);
            comment = view.findViewById(R.id.icomments);
            share = view.findViewById(R.id.ishare);
            //tscore = (TextView) view.findViewById(R.id.tscore);
            likecounttext = view.findViewById(R.id.likecounttext);
            dislikecounttext = view.findViewById(R.id.dislikecounttext);
            commentscounttext = view.findViewById(R.id.commentscounttext);
            relativeLayout = view.findViewById(R.id.layout1);

            videoImageView = (ImageView) view.findViewById(R.id.videoImage);
            playImageView = (ImageView) view.findViewById(R.id.playImageView);

            puserimg = (ImageView) view.findViewById(R.id.puserimg);
            report = (ImageView) view.findViewById(R.id.ireport);
        }

    }


}