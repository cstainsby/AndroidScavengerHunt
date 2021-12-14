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

public class CreateGameActivity extends AppCompatActivity {

    private static final String TAG = "createGameAct";

    private EditText titleText;
    private EditText numPlayersText;
    private Button postButton;
    private Button seeMapButton;

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

            postButton = findViewById(R.id.createGameButton);
            postButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String title = titleText.getText().toString();
                    Integer numPlayers = Integer.parseInt(numPlayersText.getText().toString());
                    // TODO make sure this is an integer

                    // make intent that will be passed back to the game feed
                    Intent intent = new Intent();
                    intent.putExtra("title", title);
                    intent.putExtra("numPlayers", numPlayers);

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
                    mapLauncher.launch(mapIntent);
                }
            });

            mapLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            // return points that the user has plotted
                            Intent data = result.getData();
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