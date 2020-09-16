package app.com.worldofwealth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import app.com.worldofwealth.utils.Config;
import app.com.worldofwealth.utils.DBHelper;
import com.google.firebase.messaging.FirebaseMessaging;

public class SplashActivity extends AppCompatActivity {
    DBHelper mydb;
    int splashtime = 2000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private static final String TAG = "Splash";
    String regId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mydb = new DBHelper(this);
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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startMainActivity();
            }
        }, splashtime);

    }

    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        regId = pref.getString("regId", null);
        System.out.println( "Firebase reg id: " + regId);
    }

    private void startMainActivity() {
     int usercount = mydb.numberOfRows();
        if (usercount == 0) {
            //Intent intent = new Intent(SplashActivity.this, OldLoginActivity.class);
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            intent.putExtra("Fcmkey", regId);
            startActivity(intent);
        } else {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            intent.putExtra("Fcmkey", regId);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {

    }
}
