package app.com.worldofwealth.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;



import app.com.worldofwealth.R;
import app.com.worldofwealth.models.ChatMessage;
import app.com.worldofwealth.models.Post;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatArrayAdapter extends ArrayAdapter {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final int resource = 1;
    private TextView chatText,date;
    private ArrayList<ChatMessage>  chatMessageList = new ArrayList();
    private RelativeLayout singleMessageContainer;
    Post post;


    public ChatArrayAdapter(Context context, ArrayList<ChatMessage> messagelist) {
        this(context);
        this.chatMessageList=messagelist;

    }

    public ChatArrayAdapter(@NonNull Context context) {
        super(context, resource);
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public ChatMessage getItem(int index) {
        return (ChatMessage) this.chatMessageList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.thoughtcommentrow, parent, false);
        }
        singleMessageContainer = (RelativeLayout) row.findViewById(R.id.singleMessageContainer);
        ChatMessage chatMessageObj = getItem(position);
        System.out.println("testdate :"+chatMessageObj.date);
        chatText = (TextView) row.findViewById(R.id.singleMessage);
        date = (TextView) row.findViewById(R.id.date);
        chatText.setText(chatMessageObj.message);
        String mposteddate = "";
        try {
            Date starttime = dateFormat.parse(chatMessageObj.date.replace("T", " "));
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
            mposteddate = dateFormat.format(starttime.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        date.setText(mposteddate);

       // chatText.setBackgroundResource(chatMessageObj.left ? R.drawable.bubble_a : R.drawable.bubble_b);
        singleMessageContainer.setGravity(chatMessageObj.fromme ? Gravity.RIGHT : Gravity.LEFT);
        return row;
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

}

