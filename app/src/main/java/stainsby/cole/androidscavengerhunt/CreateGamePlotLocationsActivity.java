package stainsby.cole.androidscavengerhunt;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import stainsby.cole.androidscavengerhunt.databinding.ActivityCreateGamePlotLocationsBinding;

public class CreateGamePlotLocationsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private ActivityCreateGamePlotLocationsBinding binding;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location lastKnownLocation;
    private List<LatLng> scavengerLocations;

    private Button saveMarkerButton;

    private static final String TAG = "createPlotPointsAct";
    private static final int LOCATION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCreateGamePlotLocationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        scavengerLocations = new ArrayList<>();


        saveMarkerButton = findViewById(R.id.saveMarkerButton);
        saveMarkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();

                Log.d(TAG, "onClick: num locs " + scavengerLocations.size());
                intent.putExtra("numScavLocs", scavengerLocations.size());

                for (int i = 0; i < scavengerLocations.size(); i++) {
                    LatLng latLngAtI = scavengerLocations.get(i);
                    double[] latLongMarkerCords = { latLngAtI.latitude, latLngAtI.longitude };
                    intent.putExtra("cords_" + i , latLongMarkerCords);
                }

                CreateGamePlotLocationsActivity.this.setResult(Activity.RESULT_OK, intent);
                CreateGamePlotLocationsActivity.this.finish();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        enableUserLocation();

        if (ActivityCompat.checkSelfPermission(CreateGamePlotLocationsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(CreateGamePlotLocationsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
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

                        mMap.setOnMapLongClickListener(CreateGamePlotLocationsActivity.this);

                        Log.d(TAG, "onSuccess: saving location locally");
                        lastKnownLocation = location;
                        Log.d(TAG, "onSuccess: test");
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
                // enable userLocation then find their position
                enableUserLocation();
            } else {
                Toast.makeText(this, "Location permission has been denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        Log.d(TAG, "onMapLongClick: longClick at lat: " + latLng.latitude + " long: " + latLng.longitude);
        mMap.addMarker(new MarkerOptions().position(latLng));
        scavengerLocations.add(latLng);
    }

    /** Called when the user clicks a marker. */
    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
        Integer clickCount = (Integer) marker.getTag();

        // Check if a click count was set, then display the click count.
        if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
            Toast.makeText(this,
                    marker.getTitle() +
                            " has been clicked " + clickCount + " times.",
                    Toast.LENGTH_SHORT).show();
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }
}