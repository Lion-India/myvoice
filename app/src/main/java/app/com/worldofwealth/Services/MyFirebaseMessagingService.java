package app.com.worldofwealth.Services;

import android.content.Context;
import android.content.Intent;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import app.com.worldofwealth.MainActivity;
import app.com.worldofwealth.utils.Config;
import app.com.worldofwealth.utils.DBHelper;
import app.com.worldofwealth.utils.EncodeDecoder;
import app.com.worldofwealth.utils.NotificationUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;
    public static final int FLAG_UPDATE_CURRENT = 134217728;
    public static final String NEW_DATA_EVENT ="app.com.lion.NEW_DATA_AVAILABLE";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        System.out.println("Sathish Testing: "+remoteMessage.getFrom());

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            System.out.println("res :" + remoteMessage.getNotification().getTitle() +": "+remoteMessage.getNotification().getBody());

              Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody() + remoteMessage.getNotification().getTitle());
            if (remoteMessage.getNotification().getTitle().equals("UserRoleChanged")) {
                DBHelper dbHelper = new DBHelper(getApplicationContext());
                dbHelper.insertdata(remoteMessage.getNotification().getBody(), DBHelper.USER_TBL);
            }
            if(remoteMessage.getNotification().getTitle().equals("New Post")) {
                Intent eventIntent = new Intent(NEW_DATA_EVENT);
                sendBroadcast(eventIntent);
            }
        } else {
            Toast.makeText(this, "Notification ", Toast.LENGTH_SHORT).show();
            handleNotification(remoteMessage.getNotification().getBody(), (remoteMessage.getNotification().getTitle()));
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message, String title) {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time :" + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        String time = df.format(c.getTime());
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", EncodeDecoder.decodeURIComponent(message));


            //showNotificationMessage(getApplicationContext(), "Location", message, "12.am", pushNotification);
            showNotificationMessage(getApplicationContext(), EncodeDecoder.titledecodeURIComponent(title), EncodeDecoder.decodeURIComponent(message), time, pushNotification);


            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        } else {
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());
        Log.e(TAG, "Notification Body: "+json.toString());
        try {
            //
            JSONObject data = json.getJSONObject("data");
            System.out.println("datapush" + data);
            String title = json.getString("title");
            String message = json.getString("message");
            boolean isBackground = json.getBoolean("is_background");
            String imageUrl = json.getString("image");
            String timestamp = json.getString("timestamp");
            //JSONObject payload = json.getJSONObject("payload");

            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + EncodeDecoder.decodeURIComponent(message));
            Log.e(TAG, "isBackground: " + isBackground);
            //Log.e(TAG, "payload: " + payload.toString());
            Log.e(TAG, "imageUrl: " + imageUrl);
            Log.e(TAG, "timestamp: " + timestamp);
            System.out.println("imageUrl" + imageUrl);


            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", EncodeDecoder.decodeURIComponent(title));

                // check for image attachment

                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), EncodeDecoder.titledecodeURIComponent(title), EncodeDecoder.decodeURIComponent(message), timestamp, pushNotification);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(),  EncodeDecoder.titledecodeURIComponent(title), EncodeDecoder.decodeURIComponent(message), timestamp, pushNotification, imageUrl);
                }
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            } else {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                resultIntent.putExtra("message", EncodeDecoder.decodeURIComponent(message));

                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(),  EncodeDecoder.titledecodeURIComponent(title), EncodeDecoder.decodeURIComponent(message), timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(),  EncodeDecoder.titledecodeURIComponent(title), EncodeDecoder.decodeURIComponent(message), timestamp, resultIntent, imageUrl);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage( EncodeDecoder.titledecodeURIComponent(title), EncodeDecoder.decodeURIComponent(message), timeStamp, intent);

    }

    /**
     * Showing notification with text and image
     */

    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage( EncodeDecoder.titledecodeURIComponent(title), EncodeDecoder.decodeURIComponent(message), timeStamp, intent, imageUrl);
    }
}
