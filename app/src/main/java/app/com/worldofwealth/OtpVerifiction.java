package app.com.worldofwealth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import app.com.worldofwealth.models.User;
import app.com.worldofwealth.utils.CommonUtil;
import app.com.worldofwealth.utils.DBHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class OtpVerifiction extends AppCompatActivity implements View.OnClickListener {
    String phoneno, password;
    Button verifiy;
    TextView resend;
    private Button verify;
    ProgressDialog progressDialog;
    String jsondata;
    DBHelper dbHelper;
    User user;
    String motp, sotp1, sotp2, sotp3, sotp4;
    EditText otp1, otp2, otp3, otp4;
    Integer otp;
    TextView otpSendText;
    JSONObject jsonObject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verifiction);
        Intent intent = getIntent();
        setTitle(R.string.otp_verification);
        dbHelper = new DBHelper(this);
        user = dbHelper.getUserData();
        phoneno = intent.getStringExtra("phoneo");
        jsondata = intent.getStringExtra("jsondata");
        try {
            jsonObject = new JSONObject(jsondata);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        verify = (Button) findViewById(R.id.verify);
        otp1 = (EditText) findViewById(R.id.otpFirstNumberText);
        otp2 = (EditText) findViewById(R.id.otpSecondNumberText);
        otp3 = (EditText) findViewById(R.id.otpThirdNumberText);
        otp4 = (EditText) findViewById(R.id.otpFourthNumberText);
        otpSendText = (TextView) findViewById(R.id.otpSendText);
        otpSendText.setText("Please type the verification code sent\n" +
                "to +91" + phoneno);

        resend = (TextView) findViewById(R.id.resendOtpText);
        resend.setOnClickListener(this);

        otp1.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (otp1.getText().length() == 1)
                    otp2.requestFocus();
                return false;
            }
        });

        otp2.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (otp2.getText().length() == 1)
                    otp3.requestFocus();
                return false;
            }
        });
        otp3.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (otp3.getText().length() == 1)
                    otp4.requestFocus();
                return false;
            }
        });


        verify.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if (v == verify) {

            sotp1 = otp1.getText().toString();
            sotp2 = otp2.getText().toString();
            sotp3 = otp3.getText().toString();
            sotp4 = otp4.getText().toString();
            otp = Integer.valueOf(sotp1 + sotp2 + sotp3 + sotp4);


            GetDataFromServer();
        }

        if (v == resend) {
            ResendOTP(jsonObject);
        }
    }

    private void ResendOTP(JSONObject jsonObject) {
        String url = CommonUtil.mbaseurl + "Users/PostUser";
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(OtpVerifiction.this);
        progressDialog.setMessage(OtpVerifiction.this.getString(R.string.please_wait));
        progressDialog.setTitle(OtpVerifiction.this.getString(R.string.app_name));
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        StringEntity entity = null;
        AsyncHttpClient client = new AsyncHttpClient();

        try {
            entity = new StringEntity(jsonObject.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        client.post(OtpVerifiction.this, url,
                entity, "application/json", new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String response = new String(responseBody);
                        //System.out.println("Sathish: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            dbHelper.insertdata(jsonObject.toString(), DBHelper.USER_TBL);
                            /*String Message = jsonObject.getString("Message");
                            if (Message.equals("Successfull") || Message.equals("Not Approved")) {
                                {
                                    CommonUtil.showToast(OtpVerifiction.this, OtpVerifiction.this.getString(R.string.otp_sent));
                                }
                            } else {
                                CommonUtil.showToast(OtpVerifiction.this, OtpVerifiction.this.getString(R.string.something_went_wrong));
                            }*/
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressDialog.cancel();


                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        progressDialog.cancel();
                        CommonUtil.showToast(OtpVerifiction.this, OtpVerifiction.this.getString(R.string.something_went_wrong));
                    }
                });

    }

    private void GetDataFromServer() {
        try {
            JSONObject jsonParams = new JSONObject();
            StringEntity entity = null;
            AsyncHttpClient client = new AsyncHttpClient();
            entity = new StringEntity(jsonParams.toString());
            client.post(OtpVerifiction.this,
                    CommonUtil.mbaseurl + "users/VerifyOTP?phone=" + phoneno + "&otp=" + otp + "&lat=''&lon=''", entity,
                    "application/json", getResponseHandler("verify"));
            //System.out.println("inpu :" + jsonParams.toString());

        } catch (Exception e) {
            System.err.println("Error in inserting" + e.toString());

        }
    }

    private ResponseHandlerInterface getResponseHandler(final String connectionfor) {
        return new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                progressDialog = ProgressDialog.show(OtpVerifiction.this,
                        "In progress", "Please wait");
            }

            @Override
            public void onFinish() {
                // Completed the request (either success or failure)
                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] resp) {
                if (connectionfor.contains("verify")) {
                    String response = new String(resp);
                    try {
                        JSONObject job = new JSONObject(response);
                        if (job.getString("Message").contains("Incorrect")) {
                            CommonUtil.showToast(getApplicationContext(), getApplication().getString(R.string.incorrect_otp));
                            return;
                        }
                        if (job.getString("Message").equals("Successfull")) {
                            String uid = job.getString("id");
                            String userType = job.getString("usertype");
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("id", uid);
                            jsonObject.put("usertype", userType);
                            System.out.println("inputreg :" + jsonObject.toString());
                            /*DBHelper dbHelper = new DBHelper(OtpVerifiction.this);
                            dbHelper.insertdata(job.toString(), DBHelper.USER_TBL);*/
                            CommonUtil.showToast(getApplicationContext(), getApplicationContext().getString(R.string.registered_success));
                            Intent intent = new Intent(OtpVerifiction.this, MainActivity.class);
                            startActivity(intent);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                CommonUtil
                        .showToast(getApplicationContext(),
                                getText(R.string.something_went_wrong).toString());
            }
        };
    }


}
