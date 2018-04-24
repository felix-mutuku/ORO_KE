package com.propertiesKE;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.Objects;

public class BackgroundService extends Service {
    public Context context = this;
    public Handler handler = null;
    public static Runnable runnable = null;
    public static final String DEFAULT = "N/A";
    public String SFirstname, SEmail, SLastname;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //getting information entered by user
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SFirstname = sharedPreferences.getString(Constants.FIRSTNAME, DEFAULT);
        SEmail = sharedPreferences.getString(Constants.EMAIL2, DEFAULT);
        SLastname = sharedPreferences.getString(Constants.LASTNAME, DEFAULT);

        //Toast.makeText(this, "Service created!", Toast.LENGTH_SHORT).show();

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                //Toast.makeText(context, "Service is still running", Toast.LENGTH_SHORT).show();
                //action that background service repeats
                new CheckNewMessage().execute(new ApiConnector());
                handler.postDelayed(runnable, 15000);//ten seconds
            }
        };

        handler.postDelayed(runnable, 15000);//ten seconds
    }

    @Override
    public void onDestroy() {
        //kills service when the app is killed
        //handler.removeCallbacks(runnable);
        Toast.makeText(this, "Background Service stopped", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart(Intent intent, int startid) {
        //action to perform when service started
        //Toast.makeText(this, "Service started by user.", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("StaticFieldLeak")
    private class CheckNewMessage extends AsyncTask<ApiConnector, Long, String> {
        @Override
        protected String doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            return params[0].CheckNewMessage(SEmail);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String response) {
            try {
                if (Objects.equals(response, "Exist")) {
                    //a new message exists in database
                    //show new message notification
                    messageNotification();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void messageNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Notification notification;
        notification = builder.setSmallIcon(R.drawable.ic_stat_name)
                .setTicker("You have a new message.")
                .setWhen(0)
                .setContentTitle("New Message.")
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(SFirstname + " you have new messages."))
                .setAutoCancel(true)
                .setContentText(SFirstname + " " + SLastname + " you have new messages.")
                .build();

        Intent notificationIntent = new Intent(this, UserProfileActivity.class);
        notificationIntent.putExtra("fragmentCreate", "chat");
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        //add sound
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(uri);
        //vibrate
        long[] v = {500, 1000};
        builder.setVibrate(v);

        //Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.notify(0, builder.build());

    }
}
