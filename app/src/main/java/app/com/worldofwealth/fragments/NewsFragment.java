package app.com.worldofwealth.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;




import app.com.worldofwealth.R;
import app.com.worldofwealth.adapters.NewsAdapter;
import app.com.worldofwealth.models.Post;
import app.com.worldofwealth.models.User;
import app.com.worldofwealth.utils.CommonUtil;
import app.com.worldofwealth.utils.DBHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class NewsFragment extends Fragment {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<Post> arrayList;
    ProgressDialog progressDialog;
    NewsAdapter nAdapter;
    User user;
    DBHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.news_fragment, container, false);
        recyclerView = (RecyclerView) root.findViewById(R.id.news_recycler_view);
        arrayList = new ArrayList<>();
        dbHelper = new DBHelper(getContext());
        user = dbHelper.getUserData();
        nAdapter = new NewsAdapter(getActivity(), arrayList, user);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(nAdapter);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        getTheNews();
    }

    private void getTheNews() {
        String murl = CommonUtil.mbaseurl + "news/GetNews?userid=" + user.getUid();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(getContext(), murl, null, "application/json",
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String response = new String(responseBody);
                        progressDialog.cancel();
                        arrayList.clear();

                        // System.out.println("Response: " + response);

                        try {
                            JSONArray jarr = new JSONArray(response);
                            for (int i = 0; i < jarr.length(); i++) {
                                JSONObject job = new JSONObject(jarr.get(i).toString());
                                Post post = CommonUtil.ParseNewsStringtoPost(job);
                                // System.out.println("post :"+job.toString());
                                arrayList.add(post);
                            }
                            nAdapter.notifyDataSetChanged();
                            // set the adapter object to the Recyclerview

                        } catch (Exception e) {
                            e.printStackTrace();
                            CommonUtil.showToast(getContext(), getString(R.string.something_went_wrong));
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        progressDialog.cancel();

                        CommonUtil.showToast(getContext(), getString(R.string.something_went_wrong));
                    }
                });
    }




}