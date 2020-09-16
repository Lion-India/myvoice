package app.com.worldofwealth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import app.com.worldofwealth.models.User;
import app.com.worldofwealth.utils.CommonUtil;
import app.com.worldofwealth.utils.Config;
import app.com.worldofwealth.utils.DBHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class OldLoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText phoneno, password;
    Button login, signup;
    String sphoneno, spassword;
    ProgressDialog progressDialog;
    String gcm_code;
    DBHelper dbHelper;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.old_activity_login);

        dbHelper = new DBHelper(this);
        user = dbHelper.getUserData();
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        gcm_code = pref.getString("regId", null);

        phoneno = (EditText) findViewById(R.id.emailEdit);
        password = (EditText) findViewById(R.id.passwordEdit);
        login = (Button) findViewById(R.id.loginButton);
        signup = (Button) findViewById(R.id.signupButton);
        signup.setOnClickListener(this);
        login.setOnClickListener(this);
        //Intent intent = getIntent();
        //gcm_code = intent.getStringExtra("Fcmkey");
        //dbHelper = new DBHelper(OldLoginActivity.this);


    }

    @Override
    public void onClick(View v) {
        if (v == signup) {
            Intent intent = new Intent(OldLoginActivity.this, RegisterActivity.class);
            intent.putExtra("Fcmkey", gcm_code);
            startActivity(intent);
        }
        if (v == login) {
            CallLogin();
        }
    }

    private void CallLogin() {

        sphoneno = phoneno.getText().toString();
        spassword = password.getText().toString();

        if (sphoneno.length() < 10) {
            phoneno.setError("Please Enter valid Phone Number");
            return;
        }
        if (spassword.length() < 2) {
            password.setError("Please Enter Valid Password");
        }

        try {
            JSONObject jsonParams = new JSONObject();
            StringEntity entity = null;
            jsonParams.put("phone",sphoneno);
            jsonParams.put("password",spassword);
            jsonParams.put("gcmcode",gcm_code);
            AsyncHttpClient client = new AsyncHttpClient();
            entity = new StringEntity(jsonParams.toString());
            client.post(OldLoginActivity.this,
                    CommonUtil.mbaseurl + "Users/Userlogin", entity,
                    "application/json", getResponseHandler("Login"));
            System.out.println("inpu :" + jsonParams.toString());

        } catch (Exception e) {
            System.err.println("Error in inserting" + e.toString());

        }
    }

    private ResponseHandlerInterface getResponseHandler(final String connectionfor) {
        return new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                progressDialog = ProgressDialog.show(OldLoginActivity.this,
                        "In progress", "Please wait");
            }

            @Override
            public void onFinish() {
                // Completed the request (either success or failure)
                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] resp) {
                if (connectionfor.contains("Login")) {
                    String response = new String(resp);
                    System.out.println("Login" + response.toString());
                    String Message;
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Message = jsonObject.getString("Message");
                        if (Message.contains("Successfull")) {
                            dbHelper.insertdata(jsonObject.toString(), DBHelper.USER_TBL);
                            Intent intent = new Intent(OldLoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Message = jsonObject.getString("Message");
                            if (Message.contains("Invalid Credential!")) {
                                CommonUtil.showToast(OldLoginActivity.this, "Invalid Credential!");
                            } else if (Message.contains("No Data Found!")) {
                                CommonUtil.showToast(OldLoginActivity.this, "No Data Found!");
                            }

                        }


                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                CommonUtil.showToast(OldLoginActivity.this, getString(R.string.something_went_wrong));
            }
        };
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
}
