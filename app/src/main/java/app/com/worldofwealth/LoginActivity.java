package app.com.worldofwealth;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import app.com.worldofwealth.utils.CommonUtil;
import app.com.worldofwealth.utils.DBHelper;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import com.truecaller.android.sdk.ITrueCallback;
import com.truecaller.android.sdk.TrueButton;
import com.truecaller.android.sdk.TrueError;
import com.truecaller.android.sdk.TrueProfile;
import com.truecaller.android.sdk.TruecallerSDK;
import com.truecaller.android.sdk.TruecallerSdkScope;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private static String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 007;
    EditText phoneno, password;
    Button login, signup, truecaller;
    String sphoneno, spassword;
    ProgressDialog progressDialog;
    String gcm_code;
    DBHelper dbHelper;
    TextView privacypolicy;
    public static String appPolicy = "https://worldofwealth.azurewebsites.net/PrivacyPolicy";



    RadioButton radioMale, radioFemale;
    EditText editdate;
    String sdob;
    String datedefault = "01-01-";
    String mgender = "";
    //private TextView txtName,txtEmail,textView;
    AccessTokenTracker accessTokenTracker;
    AccessTokenTracker tokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if (currentAccessToken == null) {
                //  txtName.setText("");
                //  txtEmail.setText("");
                Toast.makeText(LoginActivity.this, "User Logged out", Toast.LENGTH_LONG).show();
            } else
                loadUserProfile(currentAccessToken);
        }
    };
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    //Gmail
    private SignInButton btnSignIn;
    private GoogleApiClient mGoogleApiClient;
    //Truecaller
    private TrueButton trueLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook);



        phoneno = findViewById(R.id.emailEdit);
        password = findViewById(R.id.passwordEdit);
        login = findViewById(R.id.loginButton);
        signup = findViewById(R.id.signupButton);
        signup.setOnClickListener(this);
        login.setOnClickListener(this);
        Intent intent = getIntent();
        gcm_code = intent.getStringExtra("Fcmkey");
        dbHelper = new DBHelper(LoginActivity.this);
        privacypolicy = findViewById(R.id.privacypolicy);
        privacypolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, PrivacyPolicyActivity.class);
                intent.putExtra("weburl", appPolicy);
                startActivity(intent);
            }
        });



        loginButton = findViewById(R.id.fb_login_bn);
        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        checkLoginStatus();


        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                //  textView.setText("Logged in Successfully!");

            }

            @Override
            public void onCancel() {
                //     textView.setText("Login Cancelled!");

            }

            @Override
            public void onError(FacebookException error) {
                //  textView.setText("Error in Login!");

            }
        });


        // Gmail
        btnSignIn = findViewById(R.id.sign_in_button);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                //.addApi(Plus.API)
                .build();

        // Customizing G+ button
        btnSignIn.setSize(SignInButton.SIZE_STANDARD);
        btnSignIn.setScopes(gso.getScopeArray());
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        TruecallerSdkScope sdkScope = new TruecallerSdkScope.Builder(this, new ITrueCallback() {
            @Override
            public void onSuccessProfileShared(@NonNull TrueProfile trueProfile) {
                Log.i(TAG, trueProfile.firstName + " " + trueProfile.lastName);
                //getting values
                String gender = "";
                if (trueProfile.gender.equals("M")){
                    gender = "Male";
                    //showAlert();
                } else if (trueProfile.gender.equals("F")){
                    gender = "Female";
                }
                else if (trueProfile.gender.equals("N")){
                    showAlert(trueProfile.email, trueProfile.firstName + " " + trueProfile.lastName, "", "Truecaller", "", trueProfile.gender, trueProfile.avatarUrl);
                    return;
                }
                LoginwithSocial(trueProfile.email, trueProfile.firstName + " " + trueProfile.lastName, "", "Truecaller", "", gender, trueProfile.avatarUrl);
                System.out.println("trueProfile"+trueProfile.email);
                System.out.println("trueProfile"+trueProfile.signature);
                System.out.println("trueProfile"+trueProfile.countryCode);
                System.out.println("trueProfile"+trueProfile.avatarUrl);
                System.out.println("trueProfile"+trueProfile.payload);
                System.out.println("trueProfile"+trueProfile.isTrueName);
                System.out.println("trueProfile"+trueProfile.gender);

                //launchHome(trueProfile);
            }

            @Override
            public void onFailureProfileShared(@NonNull TrueError trueError) {
                Log.i(TAG, trueError.toString());
            }

            @Override
            public void onVerificationRequired() {
                Log.i(TAG, "onVerificationRequired");
            }
        })
                .consentMode(TruecallerSdkScope.CONSENT_MODE_POPUP )
                .consentTitleOption( TruecallerSdkScope.SDK_CONSENT_TITLE_VERIFY )
                .footerType( TruecallerSdkScope.FOOTER_TYPE_SKIP )
                .sdkOptions( TruecallerSdkScope.SDK_OPTION_WITH_OTP )
                .build();

        TruecallerSDK.init(sdkScope);

        truecaller = findViewById(R.id.login_with_truecaller);
        truecaller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TruecallerSDK.getInstance().isUsable()) {
                    TruecallerSDK.getInstance().getUserProfile(LoginActivity.this);
                } else {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                    dialogBuilder.setMessage("Truecaller App not installed.");

                    dialogBuilder.setPositiveButton("OK", (dialog, which) -> {
                                Log.d(TAG, "onClick: Closing dialog");

                                dialog.dismiss();
                            }
                    );

                    dialogBuilder.setIcon(R.drawable.com_truecaller_icon);
                    dialogBuilder.setTitle(" ");

                    AlertDialog alertDialog = dialogBuilder.create();
                    alertDialog.show();
                }
            }
        });

    }

    private void showAlert(String email, String name, String id, String from, String dob, String gender, String userprofile) {
        final Dialog dialog = new Dialog(LoginActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.alert_dialog);
        TextView mDialogOK = dialog.findViewById(R.id.alertok);
        TextView mDialogCancel = dialog.findViewById(R.id.cancelTextViewLogout);
        radioMale = (RadioButton) dialog.findViewById(R.id.maleRadio);
        radioMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mgender = "Male";
            }
        });
        radioFemale = (RadioButton) dialog.findViewById(R.id.femaleRadio);
        radioFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mgender = "Female";
            }
        });
        editdate = (EditText) dialog.findViewById(R.id.edittextdate);
        mDialogOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editdate.getText().length()<4){
                    Toast.makeText(LoginActivity.this,"Enter Birth of year",Toast.LENGTH_SHORT).show();
                    return;
                }else if (mgender.length()<2){
                    Toast.makeText(LoginActivity.this,"Select Gender",Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    LoginwithSocial(email,name,id,from,datedefault+editdate.getText().toString(),mgender,userprofile);
                    dialog.dismiss();
                }
            }
        });
        mDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginwithSocial(email,name,id,from,datedefault+editdate.getText().toString(),mgender,userprofile);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

//    private void launchHome(TrueProfile trueProfile) {
//        startActivity(new Intent(getApplicationContext(), HomeActivity.class)
//                .putExtra("profile", trueProfile));
//        finish();
//    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);

//            Person person  = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
//            String Gender = String.valueOf(person.getGender());
//            String Birthday = person.getBirthday();
//            System.out.println("Gender"+ Gender + Birthday);
        }else {
            TruecallerSDK.getInstance().onActivityResultObtained(LoginActivity.this, resultCode, data);
            System.out.println("trueresult :"+resultCode);
        }
        //super.onActivityResult(requestCode, resultCode, data);
        //TruecallerSDK.getInstance().onActivityResultObtained(LoginActivity.this, resultCode, data);
        //TruecallerSDK.getInstance().isUsable();
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("TAG", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            String personName = acct.getDisplayName();
            String id = acct.getId();
            //String personPhotoUrl = acct.getPhotoUrl().toString();
            String personPhotoUrl = null;
            if(acct.getPhotoUrl() != null){
                personPhotoUrl = acct.getPhotoUrl().toString();
            }
            String email = acct.getEmail();
            //     Log.e("TAG", "Name: " + personName + ", email: " + email
            //    + ", Image: " + personPhotoUrl);
            showAlert(email, personName, id, "Gmail", "", "", personPhotoUrl);
            //LoginwithSocial(email, personName, id, "Gmail", "", "", personPhotoUrl);

        }
    }


    private void loadUserProfile(AccessToken newAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    //System.out.println(object);
                    String first_name = object.getString("first_name");
                    String last_name = object.getString("last_name");
                    String email = object.getString("email");
                    String id = object.getString("id");
                    // String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";
                    showAlert(email, first_name + " " + last_name, id, "FaceBook", "", "", "");
                    //LoginwithSocial(email, first_name + " " + last_name, id, "FaceBook", "", "", "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();

    }

    private void LoginwithSocial(String email, String name, String id, String from, String dob, String gender, String userprofile) {
        try {
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("email",email);
            jsonParams.put("firstname",name);
            jsonParams.put("profileid",id);
            jsonParams.put("socialprofile",from);
            jsonParams.put("gcmcode",gcm_code);
            jsonParams.put("dob",dob);
            jsonParams.put("gender",gender);
            jsonParams.put("userimageurl",userprofile);
            StringEntity entity = null;
            AsyncHttpClient client = new AsyncHttpClient();
            entity = new StringEntity(jsonParams.toString());
            client.post(LoginActivity.this,
                    CommonUtil.mbaseurl + "Users/UserSocialLogin", entity,
                    "application/json", getResponseHandler("Login"));
           System.out.println("inpu :" + jsonParams.toString());

        } catch (Exception e) {
            System.err.println("Error in inserting" + e.toString());

        }

    }

    private void checkLoginStatus() {
        if (AccessToken.getCurrentAccessToken() != null) {
            loadUserProfile(AccessToken.getCurrentAccessToken());
        }
    }

    @Override
    public void onClick(View v) {
        if (v == signup) {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            intent.putExtra("Fcmkey", gcm_code);
            startActivity(intent);
        }
        if (v == login) {
            //CallLogin();
        }
    }

    private void CallLogin() {

        sphoneno = phoneno.getText().toString();
        spassword = password.getText().toString();

        if (sphoneno.length() < 10) {
            phoneno.setError("Please Enter valid Phone Number");
            return;
        }
        if (spassword.length() < 2) {
            password.setError("Please Enter Valid Password");
        }

        try {
            JSONObject jsonParams = new JSONObject();
            StringEntity entity = null;
            AsyncHttpClient client = new AsyncHttpClient();
            entity = new StringEntity(jsonParams.toString());
            client.post(LoginActivity.this,
                    CommonUtil.mbaseurl + "users/UserLogin?phone=" + sphoneno + "&password=" + spassword + "&gcmcode=" + gcm_code, entity,
                    "application/json", getResponseHandler("NormalLogin"));
            System.out.println("inpu :" + jsonParams.toString());

        } catch (Exception e) {
            System.err.println("Error in inserting" + e.toString());

        }
    }

    private ResponseHandlerInterface getResponseHandler(final String connectionfor) {
        return new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                progressDialog = ProgressDialog.show(LoginActivity.this,
                        "In progress", "Please wait");
            }

            @Override
            public void onFinish() {
                // Completed the request (either success or failure)
                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] resp) {
                if (connectionfor.contains("Login")) {
                    String response = new String(resp);
                    System.out.println("Login" + response);
                    String Message;
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Message = jsonObject.getString("Message");
                        if (Message.contains("Successfull")) {
                            dbHelper.insertdata(jsonObject.toString(), DBHelper.USER_TBL);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Message = jsonObject.getString("Message");
                            if (Message.contains("Invalid Credential!")) {
                                CommonUtil.showToast(LoginActivity.this, "Invalid Credential!");
                            } else if (Message.contains("No Data Found!")) {
                                CommonUtil.showToast(LoginActivity.this, "No Data Found!");
                            }

                        }


                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                CommonUtil.showToast(LoginActivity.this, getString(R.string.something_went_wrong));
            }
        };
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("TAG", "onConnectionFailed:" + connectionResult);

    }
}