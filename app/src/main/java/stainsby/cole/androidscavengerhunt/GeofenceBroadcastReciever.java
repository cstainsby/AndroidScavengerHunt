package stainsby.cole.androidscavengerhunt;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceBroadcastReciever extends BroadcastReceiver {

    private static final String TAG = "GeoBroadcastReciever";

    public GeofenceBroadcastReciever() {
        super();
    }

    /**
     * broadcast reciever that will post a notification when a geofence transition occurs
     * for this reciever we will post when the user exits or enters the geofence
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        // Log errors
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes
                    .getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            // Log the details
            String geofenceTransitionDetails = "";
            for (int i = 0; i < triggeringGeofences.size(); i++) {
                geofenceTransitionDetails = geofenceTransitionDetails.concat(triggeringGeofences.toString() + "\n");
            }
            Log.i(TAG, geofenceTransitionDetails);

            
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                    new Intent(context, ScavengerHuntActivity.class), 0);

            // Send notification and log the transition details.
            if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                // notify the user that they are exiting the play area
                notificationBuilder
                        .setContentTitle("Warning: Leaving play area")
                        .setContentText("You are exiting the boundary of the game area, please turn around");
            }
            else {
                // notify the user that they are entering the play area
                notificationBuilder
                        .setContentTitle("Entering play area")
                        .setContentText("You have entered the play area");
            }

            notificationBuilder.setContentIntent(contentIntent);
            notificationBuilder.setDefaults(Notification.DEFAULT_SOUND);
            notificationBuilder.setAutoCancel(true);
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, notificationBuilder.build());
        } else {
            // Log the error.
            Log.d(TAG, "onReceive: invalid geofence broadcast");
        }
    }
}