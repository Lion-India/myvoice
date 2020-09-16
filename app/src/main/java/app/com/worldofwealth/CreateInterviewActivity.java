package app.com.worldofwealth;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import app.com.worldofwealth.models.States;
import app.com.worldofwealth.models.User;
import app.com.worldofwealth.utils.CommonUtil;
import app.com.worldofwealth.utils.DBHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class CreateInterviewActivity extends AppCompatActivity {

    String mtitle, mdesc, mstartdate, mstarttime, mendtime;
    ArrayList<States> stateList;
    private Calendar calendar;
    private int year, month, day;
    Calendar startDateCalender;
    Calendar endDateCalender;
    ArrayList<String> stringArrayList;
    String mhour, mmin;
    ListView lusers;
    MyCustomAdapter dataAdapter = null;
    ProgressDialog progressDialog;
    DBHelper dbHelper;
    User user;
    EditText etitle, edesc, estartdate, estarttime, eendtime, einvite;
    Button bsave, bcancel;
    static final int DATE_DIALOG_ID = 0;
    final Calendar myCalendar = Calendar.getInstance();
    EditText inputSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_interview);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.create_interview);
        dbHelper = new DBHelper(getApplicationContext());
        user = dbHelper.getUserData();
        lusers = (ListView) findViewById(R.id.list);
        bcancel = (Button) findViewById(R.id.cancel);
        bsave = (Button) findViewById(R.id.save);
        etitle = (EditText) findViewById(R.id.ititle);
        edesc = (EditText) findViewById(R.id.idesc);
        estartdate = (EditText) findViewById(R.id.date);
        estarttime = (EditText) findViewById(R.id.stime);
        eendtime = (EditText) findViewById(R.id.etime);
        einvite = (EditText) findViewById(R.id.invite);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month + 1, day);

        inputSearch = (EditText) findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

//                if(s.toString().length() > 0){
//                    CreateInterviewActivity.this.dataAdapter.getFilter().filter(s);
//                }else {
//
//                }

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                dataAdapter = new CreateInterviewActivity.MyCustomAdapter(getApplicationContext(), R.layout.state_info, stateList);
                lusers.setAdapter(dataAdapter);
                dataAdapter.notifyDataSetChanged();
                CreateInterviewActivity.this.dataAdapter.getFilter().filter(s);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        stringArrayList = new ArrayList<>();

        final FragmentManager fm = ((AppCompatActivity) CreateInterviewActivity.this).getSupportFragmentManager();

        estartdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(CreateInterviewActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        estarttime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDailog("start");
            }
        });
        eendtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDailog("end");
            }
        });

        startDateCalender = Calendar.getInstance();
        endDateCalender = Calendar.getInstance();
        bsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubmitInterview();
            }
        });
        bcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        displayListView();

    }
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(this,MainActivity.class));
        return true;
    }
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(myCalendar.getTime());
            estartdate.setText(selectedDate);
        }

    };

    private void SubmitInterview() {

        mtitle = etitle.getText().toString();
        mdesc = edesc.getText().toString();
        mstartdate = estartdate.getText().toString();
        mstarttime = estarttime.getText().toString();
        mendtime = eendtime.getText().toString();
        if (mtitle.length() < 1) {
            CommonUtil.showToast(getApplicationContext(), getString(R.string.enter_title));
            return;
        }
        if (mdesc.length() < 1) {
            CommonUtil.showToast(getApplicationContext(), getString(R.string.enter_desc));
            return;
        }
        if (mstartdate.length() < 1) {
            CommonUtil.showToast(getApplicationContext(), getString(R.string.select_start_date));
            return;
        }
        if (mstarttime.length() < 1) {
            CommonUtil.showToast(getApplicationContext(), getString(R.string.select_start_time));
            return;
        }
        if (mendtime.length() < 1) {
            CommonUtil.showToast(getApplicationContext(), getString(R.string.select_end_time));
            return;
        }
        if (stateList.size() < 1) {
            CommonUtil.showToast(getApplicationContext(), getString(R.string.select_users));
            return;
        }
        final ArrayList<States> stateList = dataAdapter.stateList;
        JSONArray jsonArray = new JSONArray();
        try {
            //Adding created by
            JSONObject job = new JSONObject();
            job.put("userid", user.getUid());
            job.put("firstname", user.getFirstname());
            job.put("lastname", user.getLastname());
            jsonArray.put(job);

            for (int i = 0; i < stateList.size(); i++) {
                States state = stateList.get(i);
                if (state.isSelected()) {
                    job = new JSONObject();
                    job.put("userid", state.getId());
                    job.put("firstname", state.getFname());
                    job.put("lastname", state.getLname());
                    jsonArray.put(job);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (jsonArray.length() < 2) {
            CommonUtil.showToast(getApplicationContext(), getString(R.string.select_user));
            return;
        }

        try {
            JSONObject job = new JSONObject();

            String startTime = startDateCalender.get(Calendar.HOUR_OF_DAY) + ":" + startDateCalender.get(Calendar.MINUTE) + ":00";
            String endTime = endDateCalender.get(Calendar.HOUR_OF_DAY) + ":" + startDateCalender.get(Calendar.MINUTE) + ":00";
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            try {
                Date d1 = sdf.parse(startTime);
                Date d2 = sdf.parse(endTime);
                //long elapsed = d2.getTime() - d1.getTime();

                if (d1.after(d2)) {
                    CommonUtil.showToast(getApplicationContext(), "Start time cannot be greater than End time");
                    return;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            job.put("title", mtitle);
            job.put("description", mdesc);
            job.put("startdate", mstartdate);
            job.put("starttime", mstarttime);
            job.put("enddate", mstartdate);
            job.put("endtime", mendtime);
            job.put("createdby", user.getUid());
            job.put("invitedusers", jsonArray);
            job.put("datatype", "Interview");
            StringEntity entity = new StringEntity(job.toString());
            progressDialog = new ProgressDialog(CreateInterviewActivity.this);
            progressDialog.setMessage(getString(R.string.please_wait));
            progressDialog.setTitle(getString(R.string.app_name));
            progressDialog.show();
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            AsyncHttpClient client = new AsyncHttpClient();
            String murl = CommonUtil.mbaseurl + "Interview/PostInterview";
            client.post(getApplicationContext(), murl, entity, "application/json",
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            String response = new String(responseBody);
                            System.out.println("Response: " + response);
                            progressDialog.cancel();
                            try {
                                JSONObject job = new JSONObject(response);
                                String Message = job.getString("Message");
                                if (Message.equals("Successfull")) {
                                    CommonUtil.showToast(getApplicationContext(), getString(R.string.success));
                                    Intent intent = new Intent(CreateInterviewActivity.this, InterviewActivity.class);
                                    startActivity(intent);
                                    //inteny
                                    etitle.setText("");
                                    edesc.setText("");
                                    estartdate.setText("");
                                    eendtime.setText("");
                                    estarttime.setText("");
                                    for (int i = 0; i < stateList.size(); i++) {
                                        States states = stateList.get(i);
                                        states.setSelected(false);
                                        stateList.set(i, states);
                                    }
                                    dataAdapter.notifyDataSetChanged();

                                } else {
                                    //CommonUtil.showToast(AddInterviewActivity.this, getString(R.string.something_went_wrong));
                                    if (Message.equals("Interview Already Exists!")) {
                                        CommonUtil.showToast(getApplicationContext(), getString(R.string.interview_already_exists));
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                CommonUtil.showToast(getApplicationContext(), getString(R.string.something_went_wrong));
                            }


                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            progressDialog.cancel();
                            CommonUtil.showToast(getApplicationContext(), getString(R.string.something_went_wrong));
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void displayListView() {

        String url = CommonUtil.mbaseurl + "Users/GetUsers?usertype=All";

        progressDialog = new ProgressDialog(CreateInterviewActivity.this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                // System.out.println("response : " + response);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    stateList = new ArrayList<States>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject job = new JSONObject(jsonArray.get(i).toString());
                        String firstname = job.getString("firstname");
                        String lastname = job.getString("lastname");
                        String phoneno = job.getString("phone");
                        String status = job.getString("status");
                        String id = job.getString("id");
                        if (!id.equals(user.getUid()) && status.equals("Active")) {  // To remove the loggeding id
                            stateList.add(new States(phoneno, firstname, lastname, id, false));
                        }
                    }
                    dataAdapter = new CreateInterviewActivity.MyCustomAdapter(getApplicationContext(), R.layout.state_info, stateList);
                    lusers.setAdapter(dataAdapter);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                progressDialog.cancel();
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.cancel();
                CommonUtil.showToast(getApplicationContext(), getString(R.string.something_went_wrong));
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check for the results
        if (requestCode == DATE_DIALOG_ID && resultCode == Activity.RESULT_OK) {
            // get date from string
            String selectedDate = data.getStringExtra("selectedDate");
            // set the value of the editText
            estartdate.setText(selectedDate);
        }
    }

    private void showTimePickerDailog(final String str) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);


        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(CreateInterviewActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                mhour = "" + selectedHour;
                mmin = "" + selectedMinute;
                if (selectedMinute < 9) {
                    mmin = "0" + selectedMinute;
                }
                if (selectedHour < 9) {
                    mhour = "0" + selectedHour;
                }
                if (str.equals("start")) {
                    estarttime.setText(mhour + ":" + mmin);
                    startDateCalender.set(Calendar.HOUR_OF_DAY, selectedHour);
                    startDateCalender.set(Calendar.MINUTE, selectedMinute);
                } else if (str.equals("end")) {
                    eendtime.setText(mhour + ":" + mmin);
                    endDateCalender.set(Calendar.HOUR_OF_DAY, selectedHour);
                    endDateCalender.set(Calendar.MINUTE, selectedMinute);
                }
            }
        }, hour, minute, true);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }


    private void showDate(int year, int month, int day) {
        String mdate, mmonth;
        mdate = "" + day;
        mmonth = "" + month;
        if (day < 9) {
            mdate = "0" + day;
        }
        if (month < 9) {
            mmonth = "0" + month;
        }
        estartdate.setText(new StringBuilder().append(year).append("-")
                .append(mmonth).append("-").append(mdate));
    }


    private class MyCustomAdapter extends BaseAdapter {

        private ArrayList<States> stateList;

        public MyCustomAdapter(Context context, int textViewResourceId,

                               ArrayList<States> stateList) {

            this.stateList = stateList;
//            this.stateList.addAll(stateList);
        }


        private class ViewHolder {
            TextView code;
            CheckBox name;

            public ViewHolder(View view) {
                code = view.findViewById(R.id.code);
                name = view.findViewById(R.id.checkBox1);
            }
        }

        @Override
        public int getCount() {
            return stateList.size();
        }

        @Nullable
        @Override
        public States getItem(int position) {
            return stateList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return stateList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {

//                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.state_info, parent, false);
                holder = new ViewHolder(convertView);
//                holder.code = (TextView) convertView.findViewById(R.id.code);
//                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);

                convertView.setTag(holder);

//                holder.name.setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        CheckBox cb = (CheckBox) v;
//                States _state = (States) cb.getTag();
//
//                _state.setSelected(cb.isChecked());
//                    }
//                });


            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            final States state = stateList.get(position);
            holder.code.setText(" (" + state.getCode() + ")");
            holder.name.setText(state.getFname() + " " + state.getLname());
            holder.name.setChecked(stateList.get(position).isSelected());

            holder.name.setTag(state);

            final ViewHolder finalHolder1 = holder;
            holder.name.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {


                    if (b) {
                        if (stringArrayList.size() >= 3) {
                            CommonUtil.showToast(getApplicationContext(), getString(R.string.people_allowed));
                            finalHolder1.name.setChecked(false);
                        } else {
                            stringArrayList.add(state.getCode());
                            state.setSelected(true);
                        }

                    } else {
                        state.setSelected(false);
                        if (stringArrayList.contains(state.getCode())) {
                            stringArrayList.remove(state.getCode());
                        }
                    }
                }
            });

            return convertView;
        }

        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    stateList = (ArrayList<States>) results.values;
                    notifyDataSetChanged();
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    ArrayList<States> FilteredArrList = new ArrayList<States>();

                    if (stateList == null){
                        stateList = new ArrayList<States>(stateList);
                    }

                    if (constraint == null || constraint.length() == 0) {

                        // set the Original result to return
                        results.count = stateList.size();
                        results.values = stateList;
                    } else {
                        constraint = constraint.toString().toLowerCase();
                        for (int i = 0; i < stateList.size(); i++) {
                            String fname = stateList.get(i).getFname();
                            String phoneNumber =  stateList.get(i).getCode();
                            if (fname.toLowerCase().startsWith(constraint.toString()) || phoneNumber.toLowerCase().startsWith(constraint.toString())) {
                                //FilteredArrList.add(new States(stateList.get(i));
                                FilteredArrList.add(new States(stateList.get(i).getCode(), stateList.get(i).getFname(), stateList.get(i).getLname(), stateList.get(i).getId(), false));
                            }
                        }
                        // set the Filtered result to return
                        results.count = FilteredArrList.size();
                        results.values = FilteredArrList;
                    }
                    return results;
                }
            };
            return filter;
        }

    }
}