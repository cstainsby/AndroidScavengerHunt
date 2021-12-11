// ---------------------------------------------------------------------------
// DESC:
//   This class will help with the construction of geofences
// PROJECT:
//   Scavenger Hunt project
// SOURCES:
//   https://developer.android.com/training/location/geofencing
// DATE:
//   12/11/2021
// ---------------------------------------------------------------------------

package stainsby.cole.androidscavengerhunt;

import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.model.LatLng;

public class GeofenceHelper extends ContextWrapper {

    private static final String TAG = "GeofenceHelper";
    private PendingIntent pendingIntent;
    
    public GeofenceHelper(Context base) {
        super(base);
    }

    /**
     * specify the geofences to monitor and set how geofence events are triggered
     * @param geofence
     * @return geofenceRequest
     */
    public GeofencingRequest getGeofenceRequest(Geofence geofence) {
        GeofencingRequest.Builder geofencingRequestBuilder = new GeofencingRequest.Builder();
        geofencingRequestBuilder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        geofencingRequestBuilder.addGeofence(geofence);

        return geofencingRequestBuilder.build();
    }

    /**
     * create a geofence, setting the desired radius, duration, and transition types for the geofence
     * @param id
     * @param latLng
     * @param radius
     * @return a geofence
     */
    public Geofence createGeofence(String id, LatLng latLng, float radius) {
        Geofence geofence = new Geofence.Builder()
                .setRequestId(id)
                .setCircularRegion(
                        latLng.latitude,
                        latLng.longitude,
                        radius)
                .setTransitionTypes(
                        Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();
        return geofence;
    }

    /**
     * define a pending intent that starts a broadcast reciever
     * @return
     */
    public PendingIntent getPendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if(pendingIntent != null) {
            return pendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReciever.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;
    }

    public String getErrorString(Exception e) {
        if(e instanceof ApiException) {
        }
        return null;
    }
}
