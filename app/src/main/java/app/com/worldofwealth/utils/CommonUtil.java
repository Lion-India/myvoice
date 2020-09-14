package app.com.worldofwealth.utils;

import android.content.Context;
import android.os.StrictMode;
import android.widget.Toast;

import app.com.worldofwealth.models.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtil {
    //public static String mbaseurl = "http://104.211.92.151:8010/api/";
    public static String mvideourl = "http://104.211.212.232/node/server/output/";
    public static String mvideosaveurl = "http://104.211.212.232:3000/";
    public static String mbaseurl = "https://worldofwealth.azurewebsites.net/api/";

    public static void showToast(Context ctx, String string) {
        Toast.makeText(ctx, string, Toast.LENGTH_SHORT).show();
    }

    public static String parseDate(String date) {
        System.out.println("Date "+date);
        date = date.replace("T", " ");
        date = date.substring(0,19);
        try { //2019-06-04T15:52:32.7570677
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date1 = dateFormat.parse(date);
            Date date2 = new Date(); // current date
            long diff = date2.getTime() - date1.getTime();
            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000);
            int diffInDays = (int) ((date2.getTime() - date1.getTime()) / (1000 * 60 * 60 * 24));

            if (diffInDays > 1) {
                return diffInDays + " days";
            } else if (diffHours > 24) {
                return diffInDays + " day";
            } else if ((diffHours == 24) && (diffMinutes >= 1)) {
                return diffMinutes + " mins";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return date;
    }

    public static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    public static Post ParseNewsStringtoPost(JSONObject job) {
        Post post = new Post();

        try {
            post.setId(job.getString("id"));
            post.setSourcename(job.getString("sourcename"));
            post.setTitle(job.getString("title"));
            post.setDesc(job.getString("description"));
            post.setSourceurl(job.getString("url"));
            post.setImagurl(job.getString("urlToImage"));
            post.setCreateddate(job.getString("publishedAt"));
            post.setUpvote(job.getString("upvote"));
            post.setDownvote(job.getString("downvote"));
            post.setComments(job.getString("comments"));
            post.setVideourl("null");
            post.setAudiourl("null");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return post;
    }

    public static boolean isNullCheck(String str) {
        return (str != null && !str.isEmpty() && !str.equals("null"));

    }

    public static boolean checkUserExistsornot(JSONArray data, String uid) {

        for (int i = 0; i < data.length(); i++) {
            try {
                String userid = data.getJSONObject(i).getString("userid");
                if (userid.equals(uid)) {
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return false;

    }

    public static String GetMP4fileName(String filepath) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            HttpURLConnection connection;
            connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(filepath);

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                int responseCode = connection.getResponseCode();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder responseStringBuilder = new StringBuilder();

                String line = "";

                while ((line = reader.readLine()) != null) {
                    if (line.contains(".mp4")) {
                        String split[] = line.split("href=\"");
                        String split1[] = split[1].split("\">");
                        String filename = split1[0].replace("\"", "");
                        return filepath + "/" + filename;
                    }
                    responseStringBuilder.append(line);
                    responseStringBuilder.append("\n");
                }

                // Parse responseStringBuilder.toString() (probably as HTML) here:

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
