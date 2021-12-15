package stainsby.cole.androidscavengerhunt;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CreateGameActivity extends AppCompatActivity {

    private static final String TAG = "createGameAct";

    private EditText titleText;
    private EditText numPlayersText;
    private Button postButton;
    private Button seeMapButton;
    private Button randomPoints;

    private CustomAdapter adapter;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private List<LatLng> scavengerLocations;
    private Location lastKnownLocation;

    private ActivityResultLauncher<Intent> mapLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

        RecyclerView recyclerView = findViewById(R.id.markerRecyclerView);

        // set up the layout manager for recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // set up custom adapter for the recyclerView
        // implementation below
        adapter = new CustomAdapter();
        recyclerView.setAdapter(adapter);

        Intent intent = getIntent();

        if(intent != null) {
            Log.d(TAG, "onCreate: creating create activity");

            titleText = findViewById(R.id.createGameTitleEditText);
            numPlayersText = findViewById(R.id.createGameNumPlayersEditText);

            scavengerLocations = new ArrayList<>();

            postButton = findViewById(R.id.createGameButton);
            postButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String title = titleText.getText().toString();
                    String numPlayersStr = numPlayersText.getText().toString();

                    Integer numPlayers;
                    if(!numPlayersStr.equals("")) {
                         numPlayers = Integer.parseInt(numPlayersStr);
                    }
                    else {
                        numPlayers = 0;
                    }
                    // TODO make sure this is an integer

                    // make intent that will be passed back to the game feed
                    Intent intent = new Intent();
                    intent.putExtra("owner", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                    intent.putExtra("title", title);
                    intent.putExtra("numPlayers", numPlayers);
                    intent.putExtra("numScavLocs", scavengerLocations.size());

                    // convert all all scav locs to double arrays to be passed back
                    // and reinterpreted
                    for (int i = 0; i < scavengerLocations.size(); i++) {
                        LatLng latLngAtI = scavengerLocations.get(i);
                        double[] latLongMarkerCords = { latLngAtI.latitude, latLngAtI.longitude };
                        intent.putExtra("cords_" + i , latLongMarkerCords);
                    }

                    Log.d(TAG, "onClick: pass back " + title + " " + numPlayers);

                    CreateGameActivity.this.setResult(Activity.RESULT_OK, intent);
                    CreateGameActivity.this.finish();
                }
            });

            seeMapButton = findViewById(R.id.addScavLocMapButton);
            seeMapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent mapIntent = new Intent(CreateGameActivity.this, CreateGamePlotLocationsActivity.class);

                    mapIntent.putExtra("numScavLocs", scavengerLocations.size());
                    Log.d(TAG, "onClick: " + scavengerLocations.size());

                    for (int i = 0; i < scavengerLocations.size(); i++) {
                        LatLng latLngAtI = scavengerLocations.get(i);
                        double[] latLongMarkerCords = { latLngAtI.latitude, latLngAtI.longitude };
                        mapIntent.putExtra("cords_" + i , latLongMarkerCords);

                        Log.d(TAG, "onClick: put " + latLongMarkerCords.toString() + "into intent");
                    }

                    mapLauncher.launch(mapIntent);
                }
            });

            randomPoints = findViewById(R.id.randomMarkers);
            randomPoints.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Random random = new Random();
                    // 1 degree of latitude and longitude roughly 111 kilometers

                    // generate six random points
                    for (int i = 0; i < 6; i++) {
                        int degrees = i * 60;
                        double x_angle = Math.cos(degrees);
                        double y_angle = Math.sin(degrees);

                        double x_displace = x_angle * random.nextInt(5)/111;
                        double y_displace = y_angle * random.nextInt(5)/111;
                        //
                        random.nextInt();
                        double latLoc = lastKnownLocation.getLatitude();
                        double longLoc = lastKnownLocation.getLongitude();

                        LatLng newLocation = new LatLng(x_displace + latLoc, y_displace + longLoc);
                        scavengerLocations.add(newLocation);
                    }
                    adapter.notifyDataSetChanged();
                }
            });

            mapLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            // return points that the user has plotted
                            Intent data = result.getData();
                            if(data != null) {
                                Log.d(TAG, "onActivityResult: returning data from map");
                                Integer numScavLocations = data.getIntExtra("numScavLocs", 0);

                                scavengerLocations.clear();

                                // get marker points from map view
                                for (int i = 0; i < numScavLocations; i++) {
                                    double[] latLngDoubleArr = null;
                                    try {
                                        latLngDoubleArr = data.getDoubleArrayExtra("cords_" + i);
                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                    }
                                    if (latLngDoubleArr != null) {
                                        LatLng latLng = new LatLng(latLngDoubleArr[0], latLngDoubleArr[1]);
                                        Log.d(TAG, "onActivityResult: adding " + latLng + " to scav loc list");
                                        scavengerLocations.add(latLng);
                                        adapter.notifyItemChanged(scavengerLocations.size());
                                    }
                                    else {
                                        Log.d(TAG, "onActivityResult: error loading latLong at" + i);
                                    }
                                }
                            }
                        }
                    });
        }
        else {
            Log.d(TAG, "onCreate: intent failed");
            Toast.makeText(this, "Error loading page", Toast.LENGTH_SHORT).show();
            // TODO maybe make a quick error page?
        }

        if (ActivityCompat.checkSelfPermission(CreateGameActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(CreateGameActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Task<Location> locationTask = mFusedLocationProviderClient.getLastLocation();
            locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {

                        Log.d(TAG, "onSuccess: saving location locally");
                        lastKnownLocation = location;
                        Log.d(TAG, "onSuccess: test");
                    }
                }
            });
        }
        else {
            Toast.makeText(this, "Error Loading your location", Toast.LENGTH_SHORT).show();
        }
    }

    class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

        class CustomViewHolder extends RecyclerView.ViewHolder {

            private TextView markerNum;
            private TextView latAndLong;

            public CustomViewHolder(@NonNull View itemView) {
                super(itemView);

                // find and store the views text and image view
                markerNum = itemView.findViewById(R.id.markerNum);
                latAndLong = itemView.findViewById(R.id.markerLatAndLong);
            }

            public void updateView(LatLng latLng, int position) {
                // set the updated information to the display
                Log.d(TAG, "updateView: update");

                double latitude = latLng.latitude;
                double longitude = latLng.longitude;

                String latAndLongStr = "Latitude: " + latitude + "\nLongitude " + longitude;
                latAndLong.setText(latAndLongStr);

                int displayNum = position + 1;
                markerNum.setText("Scavenger Location: " + displayNum);
            }
        }
        //---------------------------------------------
        // inherited functions from Custom adapter
        //---------------------------------------------
        @NonNull
        @Override
        public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(CreateGameActivity.this)
                    .inflate(R.layout.scav_loc_card_view, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
            // get a video from the list at position an update the view with it
            LatLng latLng = scavengerLocations.get(position);

            Log.d(TAG, "onBindViewHolder: updating with " + latLng);
            holder.updateView(latLng, position);
        }

        @Override
        public int getItemCount() {
            return scavengerLocations.size();
        }
    }
}