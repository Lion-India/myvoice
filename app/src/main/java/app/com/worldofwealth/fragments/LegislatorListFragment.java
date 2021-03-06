package app.com.worldofwealth.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



import app.com.worldofwealth.R;
import app.com.worldofwealth.adapters.JudiciariesUserListAdapter;
import app.com.worldofwealth.models.User;
import app.com.worldofwealth.utils.CommonUtil;
import app.com.worldofwealth.utils.DBHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class LegislatorListFragment extends Fragment {

    ProgressDialog progressDialog;
    static DBHelper dbHelper;
    static User user;
    static ArrayList<User> userlist;
    private RecyclerView recyclerView;
    private JudiciariesUserListAdapter mAdapter;
    public LegislatorListFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        /*((MainActivity) getActivity())
                .setActionBarTitle("Judiciary");*/
        View root= inflater.inflate(R.layout.fragment_judiciary, container, false);
        dbHelper = new DBHelper(getActivity());
        user = dbHelper.getUserData();
        recyclerView = (RecyclerView) root.findViewById(R.id.my_recycler_view);
        userlist = new ArrayList<User>();
        mAdapter = new JudiciariesUserListAdapter(getActivity(), userlist, user,"Legislators");
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mAdapter);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        GetUserList();
    }

    private void GetUserList() {
        String url = CommonUtil.mbaseurl + "Users/GetUsers?usertype=Legislators";

        progressDialog = new ProgressDialog(getActivity());
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
                userlist.clear();
                try {

                    JSONArray jarr = new JSONArray(response);
                    for (int i = 0; i < jarr.length(); i++) {
                        JSONObject job = new JSONObject(jarr.get(i).toString());
                        User user = ParseStringtoOpinion(job);
                        //System.out.println("UserVimal :" + job.toString());
                        userlist.add(user);
                    }
                    mAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                progressDialog.cancel();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.cancel();
                CommonUtil.showToast(getActivity(), getString(R.string.something_went_wrong));
            }
        });
    }



    private User ParseStringtoOpinion(JSONObject job) {
        User user = new User();
        try {
            String firstname = job.getString("firstname");
            String lastname = job.getString("lastname");
            String phoneno = job.getString("phone");
            String status = job.getString("status");
            String id = job.getString("id");

            user.setFirstname(firstname);
            user.setLastname(lastname);
            user.setPhone(phoneno);
            user.setStatus(status);
            user.setUid(id);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }


}
