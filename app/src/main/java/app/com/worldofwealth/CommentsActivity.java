package app.com.worldofwealth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import app.com.worldofwealth.adapters.ChatArrayAdapter;
import app.com.worldofwealth.models.ChatMessage;
import app.com.worldofwealth.models.Interview;
import app.com.worldofwealth.models.Opinion;
import app.com.worldofwealth.models.Post;
import app.com.worldofwealth.models.User;
import app.com.worldofwealth.utils.DBHelper;

import java.util.ArrayList;

public class CommentsActivity extends AppCompatActivity {

    public static final String KEY = "data";
    Interview interview;
    Opinion opinion;
    Post  post;
    Button buttonlist;
    ChatArrayAdapter chatArrayAdapter;
    EditText chat_text;
    ImageButton btn_send;
    ProgressDialog progressDialog;
    ArrayList<ChatMessage> messagelist;
    DBHelper dbHelper;
    User user;
    String key;
    String id,newsid;
    ListView listView;
    TextView alertinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        dbHelper = new DBHelper(CommentsActivity.this);
        user = dbHelper.getUserData();
        alertinfo=(TextView)findViewById(R.id.alertinfo);


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        key = bundle.getString("Type");
        newsid = bundle.getString("id");
        if (key.contains("Post")) {
            post = (Post) bundle.getSerializable(CommentsActivity.KEY);
            opinion = new Opinion();
            opinion.setComments(post.getComments());
            opinion.setDatatype(post.getDatatype());
            opinion.setTitle(post.getTitle());
            opinion.setDescription(post.getDesc());
            id = post.getId();
            opinion.setId(post.getId());
        }
        else  if(key.contains("News")) {
            post = (Post) bundle.getSerializable(CommentsActivity.KEY);
            opinion = new Opinion();
            opinion.setComments(post.getComments());
            opinion.setDatatype("News");
            opinion.setTitle(post.getTitle());
            opinion.setDescription(post.getDesc());
            id = post.getId();
            opinion.setId(post.getId());
        }
        else {
            interview = (Interview) bundle.getSerializable(CommentsActivity.KEY);
            opinion = new Opinion();
            opinion.setComments(interview.getComments());
            opinion.setDatatype("InterView");
            opinion.setTitle(interview.getTitle());
            opinion.setDescription(interview.getDesc());
            id = interview.getId();
            opinion.setId(interview.getId());
        }

        listView = (ListView) findViewById(R.id.listView1);

        buttonlist = (Button) findViewById(R.id.buttonlist);
        buttonlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(key.contains("Post")){
                    Intent intent = new Intent(CommentsActivity.this, PostComments.class);
                    intent.putExtra(PostComments.KEY, opinion);
                    startActivity(intent);
                }else if(key.contains("News")){
                    Intent intent = new Intent(CommentsActivity.this, PostComments.class);
                    intent.putExtra(PostComments.KEY, opinion);
                    startActivity(intent);
                }else if(key.contains("InterView")){
                    Intent intent = new Intent(CommentsActivity.this, PostComments.class);
                    intent.putExtra(PostComments.KEY, opinion);
                    startActivity(intent);
                }

            }
        });

        /*messagelist = new ArrayList<ChatMessage>();
        try {
            if (opinion.getComments() != null && !opinion.getComments().equals("null")) {
                JSONArray jsonArray = new JSONArray(opinion.getComments());
                System.out.println("Messagetest :"+jsonArray.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    String message = jsonArray.getJSONObject(i).getString("comment");
                    String createddate = jsonArray.getJSONObject(i).getString("createddate");
                    if (!message.equals(null) || !message.equals("null")) {
                        String cuserid = jsonArray.getJSONObject(i).getString("userid");
                        if (cuserid.equals(user.getUid())) {
                            messagelist.add(new ChatMessage(true, message,createddate));
                        } else {
                            messagelist.add(new ChatMessage(false, message,createddate));
                        }
                    }

                }

            }
            chatArrayAdapter = new ChatArrayAdapter(CommentsActivity.this, messagelist);
            listView.setAdapter(chatArrayAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }
}
