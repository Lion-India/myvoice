package app.com.worldofwealth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

import app.com.worldofwealth.utils.CommonUtil;
import app.com.worldofwealth.utils.DBHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    String gcm_code;
    static String mgender = "";
    EditText efname, elname, eemail, ephoneno, edob, epassword,cpassword;
    RadioButton radioMale, radioFemale;
    Button bregister, blogin;
    //Calendar calendar;
    //int year, month, day;
    static String mfname, mlname, memail, mphoneno, mdob,mpassword,mcpassword,sdob;
    ProgressDialog progressDialog;
    JSONObject jsonParams = new JSONObject();
    DBHelper dbHelper;
    EditText editdate;

    private String current = "";
    private String ddmmyyyy = "ddmmyyyy";
    private Calendar cal = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Intent intent = getIntent();
        gcm_code = intent.getStringExtra("Fcmkey");
        //System.out.println("gcm :" + gcm_code);

        dbHelper = new DBHelper(RegisterActivity.this);
        efname = (EditText) findViewById(R.id.firstNameEdit);
        elname = (EditText) findViewById(R.id.lastNameEdit);
        eemail = (EditText) findViewById(R.id.emailRegisterEdit);
        ephoneno = (EditText) findViewById(R.id.mobileNumberEdit);
        edob = (EditText) findViewById(R.id.dateOfBirthEdit);
        radioMale = (RadioButton) findViewById(R.id.radioMale);
        radioFemale = (RadioButton) findViewById(R.id.radioFemale);
        epassword=(EditText)findViewById(R.id.editPassword) ;
        cpassword=(EditText)findViewById(R.id.confirmEditPassword);
        bregister = (Button) findViewById(R.id.registerButton);
        blogin = (Button) findViewById(R.id.login);
        bregister.setOnClickListener(this);
        blogin.setOnClickListener(this);
        editdate = (EditText)findViewById(R.id.edittextdate);
        //editdate.addTextChangedListener(tw);




        /*calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        String mmonth, mdate;
        int smonth = month + 1;
        mdate = "" + day;
        mmonth = "" + smonth;
        if (month < 9) {
            mmonth = "0" + smonth;
        }
        if (day < 9) {
            mdate = "0" + day;
        }*/

       /* edob.setText(new StringBuilder().append(year).append("-")
                .append(mmonth).append("-").append(mdate));

        edob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

                        monthOfYear = monthOfYear + 1;
                        String mdate, mmonth;
                        mmonth = "" + monthOfYear;
                        mdate = "" + dayOfMonth;
                        if (monthOfYear < 9) {
                            mmonth = "0" + monthOfYear;
                        }
                        if (dayOfMonth < 9) {
                            mdate = "0" + dayOfMonth;
                        }

                        String date_selected = year + "-" + mmonth + "-"
                                + mdate;

                        edob.setText(date_selected);

                    }
                }, year, month, day);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.setTitle("");
                datePickerDialog.show();
            }
        });*/

        radioFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mgender = "Female";
            }
        });
        radioMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mgender = "Male";
            }
        });
    }

    /*TextWatcher tw = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().equals(current)) {
                String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                int cl = clean.length();
                int sel = cl;
                for (int i = 2; i <= cl && i < 6; i += 2) {
                    sel++;
                }
                //Fix for pressing delete next to a forward slash
                if (clean.equals(cleanC)) sel--;

                if (clean.length() < 8){
                    clean = clean + ddmmyyyy.substring(clean.length());
                }else{
                    //This part makes sure that when we finish entering numbers
                    //the date is correct, fixing it otherwise
                    int day  = Integer.parseInt(clean.substring(0,2));
                    int mon  = Integer.parseInt(clean.substring(2,4));
                    int year = Integer.parseInt(clean.substring(4,8));

                    mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                    cal.set(Calendar.MONTH, mon-1);
                    year = (year<1900)?1900:(year>2100)?2100:year;
                    cal.set(Calendar.YEAR, year);
                    // ^ first set year for the line below to work correctly
                    //with leap years - otherwise, date e.g. 29/02/2012
                    //would be automatically corrected to 28/02/2012

                    day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                    clean = String.format("%02d%02d%02d",day, mon, year);
                }

                clean = String.format("%s-%s-%s", clean.substring(0, 2),
                        clean.substring(2, 4),
                        clean.substring(4, 8));

                sel = sel < 0 ? 0 : sel;
                current = clean;
                editdate.setText(current);
                editdate.setSelection(sel < current.length() ? sel : current.length());
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }


    };*/

    @Override
    public void onClick(View v) {
        if (v==blogin){
            startActivity(new Intent(this,LoginActivity.class));
        }
        if(v==bregister){
            String datedefault = "01-01-";
            String senddate = "-01-01";
            mfname = efname.getText().toString().trim();
            mlname = elname.getText().toString().trim();
            memail = eemail.getText().toString().trim();
            mphoneno = ephoneno.getText().toString();
            mpassword=epassword.getText().toString();
            mcpassword=cpassword.getText().toString();
            //mdob = edob.getText().toString();
            if (mfname.length() < 2) {
                efname.setError(this.getString(R.string.enter_fname));
                return;
            }
            if (mphoneno.length() < 10) {
                ephoneno.setError(this.getString(R.string.enter_phone));
                return;
            }
            if ((memail.length() > 0) && (!CommonUtil.isValidEmailAddress(memail))) {
                eemail.setError(this.getString(R.string.error_email));
            }

            if (mpassword.length() < 4) {
                epassword.setError(getText(R.string.error_field));
                return;
            }
            if (!mcpassword.equals(mpassword)) {
                cpassword.setError(getText(R.string.error_password));
                return;
            }
            if (editdate.getText().toString().length() < 4) {
                editdate.setError(getText(R.string.error_dateofbirth));
                return;
            }
            sdob = getDateofbirth(datedefault+editdate.getText().toString());





            try {

                StringEntity entity = null;
                AsyncHttpClient client = new AsyncHttpClient();
                jsonParams.put("firstname", mfname);
                jsonParams.put("lastname", mlname);
                jsonParams.put("phone", mphoneno);
                jsonParams.put("email", memail);
                jsonParams.put("password",mpassword);
                jsonParams.put("gender", mgender);
                jsonParams.put("dob", sdob);
                jsonParams.put("userimageurl", "");
                jsonParams.put("lon", "");
                jsonParams.put("lat", "");
                jsonParams.put("gcmcode", gcm_code);
                entity = new StringEntity(jsonParams.toString());
                //System.out.println("entity :"+jsonParams.toString());
                client.post(RegisterActivity.this,
                        CommonUtil.mbaseurl + "users/PostUser", entity,
                        "application/json", getResponseHandler("register"));
                //System.out.println("inpu :" + jsonParams.toString());

            } catch (Exception e) {
                System.err.println("Error in inserting" + e.toString());

            }

        }

    }

    private ResponseHandlerInterface getResponseHandler(final String connectionfor) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                progressDialog = ProgressDialog.show(RegisterActivity.this,
                        "In progress", "Please wait");
            }

            @Override
            public void onFinish() {
                // Completed the request (either success or failure)
                progressDialog.dismiss();
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] resp) {
                if (connectionfor.contains("register")) {
                    String response = new String(resp);
                    //System.out.println("res :" + response.toString());
                    try {
                        JSONObject jsonObject=new JSONObject(response);
                        dbHelper.insertdata(jsonObject.toString(), DBHelper.USER_TBL);
                        Intent intent=new Intent(RegisterActivity.this,OtpVerifiction.class);
                        intent.putExtra("phoneo",mphoneno);
                        intent.putExtra("jsondata",jsonParams.toString());
                        startActivity(intent);
                        /*String Message=jsonObject.getString("Message");
                        if(Message.equals("Successfull")){
                            Toast.makeText(RegisterActivity.this, "Please Verify the OTP!", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(RegisterActivity.this,OtpVerifiction.class);
                            intent.putExtra("phoneo",mphoneno);
                            intent.putExtra("jsondata",jsonParams.toString());
                            startActivity(intent);
                        }*/

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                CommonUtil
                        .showToast(getApplicationContext(),
                                getText(R.string.something_went_wrong).toString());
            }
        };
    }

    private String getDateofbirth(String dateofbirth) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy");
        SimpleDateFormat output = new SimpleDateFormat("yyyy-mm-dd");
        Date sd = null;
        try {
            sd = sdf.parse(dateofbirth);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return output.format(sd);
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
}
