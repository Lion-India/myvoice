package app.com.worldofwealth;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import app.com.worldofwealth.models.User;
import app.com.worldofwealth.utils.CommonUtil;
import app.com.worldofwealth.utils.DBHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;



import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class DirectoryProfileDetails extends AppCompatActivity {
    String id,uname,type;
    RatingBar individualRating;
    int rating_count = 0;
    DBHelper dbHelper;
    User user;
    String localuid;
    TextView fcount, textrating, overallrating, totalpost;
    float rating_value;
    String rating;
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(this,MainActivity.class));
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory_profile_details);
        dbHelper = new DBHelper(this);
        user = dbHelper.getUserData();
        localuid = user.getUid();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.DirectoryProfileDetails);
        /*getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar_title);
        TextView title=(TextView) findViewById(R.id.Title);
        title.setText("");*/
        fcount = findViewById(R.id.fcount);
        textrating = findViewById(R.id.rating);
        totalpost = findViewById(R.id.post);
        ImageView back=(ImageView)findViewById(R.id.back);
        /*back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Backpressed();
            }
        });*/
         Intent intent=getIntent();
         id=intent.getStringExtra("id");
         uname=intent.getStringExtra("name");
         type=intent.getStringExtra("type");


        TextView username=(TextView)findViewById(R.id.username);
        username.setText(uname);

        getRatingDetails();

        individualRating =findViewById(R.id.ratingbar);
        individualRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                if(rating_count > 0){
                    String rating = String.valueOf(individualRating.getRating());
                    Toast.makeText(getApplicationContext(), rating, Toast.LENGTH_LONG).show();
                    PostDatatoServer(rating);
                }
                rating_count++;
            }
        });
    }

    private void getRatingDetails() {
        int g_rating = 0;
        try {
            JSONObject jsonParams = new JSONObject();
            StringEntity entity = null;
            AsyncHttpClient client = new AsyncHttpClient();
            entity = new StringEntity(jsonParams.toString());
            String url = CommonUtil.mbaseurl + "users/Rating?memberid=" +id+ "&userid=" +localuid+ "&type=Get" + "&rating="+g_rating;
            Log.d("URL",url);
            client.post(this,
                    url, entity,
                    "application/json", getResponseHandler("getrating"));
            System.out.println("inputtest :" + jsonParams.toString());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in inserting" + e.toString());

        }
    }

    private void PostDatatoServer(String rating) {
        try {
            JSONObject jsonParams = new JSONObject();
            StringEntity entity = null;
            AsyncHttpClient client = new AsyncHttpClient();
            entity = new StringEntity(jsonParams.toString());
            String url = CommonUtil.mbaseurl + "users/Rating?memberid=" +id+ "&userid=" +localuid+ "&type=Post" + "&rating="+rating;
            Log.d("URL",url);
            client.post(this,
                    url, entity,
                    "application/json", getResponseHandler("rating"));
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
                if (connectionfor.contains("getrating")){
                    String response1 = new String(resp);
                    try{
                        String following1, rating1, total_rating1, userpost;
                        JSONObject jsonObject = new JSONObject(response1);
                        userpost = jsonObject.getString("totalposts");
                        following1 = jsonObject.getString("following");
                        total_rating1 = jsonObject.getString("totalrating");
                        rating = jsonObject.getString("rating");
                        rating_value= Float.parseFloat(String.valueOf(rating));
                        individualRating.setRating(rating_value);
                        fcount.setText(following1);
                        textrating.setText(rating+ "/5");
                        totalpost.setText(userpost);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else if (connectionfor.contains("rating")){
                    String response = new String(resp);
                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        String following = jsonObject.getString("following");
                        String rating = jsonObject.getString("rating");
                        String total_rating = jsonObject.getString("totalrating");
                        String userpostcount = jsonObject.getString("totalposts");
                        /*JSONArray j_array = jsonObject.getJSONArray("rating");
                        for (int i = 0; i < j_array.length(); i++) {
                            JSONObject jsonObject1 = new JSONObject(j_array.getJSONObject(i).toString());
                            String respRating = jsonObject1.getString("userrating");
                        }*/
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                CommonUtil.showToast(DirectoryProfileDetails.this, "Something went wrong");
            }
        };
    }

    private void Backpressed() {
        if(type=="legislatory"){
         Intent intent=new Intent(DirectoryProfileDetails.this,DirectoryActivity.class);
         startActivity(intent);
        }
        else if(type=="judiciaries"){
            Intent intent=new Intent(DirectoryProfileDetails.this,DirectoryActivity.class);
            startActivity(intent);

        }else{
            Intent intent=new Intent(DirectoryProfileDetails.this,DirectoryActivity.class);
            startActivity(intent);
        }
    }
}
