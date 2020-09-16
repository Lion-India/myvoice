package app.com.worldofwealth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;

import org.json.JSONObject;

import app.com.worldofwealth.models.User;
import app.com.worldofwealth.utils.CommonUtil;
import app.com.worldofwealth.utils.DBHelper;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class NotificationSettingsActivity extends AppCompatActivity {
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(this,MainActivity.class));
        return true;
    }
    Button update;
    DBHelper dbHelper;
    String uid;
    User user;
    CheckBox notificationCheckBox;
    String status = "";
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.notifications);
        dbHelper = new DBHelper(getApplicationContext());
        user = dbHelper.getUserData();
        uid = String.valueOf(user.getUid());

        notificationCheckBox = (CheckBox) findViewById(R.id.notificationCheckBox);
        if(notificationCheckBox.isChecked()){
            status = "Active";
        }else {
            status = "InActive";
        }
        update = (Button) findViewById(R.id.updatebutton);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNotification();
            }
        });
    }

    private void updateNotification() {
        /*if(notificationCheckBox.isChecked()){
            Toast.makeText(this, "Is Checked", Toast.LENGTH_SHORT).show();
        }*/
        try {
            JSONObject jsonParams = new JSONObject();
            StringEntity entity = null;
            AsyncHttpClient client = new AsyncHttpClient();
            entity = new StringEntity(jsonParams.toString());
            client.post(NotificationSettingsActivity.this,
                    CommonUtil.mbaseurl + "Users/UserNotificationSetting?userid=" + uid + "&notificationstatus=" + status , entity,
                    "application/json", getResponseHandler("Update"));
            System.out.println("inpu :" + jsonParams.toString());

        } catch (Exception e) {
            System.err.println("Error in inserting" + e.toString());

        }
    }

    private ResponseHandlerInterface getResponseHandler(final String connectionfor) {
        return new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                progressDialog = ProgressDialog.show(NotificationSettingsActivity.this,
                        "In progress", "Please wait");
            }

            @Override
            public void onFinish() {
                // Completed the request (either success or failure)
                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] resp) {
                if (connectionfor.contains("Update")) {
                    String response = new String(resp);
                    System.out.println("Update" + response);
                    String Message;
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        CommonUtil.showToast(NotificationSettingsActivity.this, "Updated Successfully!");
                        Intent intent = new Intent(NotificationSettingsActivity.this, MainActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                CommonUtil.showToast(NotificationSettingsActivity.this, getString(R.string.something_went_wrong));
            }
        };
    }
}