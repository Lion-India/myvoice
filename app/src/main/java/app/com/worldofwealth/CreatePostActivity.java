package app.com.worldofwealth;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import app.com.worldofwealth.Fileupload.Upload;
import app.com.worldofwealth.Fileupload.UploadAudio;
import app.com.worldofwealth.Fileupload.UploadVideo;
import app.com.worldofwealth.models.Post;
import app.com.worldofwealth.models.User;
import app.com.worldofwealth.utils.CommonUtil;
import app.com.worldofwealth.utils.DBHelper;
import app.com.worldofwealth.utils.ImageFilePath;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public  class CreatePostActivity extends AppCompatActivity {
    EditText editTexttitle, desc;
    ImageView video, audio, image, upload;
    Button attach, preview, cancel;
    String posttitle, postdesc;
    private int GALLERY = 1;
    private static final int CAMERA_REQUEST = 1888;
    private static final int SELECT_VIDEO = 3;
    private static final int SELECT_AUDIO = 4;
    DBHelper dbHelper;
    User user;
    Post post;
    String pid, ptitle, pdesc, userid;

    private String selectedPath;
    String imagepath;
    ProgressDialog progressDialog;
    String imageurl, videourl, audiourl;
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(this,MainActivity.class));
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.posts);
        Intent intent = getIntent();
        dbHelper = new DBHelper(getApplicationContext());
        user = dbHelper.getUserData();
        userid = user.getUid();

        editTexttitle = (EditText) findViewById(R.id.posttitle);
        desc = (EditText) findViewById(R.id.postdesc);
        video = (ImageView) findViewById(R.id.video);
        audio = (ImageView) findViewById(R.id.audio);
        image = (ImageView) findViewById(R.id.image);
        attach = (Button) findViewById(R.id.attach);
        preview = (Button) findViewById(R.id.preview);

        attach.setVisibility(View.VISIBLE);
        preview.setVisibility(View.GONE);
        requestpermission();

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog();
            }
        });
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videodialog();
            }
        });
        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audiodialog();
            }
        });

        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                posttitle = editTexttitle.getText().toString();
                postdesc = desc.getText().toString();
                if (posttitle.length() < 1 && postdesc.length() < 1){
                    editTexttitle.setError("Please Enter more then 1 word");
                    desc.setError("Please Enter more then 1 word");
                    return;
                }
                if (wordcount(posttitle) > 5) {
                    editTexttitle.setError("Please Enter less then 5 words");
                    return;
                }
                if (wordcount(postdesc) > 100) {
                    desc.setError("Please Enter less then 100 words");
                    return;
                }
                try {
                    JSONObject jsonParams = new JSONObject();
                    StringEntity entity = null;
                    AsyncHttpClient client = new AsyncHttpClient();
                    jsonParams.put("title", posttitle);
                    jsonParams.put("description", postdesc);
                    jsonParams.put("videourl", videourl);
                    jsonParams.put("videothumbnailurl", videourl);
                    jsonParams.put("imageurl", imageurl);
                    jsonParams.put("audiourl", audiourl);
                    jsonParams.put("createdby", userid);
                    jsonParams.put("posttype","Trending");
                    jsonParams.put("status", true);
                    entity = new StringEntity(jsonParams.toString());
                    client.post(CreatePostActivity.this,
                            CommonUtil.mbaseurl + "Post/PostPost", entity,
                            "application/json", getResponseHandler("post"));
                    System.out.println("inpu :" + jsonParams.toString());

                } catch (Exception e) {
                    System.err.println("Error in inserting" + e.toString());

                }



                /*Toast.makeText(CreatePost.this, imageurl+ " "+videourl, Toast.LENGTH_SHORT).show();
                System.out.println("url :"+imageurl +"video :"+videourl+"audio :"+audiourl);*/

            }
        });


        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                posttitle = editTexttitle.getText().toString();
                postdesc = desc.getText().toString();
                if (posttitle.length() < 1 && postdesc.length() < 1){
                    editTexttitle.setError("Please Enter more then 1 word");
                    return;
                }
                if (wordcount(posttitle) > 5) {
                    editTexttitle.setError("Please Enter less then 5 words");
                    return;
                }
                if (wordcount(postdesc) > 100) {
                    desc.setError("Please Enter less then 100 words");
                    return;
                }
                Intent intent = new Intent(CreatePostActivity.this, PostPreview.class);
                intent.putExtra("posttitle", posttitle);
                intent.putExtra("postdesc", postdesc);
                intent.putExtra("localimageurl", imagepath);
                intent.putExtra("imageurl", imageurl);
                intent.putExtra("videourl", videourl);
                intent.putExtra("audiourl", audiourl);
                intent.putExtra("uid",userid);
                intent.putExtra("posttype","Trending");
                intent.putExtra("status", true);
                startActivity(intent);
            }
        });

    }

    private ResponseHandlerInterface getResponseHandler(final String connectionfor) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                progressDialog = ProgressDialog.show(CreatePostActivity.this,
                        "In progress", "Please wait");
            }

            @Override
            public void onFinish() {
                // Completed the request (either success or failure)
                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] resp) {
                if (connectionfor.contains("post")) {
                    String response = new String(resp);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String message = jsonObject.getString("Message");
                        if (message.contains("Successfull")) {
                           Toast.makeText(CreatePostActivity.this, "Post Created successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CreatePostActivity.this, MainActivity.class);
                            intent.putExtra("navigation","trending");
                            startActivity(intent);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        };
    }

    private void onBackPressed1() {
        super.onBackPressed();
        return;
    }

    private void requestpermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 121);

    }

    private void audiodialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(CreatePostActivity.this);
        dialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Upload Audio"};
        dialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                audiourl = null;

                                chooseAudio();
                                break;

                        }
                    }
                });
        dialog.show();
    }

    private void videodialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(CreatePostActivity.this);
        dialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Upload Video"};
        dialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                videourl = null;
                                imageurl = null;
                                chooseVideo();
                                break;

                        }
                    }
                });
        dialog.show();
    }

    private void dialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(CreatePostActivity.this);
        dialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Upload Image",
        };
        dialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                videourl = null;
                                imageurl = null;
                                showPictureDialog();
                                break;
                        }
                    }
                });
        dialog.show();
    }

    private void showPictureDialog() {

        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(CreatePostActivity.this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select from Gallery"
                /*"Open Camera"*/};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                //takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), GALLERY);
    }

    private void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a Video "), SELECT_VIDEO);
    }

    private void chooseAudio() {
        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a Audio "), SELECT_AUDIO);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GALLERY && resultCode == this.RESULT_OK) {
            onSelectFromGalleryResult(data);

        }

        if (requestCode == SELECT_VIDEO && resultCode == this.RESULT_OK) {
            Uri selectedImageUri = data.getData();
            selectedPath = getPath(selectedImageUri);
            //for upload
            videoupload(selectedPath, "postvideo");

        }

        if (requestCode == SELECT_AUDIO && resultCode == this.RESULT_OK) {
            Uri selectedImageUri = data.getData();
            selectedPath = audiogetPath(selectedImageUri);

            //for upload
            audioupload(selectedPath, "opinionaudio");

        }

    }

    private void audioupload(final String selectedPath, final String opinionvideo) {
        class uploadAudios extends AsyncTask<Void, Void, String> {

            ProgressDialog uploading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                uploading = ProgressDialog.show(CreatePostActivity.this, "Uploading File", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                System.out.println("res :" + s);
                uploading.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String Message = jsonObject.getString("Message");
                    if (Message.contains("Successfull")) {
                        String aid = jsonObject.getString("imageurl");
                        preview.setVisibility(View.VISIBLE);
                        attach.setVisibility(View.VISIBLE);
                        CommonUtil.showToast(CreatePostActivity.this, "Audio Uploaded Successfully");
                        if (aid.contains("Invalid")) {
                            CommonUtil.showToast(CreatePostActivity.this, getString(R.string.something_went_wrong));
                        } else {
                            audiourl = aid;
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected String doInBackground(Void... params) {
                UploadAudio u = new UploadAudio(opinionvideo);
                String msg = u.uploadAudios(selectedPath);
                return msg;
            }
        }
        uploadAudios uv = new uploadAudios();
        uv.execute();
    }


    private void videoupload(final String selectedPath, final String opinionvideo) {
        class UploadVideos extends AsyncTask<Void, Void, String> {

            ProgressDialog uploading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                uploading = ProgressDialog.show(CreatePostActivity.this, "Uploading File", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                System.out.println("res :" + s);
                uploading.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String Message = jsonObject.getString("Message");
                    if (Message.contains("Successfull")) {
                        String vid = jsonObject.getString("videourl");

                        preview.setVisibility(View.VISIBLE);
                        attach.setVisibility(View.VISIBLE);
                        CommonUtil.showToast(CreatePostActivity.this, "Video Uploaded Successfully");
                        if (vid.contains("Invalid")) {
                            CommonUtil.showToast(CreatePostActivity.this, getString(R.string.something_went_wrong));
                        } else {
                            videourl = vid;
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected String doInBackground(Void... params) {
                UploadVideo u = new UploadVideo(opinionvideo);
                String msg = u.uploadVideos(selectedPath);
                return msg;
            }
        }
        UploadVideos uv = new UploadVideos();
        uv.execute();
    }

    private String getPath(Uri uri) {
        Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = this.getContentResolver().query(
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        cursor.close();

        return path;
    }


    private String audiogetPath(Uri uri) {
        Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = this.getContentResolver().query(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
        cursor.close();

        return path;
    }

    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImage = data.getData();
        Bitmap bm = null;
        if (data != null) {

            Uri uri1 = data.getData();
            imagepath = ImageFilePath.getPath(this.getApplicationContext(), uri1);
            Log.i("Image File Path", "" + imagepath);
            String[] projection = {MediaStore.Images.Media.DATA};
            System.out.println("PicturePath:" + imagepath);
            imageUpload(imagepath, "postimage");

        }


    }

    private void imageUpload(final String imagepath, final String opinionimage) {
        class UploadVideos extends AsyncTask<Void, Void, String> {

            ProgressDialog uploading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                uploading = ProgressDialog.show(CreatePostActivity.this, "Uploading File", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                System.out.println("res :" + s);
                uploading.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String Message = jsonObject.getString("Message");
                    if (Message.contains("Successfull")) {
                        String img = jsonObject.getString("imageurl");
                        preview.setVisibility(View.VISIBLE);
                        attach.setVisibility(View.VISIBLE);
                        CommonUtil.showToast(CreatePostActivity.this, "Image Uploaded Successfully");
                        if (img.contains("Invalid")) {
                            CommonUtil.showToast(CreatePostActivity.this, getString(R.string.something_went_wrong));
                        } else {
                            imageurl = img;
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected String doInBackground(Void... params) {
                Upload u = new Upload(opinionimage);
                String msg = u.uploadVideos(imagepath);
                return msg;
            }
        }
        UploadVideos uv = new UploadVideos();
        uv.execute();
    }
    public int wordcount(String string)
    {
        int count=0;

        char ch[]= new char[string.length()];
        for(int i=0;i<string.length();i++)
        {
            ch[i]= string.charAt(i);
            if( ((i>0)&&(ch[i]!=' ')&&(ch[i-1]==' ')) || ((ch[0]!=' ')&&(i==0)) )
                count++;
        }
        return count;
    }

}