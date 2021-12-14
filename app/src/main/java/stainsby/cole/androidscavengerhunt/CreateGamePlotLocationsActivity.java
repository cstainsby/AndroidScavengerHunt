package stainsby.cole.androidscavengerhunt;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

import stainsby.cole.androidscavengerhunt.databinding.ActivityCreateGamePlotLocationsBinding;

public class CreateGamePlotLocationsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener{

    private GoogleMap mMap;
    private ActivityCreateGamePlotLocationsBinding binding;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location lastKnownLocation;
    private List<LatLng> scavengerLocations;

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
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
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

                        lastKnownLocation = location;
                    }
                }
            });
        }
        else {
            Log.d(TAG, "onMapReady: error getting location");
        }

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
    public void onMapClick(@NonNull LatLng latLng) {
        mMap.addMarker(new MarkerOptions().position(latLng).title(""));
        scavengerLocations.add(latLng);
    }
}