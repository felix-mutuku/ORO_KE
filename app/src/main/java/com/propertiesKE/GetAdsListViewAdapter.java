package com.propertiesKE;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

public class GetAdsListViewAdapter extends BaseAdapter {
    private JSONArray dataArray;
    String Image;
    private static LayoutInflater inflater = null;
    private Context context;
    Activity activity;

    public GetAdsListViewAdapter(JSONArray jsonArray, Activity a) {
        try {
            this.dataArray = jsonArray;
            activity = a;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GetAdsListViewAdapter(Context context) {
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
            convertView = inflater.inflate(R.layout.get_all_ads_list_view_cell, null);
            cell = new ListCell();

            cell.location = convertView.findViewById(R.id.location);
            cell.date = convertView.findViewById(R.id.date);
            cell.views = convertView.findViewById(R.id.views);
            cell.title = convertView.findViewById(R.id.title);
            cell.ad_pic = convertView.findViewById(R.id.ad_pic);
            cell.price = convertView.findViewById(R.id.price);
            cell.top = convertView.findViewById(R.id.top);

            convertView.setTag(cell);
        } else {
            cell = (ListCell) convertView.getTag();
        }

        //change the data of cell
        try {
            JSONObject jsonObject = this.dataArray.getJSONObject(position);

            Image = jsonObject.getString("path");

            String top_ad = jsonObject.getString("ad_type");
            int SPrice = jsonObject.getInt("price");
            String random = jsonObject.getString("acode");

            if (Objects.equals(top_ad, "Top")) {
                cell.top.setText("TOP AD");
                cell.top.setBackground(activity.getResources().getDrawable(R.drawable.bg_button3));
            } else {
                cell.top.setText("");
                cell.top.setBackgroundColor(00000000);
            }

            Glide
                    .with(activity.getApplicationContext())
                    .load(Constants.BASE_URL2 + "uploader/ads/" + random + "/" + Image)
                    .thumbnail(0.1f)
                    .error(R.drawable.non_image)
                    .crossFade()
                    .into(cell.ad_pic);

            cell.date.setText(jsonObject.getString("date_posted") + " " + jsonObject.getString("time_posted"));
            cell.location.setText(jsonObject.getString("county").replace("_", " "));
            cell.views.setText(jsonObject.getString("ad_views"));
            cell.title.setText(jsonObject.getString("title"));

            if (SPrice == 0) {
                cell.price.setText("Price on Enquiry");
            } else {
                String Sprice2 = NumberFormat.getNumberInstance(Locale.US).format(SPrice);
                cell.price.setText("Kes. " + Sprice2);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }

    private class ListCell {
        private TextView location;
        private TextView date;
        private TextView views;
        private TextView title;
        private TextView price;
        private TextView top;
        private ImageView ad_pic;
    }
}
