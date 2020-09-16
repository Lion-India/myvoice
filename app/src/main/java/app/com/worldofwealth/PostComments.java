package app.com.worldofwealth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import app.com.worldofwealth.models.Interview;
import app.com.worldofwealth.models.Opinion;
import app.com.worldofwealth.models.Post;
import app.com.worldofwealth.models.User;
import app.com.worldofwealth.utils.CommonUtil;
import app.com.worldofwealth.utils.DBHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import static app.com.worldofwealth.utils.CommonUtil.showToast;

public class PostComments extends AppCompatActivity {

    public static final String KEY = "postkey";
    Post post;
    EditText postmessage;
    Button postcommentsubmit;
    DBHelper dbHelper;
    User user;
    ProgressDialog progressDialog;
    Interview interview;
    Opinion opinion;
    TextView posttitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comments);
        dbHelper = new DBHelper(PostComments.this);
        user = dbHelper.getUserData();
        Intent intent = getIntent();
        opinion = (Opinion) intent.getSerializableExtra(KEY);
        //System.out.println("testopinion :"+opinion.toString());


        posttitle = (TextView) findViewById(R.id.posttitleEdittext);
        postmessage = (EditText) findViewById(R.id.postcommentsEdittext);
        postcommentsubmit = (Button) findViewById(R.id.buttonpostcomment);

        posttitle.setText(opinion.getTitle());
        postcommentsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mtext = postmessage.getText().toString();
                if (mtext.length() < 2) {
                    postmessage.setError(getString(R.string.enter_comments));
                    return;
                }else {
                    PostCommentstoServer(mtext);
                }
            }
        });
    }

    private void PostCommentstoServer(final String mtext) {
            String murl = CommonUtil.mbaseurl + "Opinion/PostComments?doctype=" + opinion.getDatatype();
            System.out.println("murl :"+murl);
            progressDialog = new ProgressDialog(PostComments.this);
            progressDialog.setMessage(getString(R.string.please_wait));
            progressDialog.setTitle(getString(R.string.app_name));
            progressDialog.show();
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            try {
                final JSONObject job = new JSONObject();

                job.put("userid", user.getUid());
                job.put("doctypeid", opinion.getId());
                job.put("comment", mtext);

                StringEntity entity = new StringEntity(job.toString());
                AsyncHttpClient client = new AsyncHttpClient();
                client.post(getApplicationContext(), murl, entity, "application/json",
                        new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                progressDialog.cancel();
                                String response = new String(responseBody);
                                // System.out.println("Sathish: " + response);
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.getString("Message").equals("Successfull")) {
                                        showToast(getApplicationContext(), "Thanks, Your comments posted to admin");
                                        finish();
                                        /*Intent intent = new Intent(PostComments.this, CommentsActivity.class);
                                        startActivity(intent);*/
                                    } else {
                                        showToast(getApplicationContext(), getString(R.string.something_went_wrong));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                progressDialog.cancel();
                                showToast(getApplicationContext(), getString(R.string.something_went_wrong));
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
                showToast(getApplicationContext(), getString(R.string.something_went_wrong));
            }
        }


}
