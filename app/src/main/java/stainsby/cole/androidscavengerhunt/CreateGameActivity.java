package stainsby.cole.androidscavengerhunt;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class CreateGameActivity extends AppCompatActivity {

    private static final String TAG = "createGameAct";

    private EditText titleText;
    private EditText numPlayersText;
    private Button postButton;
    private Button seeMapButton;

    private List<LatLng> scavengerLocations;

    private ActivityResultLauncher<Intent> mapLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

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
                    Integer numPlayers = Integer.parseInt(numPlayersText.getText().toString());
                    // TODO make sure this is an integer

                    // make intent that will be passed back to the game feed
                    Intent intent = new Intent();
                    intent.putExtra("owner", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                    intent.putExtra("title", title);
                    intent.putExtra("numPlayers", numPlayers);

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

                    for (int i = 0; i < scavengerLocations.size(); i++) {
                        LatLng latLngAtI = scavengerLocations.get(i);
                        double[] latLongMarkerCords = { latLngAtI.latitude, latLngAtI.longitude };
                        intent.putExtra("cords_" + i , latLongMarkerCords);
                    }

                    mapLauncher.launch(mapIntent);
                }
            });

            mapLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            // return points that the user has plotted
                            Intent data = result.getData();
                            if(data != null) {
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
                                        scavengerLocations.add(latLng);
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
    }


}