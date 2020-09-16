package app.com.worldofwealth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import app.com.worldofwealth.models.Interview;
import app.com.worldofwealth.models.Opinion;
import app.com.worldofwealth.models.Post;
import app.com.worldofwealth.models.Thoughts;
import app.com.worldofwealth.models.User;
import app.com.worldofwealth.utils.CommonUtil;
import app.com.worldofwealth.utils.DBHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import static app.com.worldofwealth.utils.CommonUtil.showToast;

public class PostThoughts extends AppCompatActivity {
    public static final String KEY = "postkey";
    Thoughts thought;
    Post post;
    EditText posttitle,postmessage;
    Button postcommentsubmit;
    DBHelper dbHelper;
    User user;
    String uid;
    ProgressDialog progressDialog;
    Interview interview;
    Opinion opinion;
    JSONObject jsonParams = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_thoughts);
        dbHelper = new DBHelper(PostThoughts.this);
        user = dbHelper.getUserData();
        uid = user.getUid();
        Intent intent = getIntent();
        opinion = (Opinion) intent.getSerializableExtra(KEY);

        posttitle = (EditText) findViewById(R.id.posttitleEdittext);
        postmessage = (EditText) findViewById(R.id.postcommentsEdittext);
        postcommentsubmit = (Button) findViewById(R.id.buttonpostcomment);

        postcommentsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   PostThoughtstoserver();

            }
        });
    }

    private void PostThoughtstoserver() {
        String mtitle = posttitle.getText().toString();
        String mtext = postmessage.getText().toString();
        if (mtitle.length() < 2) {
            posttitle.setError(getString(R.string.enter_title));
            return;
        }
        if (mtext.length() < 2) {
            postmessage.setError(getString(R.string.enter_comments));
            return;
        }
        try {
            StringEntity entity = null;
            AsyncHttpClient client = new AsyncHttpClient();
            jsonParams.put("id", uid);
            jsonParams.put("title", mtitle);
            jsonParams.put("description", mtext);
            entity = new StringEntity(jsonParams.toString());
            //System.out.println("entity :"+jsonParams.toString());
            client.post(PostThoughts.this,
                    CommonUtil.mbaseurl + "Thought/PostThought", entity,
                    "application/json", getResponseHandler("postthought"));
            //System.out.println("inpu :" + jsonParams.toString());

        } catch (Exception e) {
            System.err.println("Error in inserting" + e.toString());

        }
    }

    private ResponseHandlerInterface getResponseHandler(final String connectionfor) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] resp) {
                if(connectionfor.contains("postthought")){
                    String response = new String(resp);
                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("Message").equals("Successfull")) {
                            showToast(getApplicationContext(), "Thanks, Your comments are posted to admin");
                            Intent intent = new Intent(PostThoughts.this,MainActivity.class);
                            startActivity(intent);
                        } else {
                            showToast(getApplicationContext(), getString(R.string.something_went_wrong));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showToast(getApplicationContext(), getString(R.string.something_went_wrong));
            }
        };
    }
}
