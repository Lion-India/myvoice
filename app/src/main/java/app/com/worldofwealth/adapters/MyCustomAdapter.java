package app.com.worldofwealth.adapters;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;



import app.com.worldofwealth.R;
import app.com.worldofwealth.models.States;

import java.util.ArrayList;

public class MyCustomAdapter extends BaseAdapter {
    public ArrayList<States> stateList;
    private Context context;
    ArrayList<String> stringArrayList;

    public MyCustomAdapter(Context context, int textViewResourceId,

                           ArrayList<States> stateList, ArrayList<String> stringArrayList) {

        this.stateList = stateList;
        this.context=context;
        this.stringArrayList=stringArrayList;

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

            convertView = LayoutInflater.from(context).inflate(R.layout.state_info, parent, false);
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
                        Toast.makeText(context, "Only three people are allowed", Toast.LENGTH_SHORT).show();
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
}
