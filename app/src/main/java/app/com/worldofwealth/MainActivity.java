package app.com.worldofwealth;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import app.com.worldofwealth.fragments.ScholarsFragment;
import app.com.worldofwealth.fragments.LawMakersFragment;
import app.com.worldofwealth.fragments.MyVoiceFragment;
import app.com.worldofwealth.fragments.TrendingFragment;
import app.com.worldofwealth.fragments.TopScoreFragment;
import app.com.worldofwealth.models.User;
//import app.com.lion.utils.CircleTransform;
import app.com.worldofwealth.utils.CommonUtil;
import app.com.worldofwealth.utils.Config;
import app.com.worldofwealth.utils.DBHelper;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1001;

    BottomNavigationView mBottomNavigationView;
    Toolbar toolbar;
    FloatingActionButton fab;
    User user;
    DBHelper dbHelper;
    String usertype;
    boolean doubleBackToExitPressedOnce = false;
    TextView textNotificationItemCount;
    int mCartItemCount = 15;
    int updateviewcountleft;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private static final String TAG = "MainActivity";
    String regId;
    String navigation = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.topScore);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            navigation = extras.getString("navigation");
        }

        /*final RippleBackground rippleBackground=(RippleBackground)findViewById(R.id.content);
        ImageView imageView=(ImageView)findViewById(R.id.centerImage);
        rippleBackground.startRippleAnimation();*/

        dbHelper = new DBHelper(MainActivity.this);
        user = dbHelper.getUserData();
        usertype = user.getUsertype();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        Menu menu = navigationView.getMenu();
        //MenuItem target = menu.findItem(R.id.nav_posts);
        /*if((usertype.contains("User"))||(usertype.contains("Admin"))){
            target.setVisible(false);
        }*/
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);

        TextView ttoscore = (TextView) mBottomNavigationView.findViewById(R.id.topscore).findViewById(R.id.largeLabel);
        ttoscore.setTextSize(20);
        TextView tstars = (TextView) mBottomNavigationView.findViewById(R.id.stars).findViewById(R.id.largeLabel);
        tstars.setTextSize(20);
        TextView tlegislators = (TextView) mBottomNavigationView.findViewById(R.id.legislators).findViewById(R.id.largeLabel);
        tlegislators.setTextSize(20);
        TextView tngo = (TextView) mBottomNavigationView.findViewById(R.id.ngo).findViewById(R.id.largeLabel);
        tngo.setTextSize(20);
        TextView tjudiciary = (TextView) mBottomNavigationView.findViewById(R.id.judiciary).findViewById(R.id.largeLabel);
        tjudiciary.setTextSize(20);

        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.tusername);
        TextView navUseremail = (TextView) headerView.findViewById(R.id.tuseremail);
        final ImageView navUserimg = (ImageView) headerView.findViewById(R.id.imageView);

        int usercount = dbHelper.numberOfRows();
        if (usercount > 0) {
            navUsername.setText(user.getFirstname() + " " + user.getLastname());
            navUseremail.setText(user.getEmail());
        }

//        Glide.with(this).load(user.getUserimageurl())
////                .crossFade()
//                .thumbnail(0.5f)
//                .placeholder(R.drawable.icon_profile_48)
//                .error(R.drawable.icon_profile_48)
////                .bitmapTransform(new CircleTransform(this))
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(navUserimg);
        Glide.with(this).load(user.getUserimageurl())
                .placeholder(R.drawable.icon_profile_48)
                .apply(RequestOptions.circleCropTransform())
                .into(navUserimg);

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.stars:
                        toolbar.setTitle(getText(R.string.lawMakers));
                        fragment = new LawMakersFragment();
                        loadFragment(fragment);
                        return true;
                    case R.id.legislators:
                        toolbar.setTitle(getText(R.string.myvoice));
                        fragment = new MyVoiceFragment();
                        loadFragment(fragment);
                        return true;
                    case R.id.topscore:
                        toolbar.setTitle(getText(R.string.topScore));
                        fragment = new TopScoreFragment();
                        loadFragment(fragment);
                        return true;
                    case R.id.ngo:
                        toolbar.setTitle(getText(R.string.enterprises));
                        fragment = new TrendingFragment();
                        loadFragment(fragment);
                        return true;
                    case R.id.judiciary:
                        toolbar.setTitle(getText(R.string.scholars));
                        fragment = new ScholarsFragment();
                        loadFragment(fragment);
                        return true;
                }

                return false;
            }
        });
        fab = findViewById(R.id.fabmain);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomNavigationView.setSelectedItemId(R.id.topscore);
                Fragment fragment;
                toolbar.setTitle(getText(R.string.topScore));
                fragment = new TopScoreFragment();
                loadFragment(fragment);
            }
        });
        //homeUpdateCount();
        //Default load fragment
        if (navigation != null && !navigation.isEmpty() && !navigation.equals("null")){
            System.out.println(navigation);
            mBottomNavigationView.setSelectedItemId(R.id.legislators);
            Fragment fragment;
            toolbar.setTitle(getText(R.string.trending));
            fragment = new TrendingFragment();
            loadFragment(fragment);

        } else {
            mBottomNavigationView.setSelectedItemId(R.id.topscore);
            Fragment fragment;
            toolbar.setTitle(getText(R.string.topScore));
            fragment = new TopScoreFragment();
            loadFragment(fragment);

        }

        checkAndRequestPermissions();


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    displayFirebaseRegId();
                }
            }
        };
        displayFirebaseRegId();
    }

    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        regId = pref.getString("regId", null);
        updateGcmCode();
    }

    private void updateGcmCode() {
        try {
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("id", user.getUid());
            jsonParams.put("gcmcode", regId);
            StringEntity entity = null;
            AsyncHttpClient client = new AsyncHttpClient();
            entity = new StringEntity(jsonParams.toString());
            String url = CommonUtil.mbaseurl + "Users/UpdateUserGcmCode";
            client.post(MainActivity.this,
                    url, entity,
                    "application/json", getResponseHandler("updategcm"));

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in inserting" + e.toString());

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        homeUpdateCount();
    }

    private void homeUpdateCount() {
        try {
            JSONObject jsonParams = new JSONObject();
            StringEntity entity = null;
            AsyncHttpClient client = new AsyncHttpClient();
            entity = new StringEntity(jsonParams.toString());
            String url = CommonUtil.mbaseurl + "Interview/GetHomeUpdateNewCount?userid=" + user.getUid();
            Log.d("URL", url);
            client.post(MainActivity.this,
                    url, entity,
                    "application/json", getResponseHandler("updatecount"));
            System.out.println("reporttest :" + jsonParams.toString());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in inserting" + e.toString());

        }
    }

    private ResponseHandlerInterface getResponseHandler(final String connectionfor) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (connectionfor.contains("updatecount")) {
                    String response = new String(responseBody);
                    String Message;
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Message = jsonObject.getString("Message");
                        if (Message.contains("Successfull")) {
                            //CommonUtil.showToast(MainActivity.this, "success");
                            updateviewcountleft = Integer.parseInt(jsonObject.getString("updateviewcountleft"));
                            setupBadge();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (connectionfor.contains("updategcm")) {
                    //  System.out.println("updategcm: " + new String(responseBody));
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                System.out.println("Error");
            }
        };
    }

    private void checkAndRequestPermissions() {
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int writeExternalPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        //int callPhonePermission = ContextCompat.checkSelfPermission(this, permission.CALL_PHONE);
        //int contactPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
//        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
//            listPermissionsNeeded.add(permission.READ_PHONE_STATE);
//        }
        if (writeExternalPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
//        if (callPhonePermission != PackageManager.PERMISSION_GRANTED) {
//            listPermissionsNeeded.add(permission.CALL_PHONE);
//        }
//
//        if (callPhonePermission != PackageManager.PERMISSION_GRANTED) {
//            listPermissionsNeeded.add(permission.CALL_PHONE);
//        }
//        if (contactPermission != PackageManager.PERMISSION_GRANTED) {
//            listPermissionsNeeded.add(Manifest.permission.GET_ACCOUNTS);
//        }


        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return;
        }


        return;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final MenuItem menuItem = menu.findItem(R.id.action_settings);
        View actionView = menuItem.getActionView();
        textNotificationItemCount = (TextView) actionView.findViewById(R.id.cart_badge);
        //for check box

        setupBadge();

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings: {

                Intent i = new Intent(this, UpdateActivity.class);
                this.startActivity(i);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupBadge() {

        if (textNotificationItemCount != null) {
            if (updateviewcountleft == 0) {
                if (textNotificationItemCount.getVisibility() != View.GONE) {
                    textNotificationItemCount.setVisibility(View.GONE);
                }
            } else {
                textNotificationItemCount.setText(String.valueOf(Math.min(updateviewcountleft, 99)));
                if (textNotificationItemCount.getVisibility() != View.VISIBLE) {
                    textNotificationItemCount.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // super.onBackPressed();
            if (doubleBackToExitPressedOnce) {
                //super.onBackPressed();
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        /*if (id == R.id.nav_posts) {
            startActivity(new Intent(MainActivity.this,CreatePostActivity.class));
        }*/
        if (id == R.id.nav_interview) {
            startActivity(new Intent(MainActivity.this, InterviewActivity.class));
        }
       /* else if (id == R.id.nav_news) {
            mBottomNavigationView.setSelectedItemId(R.id.news);
            Fragment fragment;
            toolbar.setTitle(getText(R.string.news));
            fragment = new NewsFragment();
            loadFragment(fragment);
        }*/
        else if (id == R.id.nav_thougts) {
            startActivity(new Intent(MainActivity.this, PostThoughts.class));
        } else if (id == R.id.nav_directory) {
            startActivity(new Intent(MainActivity.this, DirectoryActivity.class));
        }
        /*else if (id == R.id.nav_savedarticles) {
            startActivity(new Intent(MainActivity.this,SavedAritcalActivity.class));
        }*/
        else if (id == R.id.nav_terms) {
            startActivity(new Intent(MainActivity.this, TermsAndConditionsActivity.class));
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
        } else if (id == R.id.action_check) {
            Intent intent = new Intent(MainActivity.this, NotificationSettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            if (accessToken != null) {
                LoginManager.getInstance().logOut();
            }
            Intent intent = new Intent(MainActivity.this, SplashActivity.class);
            dbHelper.deletetable("user");
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
