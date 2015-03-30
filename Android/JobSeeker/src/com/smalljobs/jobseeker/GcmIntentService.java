package com.smalljobs.jobseeker;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.smalljobs.jobseeker.models.Notification;
import com.smalljobs.jobseeker.views.MainActivity;

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
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                //sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                //sendNotification("Deleted messages on server: " +
                //        extras.toString());
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                
                String received = extras.toString().substring(7,extras.toString().length()-1);
                System.out.println(received);
                
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
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification() {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_logo)
        .setContentTitle(notification.getType())
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(notification.getJob().getTitle()))
        .setContentText(notification.getJob().getTitle());

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Uri soundUri =  Uri.parse(sharedPref.getString("notifications_new_message_ringtone", ""));
        
       	if (sharedPref.getBoolean("notifications_new_message", false)) {
       		mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
       	}
        
        mBuilder.setSound(soundUri);
        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}