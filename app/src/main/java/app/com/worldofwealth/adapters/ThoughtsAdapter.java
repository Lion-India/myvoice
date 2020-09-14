package app.com.worldofwealth.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;



import app.com.worldofwealth.R;
import app.com.worldofwealth.models.Post;
import app.com.worldofwealth.models.Thoughts;
import app.com.worldofwealth.models.User;
import app.com.worldofwealth.utils.DBHelper;

import java.util.ArrayList;
import java.util.List;


public class ThoughtsAdapter extends RecyclerView.Adapter<ThoughtsAdapter.ViewHolder> {
    private List<Thoughts> thoughtsList;
    private Context context;
    String userid;
    DBHelper dbHelper;
    User user;
    Post post;
    Thoughts thoughts;


    public ThoughtsAdapter(Context context, ArrayList<Thoughts> thoughtsList) {
        this.thoughtsList = thoughtsList;
        this.context = context;
        dbHelper = new DBHelper(context);
    }

    @Override
    public ThoughtsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.thoughtcommentlistrow, null);
        ThoughtsAdapter.ViewHolder viewHolder = new ThoughtsAdapter.ViewHolder(itemLayoutView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final ThoughtsAdapter.ViewHolder holder, final int position) {

        thoughts = thoughtsList.get(position);
        user = dbHelper.getUserData();
        userid = user.getUid();


        holder.tposttitle.setText(thoughts.getTitle());
        holder.tpostdesc.setText(thoughts.getDesc());
    }


    @Override
    public int getItemCount() {
        return thoughtsList.size();
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

        public TextView tpusername, tpusertype, tposttitle, tpostdesc, tpostdate, tscore;
        ImageView ipostimage, tipmenu, like, dislike, comment, share;

        public ViewHolder(View view) {
            super(view);
            tposttitle = (TextView) view.findViewById(R.id.posttitle);
            tpostdesc = (TextView) view.findViewById(R.id.postdesc);
            tpusername = (TextView) view.findViewById(R.id.pusername);
            tpusertype = (TextView) view.findViewById(R.id.pusertype);
            tipmenu = (ImageView) view.findViewById(R.id.ipmenu);
            tpostdate = (TextView) view.findViewById(R.id.postdate);


        }

    }


}