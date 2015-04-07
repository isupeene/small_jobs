package com.smalljobs.jobseeker;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.smalljobs.jobseeker.models.Notification;
import com.smalljobs.jobseeker.views.LoginActivity;

/**
 * Intent Service for receiving a GCM message
 * 
 * Requirements Specifications Reference:
 * 3.2.2.3.2 Raise a notification when important events occur
 * 3.2.2.3.2.1 When a bid of theirs has been accepted or rejected, 
 *             or when a job on which they have bid is modified.
 */

public class GcmIntentService extends IntentService {
	
    public static final int NOTIFICATION_ID = 1;
    static final String TAG = "GCM";
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    
    private Notification notification;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
            	
                // Post notification of received message.
                String received = extras.toString().substring(7,extras.toString().length()-1);
                
                Gson gson = new Gson();
                JsonParser parser = new JsonParser();
                JsonObject obj = parser.parse(received).getAsJsonObject();

                notification = gson.fromJson( obj , Notification.class);
                
                String type = notification.getType();
                
                switch(notification.getType()) {
                case ("job_modified"):
                	type = "Job modified";
                	break;
                case ("job_deleted"):
                	type = "Job deleted";
                	break;
                case ("bid_accepted"):
                	type = "Your bid was accepted";
                	break;
                case ("bid_rejected"):
                	type = "Your bid was rejected";
                	break;
                }
                
                notification.setType(type);
                
                SharedPreferences credentials = this.getSharedPreferences("credentials", 0);
                
                NotificationsManager nm = new NotificationsManager(this, credentials.getString("email", "default"));
                nm.saveNotification(notification);
                
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
                boolean wantNotifications = sharedPref.getBoolean("notifications_new_message", false);
                
                if (wantNotifications) {
                    sendNotification();
                }
                
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    private void sendNotification() {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, LoginActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_notif_logo)
        .setContentTitle(notification.getType())
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(notification.getJob().getTitle()))
        .setContentText(notification.getJob().getTitle());

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        
       	if (sharedPref.getBoolean("notifications_new_message", false)) {
       		mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
       	}
        
       	Uri soundUri =  Uri.parse(sharedPref.getString("notifications_new_message_ringtone", ""));
        mBuilder.setSound(soundUri);
        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}