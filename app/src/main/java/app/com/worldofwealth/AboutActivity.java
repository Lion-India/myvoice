package app.com.worldofwealth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import app.com.worldofwealth.utils.CommonUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class AboutActivity extends AppCompatActivity {

    TextView about;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_lion);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.about);
        about = (TextView) findViewById(R.id.tabout);
        GetDatafromServer();

    }
    public boolean onSupportNavigateUp() {
       startActivity(new Intent(this,MainActivity.class));
        return true;
    }
    private void GetDatafromServer() {
        progressDialog = new ProgressDialog(AboutActivity.this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.show();
        //progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(CommonUtil.mbaseurl + "users/GetAbout?doctype=About", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] res) {
                String response = new String(res);
                try {
                    JSONObject job = new JSONObject(response);
                    String About = job.getString("about");
                    Spanned spanned = Html.fromHtml(About);
                    about.setText(spanned);
                    progressDialog.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.cancel();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.cancel();
                CommonUtil.showToast(getApplicationContext(), getString(R.string.something_went_wrong));
            }

        });
    }


}
