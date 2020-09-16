package app.com.worldofwealth.agora.openlive.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;



import app.com.worldofwealth.R;
import app.com.worldofwealth.agora.openlive.model.ConstantApp;
import app.com.worldofwealth.models.Interview;
import app.com.worldofwealth.models.User;
import app.com.worldofwealth.utils.CommonUtil;
import app.com.worldofwealth.utils.DBHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import io.agora.rtc.Constants;

public class MainActivity extends BaseActivity {
    public static final String InterViewKey = "sInterView";
    Interview interview;
    TextView tname, tdesc, tcreatedby;
    JSONArray jsonArray;
    DBHelper dbHelper;
    User user;
    ArrayList<User> userlist = new ArrayList<User>();
    ListView lparticipants;
    Button button_join;
    int role = 0;
    ProgressDialog progressDialog;

    @Override
    public void onBackPressed() {
        //startActivity(new Intent(this, InterviewActivity.class));
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        interview = (Interview) bundle.getSerializable(InterViewKey);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(interview.getTitle());

        tname = findViewById(R.id.tname);
        tdesc = findViewById(R.id.tdesc);
        tcreatedby = findViewById(R.id.tcreatedby);
        lparticipants = findViewById(R.id.lparticipants);
        button_join = findViewById(R.id.button_join);
        tname.setText(interview.getTitle());
        tdesc.setText(interview.getDesc());
        dbHelper = new DBHelper(this);
        user = dbHelper.getUserData();

        try {
            jsonArray = new JSONArray(interview.getInvitedusers());
            userlist = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                String uid = jsonArray.getJSONObject(i).getString("userid");
                String firstname = jsonArray.getJSONObject(i).getString("firstname");
                String lastname = jsonArray.getJSONObject(i).getString("lastname");
                if (interview.getCreatedby().equals(uid)) {
                    tcreatedby.setText("Host by: " + firstname + " " + lastname);
                } else {
                    User user = new User();
                    user.setUid(uid);
                    user.setFirstname(firstname);
                    user.setLastname(lastname);
                    userlist.add(user);
                }
            }
            //  System.out.println("Partcipants: " + userlist.size());
            UserAdapter adapter = new UserAdapter(MainActivity.this, R.layout.partcipant_row, userlist);
            lparticipants.setAdapter(adapter);

            if (CommonUtil.checkUserExistsornot(new JSONArray(interview.getInvitedusers()), user.getUid())) {
                role = (Constants.CLIENT_ROLE_BROADCASTER);
                button_join.setText(getString(R.string.join_as_broadcaster));
            } else {
                role = (Constants.CLIENT_ROLE_AUDIENCE);
                button_join.setText(getString(R.string.join_as_audience));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void initUIandEvent() {
    }

    @Override
    protected void deInitUIandEvent() {
    }

   /* @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                forwardToSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

    public void onClickJoin(View view) {
        // show dialog to choose role
     /*   AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.msg_choose_role);
        builder.setNegativeButton(R.string.label_audience, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.this.forwardToLiveRoom(Constants.CLIENT_ROLE_AUDIENCE);
            }
        });
        builder.setPositiveButton(R.string.label_broadcaster, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.this.forwardToLiveRoom(Constants.CLIENT_ROLE_BROADCASTER);
            }
        });
        AlertDialog dialog = builder.create();

        dialog.show();*/

        //Check the Hoster is started the Interview or not
        String url = CommonUtil.mbaseurl + "Opinion/GetDocumentById?doctypeid=" + interview.getId() + "&doctype=Interview";
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                progressDialog.cancel();
                try {
                    JSONObject object = new JSONObject(response);
                    String videoid = object.getString("videoid");
                    String status = object.getString("status");

                    if (status.equals("Active")) {
                        //Checking hoster is logged in
                        if (interview.getCreatedby().equals(user.getUid())) {
                            if (!CommonUtil.isNullCheck(videoid)) {
                                forwardToLiveRoom(role, "Yes");
                            } else {
                                CommonUtil.showToast(MainActivity.this, getString(R.string.interview_closed));

                            }
                        } else {
                            if (!CommonUtil.isNullCheck(videoid)) {
                                CommonUtil.showToast(MainActivity.this, getString(R.string.interview_not_started));
                            } else {
                                forwardToLiveRoom(role, "No");
                            }
                        }

                    } else {
                        CommonUtil.showToast(MainActivity.this, getString(R.string.interview_closed));
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                CommonUtil.showToast(MainActivity.this, getString(R.string.something_went_wrong));
                progressDialog.cancel();
            }


            //   forwardToLiveRoom(role) ;
        });
    }


    public void forwardToLiveRoom(int cRole, String recording) {
        String room = interview.getTitle();
        Intent i = new Intent(MainActivity.this, LiveRoomActivity.class);
        i.putExtra(ConstantApp.ACTION_KEY_CROLE, cRole);
        i.putExtra(ConstantApp.START_RECORDING, recording);
        i.putExtra(ConstantApp.ACTION_KEY_ROOM_NAME, room);
        Bundle b = new Bundle();
        b.putSerializable(MainActivity.InterViewKey, interview);
        i.putExtras(b);
        startActivity(i);
    }

    public void forwardToSettings() {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    public class UserAdapter extends ArrayAdapter<User> {

        Context context;
        int layoutResourceId;
        ArrayList<User> data = null;

        public UserAdapter(Context context, int layoutResourceId, ArrayList<User> data) {
            super(context, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            UserHolder holder = null;

            if (row == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                holder = new UserHolder();
                holder.txtTitle = (TextView) row.findViewById(R.id.txtTitle);

                row.setTag(holder);
            } else {
                holder = (UserHolder) row.getTag();
            }

            User user = data.get((position));
            holder.txtTitle.setText(user.getFirstname() + " " + user.getLastname());


            return row;
        }

        class UserHolder {
            TextView txtTitle;
        }
    }
}
