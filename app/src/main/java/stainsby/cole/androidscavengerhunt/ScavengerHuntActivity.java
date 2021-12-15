package stainsby.cole.androidscavengerhunt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import stainsby.cole.androidscavengerhunt.databinding.ActivityScavengerHuntBinding;

public class ScavengerHuntActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityScavengerHuntBinding binding;

    private GeofencingClient geofencingClient;
    private PendingIntent pendingIntent;
    private List<LatLng> scavengerLocations;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private static final int LOCATION_REQUEST_CODE = 1;
    private static final String TAG = "ScavengerHuntActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityScavengerHuntBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        scavengerLocations = new ArrayList<>();
        // add the other in game fragments to the activity
        /*if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.inGameChatFragement, inGameChatFragment.class, null)
                    .add(R.id.inGameScavengerLocationsFragment, inGameScavengerLocationFragment.class, null)
                    .commit();
        }*/
        geofencingClient = LocationServices.getGeofencingClient(this);

        //TODO: make bottom navigation view work
        /*BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, inGameScavengerLocationFragment.newInstance());
        transaction.commit();*/
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    public void loadGameAttributes() {
        // set game attributes
        // based on the given scav locations we will need to determine the size of the geofence
        // search for the location with the largest distance from the center point of all locations
        LatLng furthestLocation = scavengerLocations.get(0);
        double meanLat = 0.0;
        double meanLong = 0.0;

        if (scavengerLocations.size() > 0) {

            for (int i = 0; i < scavengerLocations.size(); i++) {
                meanLat += scavengerLocations.get(i).latitude;
                meanLong += scavengerLocations.get(i).longitude;
            }
            // this is where the center point for the geofence will be
            meanLat /= scavengerLocations.size();
            meanLong /= scavengerLocations.size();

            for (int i = 0; i < scavengerLocations.size(); i++) {
                double distanceOfFurthest =
                        Math.sqrt(Math.pow(furthestLocation.latitude - meanLat, 2.0)
                                + Math.pow(furthestLocation.longitude - meanLong, 2.0));
                double distanceOfI =
                        Math.sqrt(Math.pow(scavengerLocations.get(i).latitude - meanLat, 2.0)
                                + Math.pow(scavengerLocations.get(i).longitude - meanLong, 2.0));

                if (distanceOfFurthest < distanceOfI) {
                    furthestLocation = scavengerLocations.get(i);
                }
            }
        }
        LatLng meanCoordinates = new LatLng(meanLat, meanLong);
        double radius = Math.sqrt(Math.pow(furthestLocation.latitude - meanLat, 2.0)
                + Math.pow(furthestLocation.longitude - meanLong, 2.0));
        // we will add additional space around the circle to make sure the
        double extraSpace = 1.0 + radius;

        // we will also offset the center coordinates by a random x < extraSpace to make sure
        // our furthest out spot will not be the same radius from center every time
        Random random = new Random();
        //int offset = random.nextInt(Math.floor(extraSpace));


        Log.d(TAG, "loadGameAttributes: furthest location is ");
        Log.d(TAG, "loadGameAttributes: drawing circle on user position with radius " + radius);
        drawCircle(meanCoordinates, 10000.0f);
        addGeofence(meanCoordinates, 10000.0f);

        for(LatLng latLng : scavengerLocations) {
            drawCircle(latLng, 1000.0f);
            addGeofence(latLng, 1000.0f);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        enableUserLocation();


        if (ActivityCompat.checkSelfPermission(ScavengerHuntActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(ScavengerHuntActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Task<Location> locationTask = mFusedLocationProviderClient.getLastLocation();
            locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        // get user position and set the camera
                        LatLng userPosition = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(userPosition));
                        mMap.setMinZoomPreference(10.0f);
                        mMap.setMaxZoomPreference(18.0f);
                        CameraUpdateFactory.zoomTo(17.0f);
                    }
                }
            });

            // load previously made markers
            Intent data = getIntent();

            Integer numScavLocations = data.getIntExtra("numScavLocs", 0);

            for (int i = 0; i < numScavLocations; i++) {
                double[] latLngDoubleArr = null;
                try {
                    latLngDoubleArr = data.getDoubleArrayExtra("cords_" + i);
                }catch (NullPointerException e) {
                    e.printStackTrace();
                }
                if(latLngDoubleArr != null) {
                    LatLng latLng = new LatLng(latLngDoubleArr[0], latLngDoubleArr[1]);
                    scavengerLocations.add(latLng);
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Scavenger Location " + i));
                }
            }
            loadGameAttributes();
        }
        else {
            Log.d(TAG, "onMapReady: error getting location");
        }
        Log.d(TAG, "onMapReady: map set");
    }

    /**
     * get permissions if needed to access user location
     */
    private void enableUserLocation() {
        // we need to get the user permission at runtime to access their FINE LOCATION
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_DENIED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // we need to request permission for the user's fin location
            // creates an alert dialog and prompts the user grant or deny
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // this callback executes once the user has made their choice in the alert dialog
        if (requestCode == LOCATION_REQUEST_CODE) {
            // only requested one permission
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation();
            } else {
                Toast.makeText(this, "Location permission has been denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * create a geofence for the activity
     * @param latLng
     * @param radius
     */
    private void addGeofence(LatLng latLng, float radius) {
        //TODO make a more dynamic implementation for id

        // create geofence, request for geofence and the pending intent for activity
        Geofence geofence = createGeofence("1", latLng, radius);
        GeofencingRequest request = getGeofenceRequest(geofence);
        PendingIntent pendingIntent = getPendingIntent();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // this will be a sort of safety check
            // access to fine location should have been validated beforehand
            return;
        }
        geofencingClient.addGeofences(request, pendingIntent)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Geofences added
                        Log.d(TAG, "onSuccess: geofence added successfully");
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to add geofences
                        Log.d(TAG, "onFailure: error on geofence add");
                    }
                });
    }

    /**
     * specify the geofences to monitor and set how geofence events are triggered
     * @param geofence
     * @return geofenceRequest
     */
    private GeofencingRequest getGeofenceRequest(Geofence geofence) {
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
    private Geofence createGeofence(String id, LatLng latLng, float radius) {
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
    private PendingIntent getPendingIntent() {
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

    @Override
    protected void onStop() {
        super.onStop();
        PendingIntent pendingIntent = getPendingIntent();

        geofencingClient.removeGeofences(pendingIntent)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Geofences removed
                        Log.d(TAG, "onSuccess: geofences removed");
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to remove geofences
                        Log.d(TAG, "onFailure: error removing geofences");
                    }
                });
    }

    /**
     * define menu actions
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * add a circle with a given center point and radius to the map
     * we will use this to visualize the geofence
     * @param latLng
     * @param radius
     */
    private void drawCircle(LatLng latLng, float radius) {
        Log.d(TAG, "drawCircle: drawing circle radius: " + radius + " at lat: " + latLng.latitude + " lng: " + latLng.longitude);
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255, 255, 0,0));
        circleOptions.fillColor(Color.argb(64, 255, 0,0));
        circleOptions.strokeWidth(4);
        mMap.addCircle(circleOptions);
    }
}