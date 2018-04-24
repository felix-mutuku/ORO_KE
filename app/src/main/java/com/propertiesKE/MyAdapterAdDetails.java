package com.propertiesKE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.util.List;

public class MyAdapterAdDetails extends PagerAdapter {

    private List<String> images;
    private LayoutInflater inflater;
    private Context context;
    private String SRandom;
    private static final String DEFAULT = "N/A";

    MyAdapterAdDetails(Context context, List<String> images) {
        this.context = context;
        this.images = images;
        inflater = LayoutInflater.from(context);

        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SRandom = sharedPreferences.getString(Constants.RANDOM, DEFAULT);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View myImageLayout = inflater.inflate(R.layout.slide, view, false);
        ImageView myImage = myImageLayout.findViewById(R.id.slide_image);

        myImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPics();
            }
        });

        Glide
                .with(context)
                .load(Constants.BASE_URL2 + "uploader/ads/" + SRandom + "/" + images.get(position))
                .thumbnail(0.1f)
                .crossFade()
                .into(new GlideDrawableImageViewTarget(myImage));

        view.addView(myImageLayout, 0);
        return myImageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    private void goToPics() {
        Intent intent = new Intent(context, PicsActivity.class);
        context.startActivity(intent);
    }
}