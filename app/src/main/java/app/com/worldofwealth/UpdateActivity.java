package app.com.worldofwealth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;

import java.util.ArrayList;
import java.util.List;

import app.com.worldofwealth.models.ResponseModal;
import app.com.worldofwealth.models.User;
import app.com.worldofwealth.utils.CommonUtil;
import app.com.worldofwealth.utils.DBHelper;
import cz.msebera.android.httpclient.Header;
import hb.xvideoplayer.MxVideoPlayer;

public class UpdateActivity extends AppCompatActivity {
    AsyncHttpClient client;
    String id, title, description, mediatype, mediaid;
    TextView posttitle, postdesc;
    MxVideoPlayer mpw_video_player;
    ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    //private CallsAdapter callsAdapter;
    ImageView imageView;
    String userid;
    DBHelper dbHelper;
    User user;

    ViewPager viewPager;

    public boolean onSupportNavigateUp() {
        startActivity(new Intent(this,MainActivity.class));
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updateactivity);
        dbHelper = new DBHelper(UpdateActivity.this);
        user = dbHelper.getUserData();
        //userid = user.getUid();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.update);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setClipToPadding(false);
        viewPager.setPadding(60, 0, 60, 0);
        viewPager.setPageMargin(1);
        getUpdate();
    }

    private void getUpdate() {
        progressDialog = new ProgressDialog(UpdateActivity.this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        String url = CommonUtil.mbaseurl + "Interview/GetHomeUpdate?UserId=" + user.getUid();
        client = new AsyncHttpClient();
        client.post(UpdateActivity.this,
                url, null,
                "application/json", getResponseHandler("Update"));
    }

    private ResponseHandlerInterface getResponseHandler(final String connectionfor) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressDialog.cancel();
                if (connectionfor.contains("Update") || connectionfor.contains("LikeDisLike")) {
                    String response = new String(responseBody);

                    List<ResponseModal> fullResponseObject = new Gson().fromJson(response, new TypeToken<List<ResponseModal>>() {
                    }.getType());

                    ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
                    for (ResponseModal modal : fullResponseObject) {
                        pagerAdapter.addFrag(app.com.worldofwealth.fragments.UpdateFragment.newInstance(new Gson().toJson(modal)), "Title-");
                    }
                    viewPager.setAdapter(pagerAdapter);

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.cancel();
                System.out.println("Error");
            }
        };
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


}
