package com.smalljobs.jobseeker;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Broadcast receiver for receiving a GCM message
 * 
 * Requirements Specifications Reference:
 * 3.2.2.3.2 Raise a notification when important events occur
 * 3.2.2.3.2.1 When a bid of theirs has been accepted or rejected, 
 *             or when a job on which they have bid is modified.
 */

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Explicitly specify that GcmIntentService will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(),
                GcmIntentService.class.getName());
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}