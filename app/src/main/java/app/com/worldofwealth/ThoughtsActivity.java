package app.com.worldofwealth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import app.com.worldofwealth.adapters.ChatArrayAdapter;
import app.com.worldofwealth.adapters.ThoughtsAdapter;
import app.com.worldofwealth.models.ChatMessage;
import app.com.worldofwealth.models.Post;
import app.com.worldofwealth.models.Thoughts;
import app.com.worldofwealth.models.User;
import app.com.worldofwealth.utils.CommonUtil;
import app.com.worldofwealth.utils.DBHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ThoughtsActivity extends AppCompatActivity {
    Post post;
    Button buttonlist;
    ChatArrayAdapter chatArrayAdapter;
    ProgressDialog progressDialog;
    ArrayList<ChatMessage> messagelist;
    DBHelper dbHelper;
    User user;
    String key;
    String id,newsid;
    ListView listView;
    ArrayList<Thoughts> thoughtlist;
    ThoughtsAdapter thoughtsAdapter;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thoughts);
        dbHelper = new DBHelper(ThoughtsActivity.this);
        user = dbHelper.getUserData();

        thoughtlist = new ArrayList<Thoughts>();

        recyclerView = (RecyclerView) findViewById(R.id.listView1);
        layoutManager = new LinearLayoutManager(ThoughtsActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());

        thoughtsAdapter = new ThoughtsAdapter(ThoughtsActivity.this,thoughtlist);
        recyclerView.setAdapter(thoughtsAdapter);
        buttonlist = (Button) findViewById(R.id.buttonlist);
        buttonlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ThoughtsActivity.this, PostThoughts.class);
                startActivity(intent);
            }
        });

        getThoughtList();
    }

    private void getThoughtList() {
        String murl = CommonUtil.mbaseurl + "Thought/GetThought";
        progressDialog = new ProgressDialog(ThoughtsActivity.this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(ThoughtsActivity.this, murl, null, "application/json",
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String response = new String(responseBody);
                        progressDialog.cancel();
                        thoughtlist.clear();
                        try {
                            JSONArray jarr = new JSONArray(response);
                            for (int i = 0; i < jarr.length(); i++) {
                                JSONObject job = new JSONObject(jarr.get(i).toString());
                                Thoughts thought = ParseStringtoPost(job);
                                System.out.println("post :" + job.toString());
                                thoughtlist.add(thought);
                            }
                            thoughtsAdapter.notifyDataSetChanged();

                        } catch (Exception e) {
                            e.printStackTrace();
                            CommonUtil.showToast(ThoughtsActivity.this, getString(R.string.something_went_wrong));
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        progressDialog.cancel();
                        CommonUtil.showToast(ThoughtsActivity.this, getString(R.string.something_went_wrong));
                    }

                });
    }

    private Thoughts ParseStringtoPost(JSONObject job) {
        Thoughts thoughts = new Thoughts();
        try{
            thoughts.setId(job.getString("id"));
            thoughts.setTitle(job.getString("title"));
            thoughts.setDesc(job.getString("description"));
            thoughts.setCreatedby(job.getString("createdby"));
            thoughts.setCreatedbyname(job.getString("createdbyname"));
            thoughts.setComments(job.getString("comments"));
            thoughts.setDatatype(job.getString("datatype"));
            thoughts.setCreateddate(job.getString("createddate"));

        }catch (Exception e){
            e.printStackTrace();
        }
        return thoughts;
    }
}
