package com.propertiesKE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class GetChatsListViewAdapter extends BaseAdapter {
    private JSONArray dataArray;
    private static LayoutInflater inflater = null;
    private Context context;
    Activity activity;
    public static final String DEFAULT = "N/A";

    public GetChatsListViewAdapter(JSONArray jsonArray, Activity a) {
        try {
            this.dataArray = jsonArray;
            activity = a;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GetChatsListViewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        try {
            return this.dataArray.length();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // set up convert view if it is null
        ListCell cell;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.get_all_chats_list_view_cell, null);
            cell = new ListCell();

            cell.subject = convertView.findViewById(R.id.subject);
            cell.name = convertView.findViewById(R.id.name);
            cell.message = convertView.findViewById(R.id.message);
            cell.date = convertView.findViewById(R.id.date);
            cell.read = convertView.findViewById(R.id.read);
            convertView.setTag(cell);
        } else {
            cell = (ListCell) convertView.getTag();
        }

        //change the data of cell
        try {
            JSONObject jsonObject = this.dataArray.getJSONObject(position);

            String read = jsonObject.getString("status");

            if (Objects.equals(read, "unread")) {
                cell.read.setText("UNREAD");
                cell.read.setBackground(activity.getResources().getDrawable(R.drawable.bg_button3));
            } else {
                cell.read.setVisibility(View.GONE);
            }

            cell.date.setText("Sent on: " + jsonObject.getString("date_sent") + " at " + jsonObject.getString("time_sent"));
            cell.subject.setText(jsonObject.getString("subject"));

            cell.name.setText("Sent by " + jsonObject.getString("from_name"));
            cell.message.setText(jsonObject.getString("message"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }

    private class ListCell {
        private TextView subject;
        private TextView name;
        private TextView message;
        private TextView date;
        private TextView read;
    }
}
