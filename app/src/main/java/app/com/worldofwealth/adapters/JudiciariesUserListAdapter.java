package app.com.worldofwealth.adapters;


import android.content.Context;
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


import app.com.worldofwealth.R;
import app.com.worldofwealth.models.User;
import app.com.worldofwealth.utils.CommonUtil;
import app.com.worldofwealth.utils.DBHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class JudiciariesUserListAdapter extends RecyclerView.Adapter<JudiciariesUserListAdapter.ViewHolder> {
    private static ArrayList<User> userlist;
    Context context;
    User user;
    DBHelper dbHelper;
    String userType;

    public JudiciariesUserListAdapter(Context context, ArrayList<User> userlist, User user, String usertype) {
        this.userlist = userlist;
        this.context = context;
        this.user = user;
        dbHelper = new DBHelper(context);
        user = dbHelper.getUserData();
        this.userType = usertype;
    }


    @Override
    public JudiciariesUserListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.user_row, null);
        JudiciariesUserListAdapter.ViewHolder viewHolder = new JudiciariesUserListAdapter.ViewHolder(itemLayoutView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(JudiciariesUserListAdapter.ViewHolder viewHolder, final int i) {

        final User user = userlist.get(i);
        viewHolder.username.setText(user.getFirstname());
        viewHolder.detail.setText(user.getUsertype());
        viewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  Intent intent=new Intent(context, DirectoryProfileDetails.class);
                intent.putExtra("id", user.getUid());
                intent.putExtra("name",user.getFirstname()+" "+user.getLastname());
                intent.putExtra("type", userType);
                context.startActivity(intent);*/
            }
        });
        viewHolder.menuicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          //      showPopupWindow(v,user);
            }
        });


    }

    private void showPopupWindow(View v, final User user) {
        final   String followinguserd=user.getUid();
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
        popup.getMenuInflater().inflate(R.menu.judiciary_poupup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            public boolean onMenuItemClick(MenuItem item) {
                if(item.getTitle().equals("Follow")){
                    follow(followinguserd);
                }
                if(item.getTitle().equals("Give me Rating")){
                   /* Intent intent = new Intent(context, DirectoryProfileDetails.class);
                    intent.putExtra("id", user.getUid());
                    context.startActivity(intent);*/
                }
                //Toast.makeText(context, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        popup.show();
    }

    private void follow(String followinguserd) {
        DBHelper dbHelper=new DBHelper(context);
        User users;
        users=dbHelper.getUserData();
        String localuserid=users.getUid();
        System.out.println("uidfollow :"+followinguserd +" : "+localuserid);
        try {
            JSONObject jsonParams = new JSONObject();
            StringEntity entity = null;
            AsyncHttpClient client = new AsyncHttpClient();
            entity = new StringEntity(jsonParams.toString());
            String url = CommonUtil.mbaseurl + "users/PostFollowing?followeruserid=" +localuserid+ "&followinguserid=" +followinguserd+ "&type=follow";
            Log.d("URL",url);
            client.post(context,
                    url, entity,
                    "application/json", getResponseHandler("follow"));
            System.out.println("inputtest :" + jsonParams.toString());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in inserting" + e.toString());

        }
    }

    private ResponseHandlerInterface getResponseHandler(final String connectionfor) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] resp) {
                if (connectionfor.contains("follow")){
                    String response = new String(resp);
                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray js_array = jsonObject.getJSONArray("following");
                        if (js_array.length() >= 1){
                            CommonUtil.showToast(context, context.getString(R.string.success));
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                CommonUtil.showToast(context, context.getString(R.string.something_went_wrong));
            }
        };
    }


    @Override
    public int getItemCount() {
        return userlist.size();
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

        TextView initial, detail, username;
        ImageView userimage,menuicon;
        RelativeLayout relativeLayout;


        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            initial = (TextView) itemLayoutView
                    .findViewById(R.id.initial);
            detail = (TextView) itemLayoutView
                    .findViewById(R.id.detail);
            username = (TextView) itemLayoutView
                    .findViewById(R.id.username);
            userimage = (ImageView) itemLayoutView
                    .findViewById(R.id.userimag);
            relativeLayout=(RelativeLayout)itemLayoutView
                    .findViewById(R.id.first);
            menuicon = (ImageView) itemLayoutView
                    .findViewById(R.id.menu);


        }

    }


}
