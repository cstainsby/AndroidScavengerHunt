// setup android FCM and tokens: https://firebase.google.com/docs/cloud-messaging/android/client

package stainsby.cole.androidscavengerhunt;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GameFeedActivity extends AppCompatActivity {

    private static final String TAG = "GameFeedActivity";

    private List<ScavengerHuntGame> games;
    private CustomAdapter adapter;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseMessagingService messagingService;
    private DatabaseReference mScavengerGameDatabase;

    private ActivityResultLauncher<Intent> createGameLauncher;
    private ActivityResultLauncher<Intent> startGameLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_feed);

        RecyclerView recyclerView = findViewById(R.id.gameFeedRecyclerView);

        // set up the layout manager for recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // set up custom adapter for the recyclerView
        // implementation below
        adapter = new CustomAdapter();
        recyclerView.setAdapter(adapter);

        games = new ArrayList<>();

        createGameLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        // load in created game contents
                        // load it into firebase
                        if(result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();

                            ScavengerHuntGame game = new ScavengerHuntGame();

                            String gameTitle = data.getStringExtra("title");
                            Integer numPlayers = data.getIntExtra("numPlayers", 0);
                            Integer numScavLocations = data.getIntExtra("numScavLocs", 0);

                            // append a hash code to the end of the title as an id to prevent collisions
                            String gameID = gameTitle + "_" + hashCode();

                            String currentUserDisplayName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();


                            // get marker points from map view
                            for (int i = 0; i < numScavLocations; i++) {
                                double[] latLngDoubleArr = null;
                                try {
                                    latLngDoubleArr = data.getDoubleArrayExtra("cords_" + i);
                                }catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                                if(latLngDoubleArr != null) {
                                    LatLng latLng = new LatLng(latLngDoubleArr[0], latLngDoubleArr[1]);
                                    game.addScavengerLocation(latLng);
                                }
                            }

                            // add game items to object then push to firebase
                            game.setId(gameID);
                            game.setTitle(gameTitle);
                            game.setNumPlayers(numPlayers);
                            GameAdmin admin = new GameAdmin(currentUserDisplayName);
                            game.setAdmin(admin);


                            mScavengerGameDatabase.child(gameID).child("title").setValue(gameTitle);
                            mScavengerGameDatabase.child(gameID).child("numPlayers").setValue(numPlayers);
                            mScavengerGameDatabase.child(gameID).child("Owner").setValue(currentUserDisplayName);
                            mScavengerGameDatabase.child(gameID).child("numScavLocations").setValue(numScavLocations);

                            // add all scav location coordinates
                            Log.d(TAG, "onActivityResult: size of locations " + game.getScavengerLocations().size());
                            List<LatLng> cords = game.getScavengerLocations();
                            for (int i = 0; i < numScavLocations; i++) {
                                mScavengerGameDatabase.child(gameID).child("cords_" + i)
                                        .setValue(cords.get(i).latitude + " " + cords.get(i).longitude);
                            }

                            Log.d(TAG, "onActivityResult: add game: " + gameID + " to database");
                            Toast.makeText(GameFeedActivity.this, "Game Posted", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Log.d(TAG, "onActivityResult: error on intent result");
                        }
                    }
                });
        startGameLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        // get game results
                        if(result.getResultCode() == Activity.RESULT_OK) {
                            Log.d(TAG, "onActivityResult: game over");
                            Toast.makeText(GameFeedActivity.this, "Game Over", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        setupFirebase();
    }

    private void setupFirebase() {
        FirebaseApp.initializeApp(GameFeedActivity.this);
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();

        messagingService = new FirebaseMessagingService(this);

        // get a string referencing the className of the object (pathname)
        String pathName = ScavengerHuntGame.class.getSimpleName();

        mScavengerGameDatabase = mFirebaseDatabase.getReference().child(pathName);
        mScavengerGameDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                games.clear();

                // TODO color needs to be set based on if user is in the game or not
                //  I need to store a list of users currently in the game queue as a list
                //  this will also allow me to see how many players are registered to play the game
                //  Authentication needed

                for (DataSnapshot gameSnapshot : snapshot.getChildren()) {

                    ScavengerHuntGame game = new ScavengerHuntGame();

                    //String gameID = gameSnapshot.getValue(String.class);
                    String title = gameSnapshot.child("title").getValue(String.class);
                    Integer numPlayers = gameSnapshot.child("numPlayers").getValue(Integer.class);
                    String Owner = gameSnapshot.child("Owner").getValue(String.class);
                    Integer numScavLocations = gameSnapshot.child("numScavLocations").getValue(Integer.class);

                    Log.d(TAG, "onDataChange: " + title + " " + numPlayers + " " + Owner + " " + numScavLocations);


                    /*if(numScavLocationsStr != null && numPlayersStr != null) {
                        numScavLocations = Integer.parseInt(numScavLocationsStr);
                        numPlayers = Integer.parseInt(numPlayersStr);
                    }*/


                    if(numScavLocations != null) {
                        for (int j = 0; j < numScavLocations; j++) {
                            String cords = gameSnapshot.child("cords_" + j).getValue(String.class);
                            Log.d(TAG, "onDataChange: cords: " + cords);
                            String[] split_cords = cords.split("\\s+");

                            game.addScavengerLocation(new LatLng(Double.parseDouble(split_cords[0]), Double.parseDouble(split_cords[1])));
                        }

                        //game.setId(gameID);
                        game.setTitle(title);
                        game.setNumPlayers(numPlayers);
                        game.setAdmin(new GameAdmin(Owner));
                        game.setNumScavengerLocations(numScavLocations);
                        Log.d(TAG, "onDataChange: " + title + " " + Owner + " " + numPlayers);

                        games.add(game);

                        adapter.notifyItemChanged(i);
                        i++;
                    }
                    else {
                        Toast.makeText(GameFeedActivity.this, "error loading data", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: Error reading from database on change");
            }
        });
    }

    // inflate the menu
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.game_feed_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    // handle clicks on the items added to the menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        switch(itemId) {
            case R.id.addMenuItem:
                Intent intent = new Intent(this, CreateGameActivity.class);

                Log.d(TAG, "onOptionsItemSelected: Going into create activity");
                createGameLauncher.launch(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }



    class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

        class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private TextView titleView;
            private TextView gameOwnerView;

            // this will be what identifies a game in the database
            // to keep it unique we will structure the name like:
            //      "game title"_hash
            private String gameID;

            // user specific items
            boolean gameJoined;

            public CustomViewHolder(@NonNull View itemView) {
                super(itemView);

                // find and store the views text and image view
                titleView = itemView.findViewById(R.id.gameTitleCardView);
                gameOwnerView = itemView.findViewById(R.id.gameOwnerText);

                // connect on click listeners to the view holder
                itemView.setOnClickListener(this);

                // TODO games that you have joined should persist
                //  if you load in a game that is still available to join
                //  that you have previously joined, your user account should remember that
                // when creating the game we will initially set
                // TODO: 12/13/2021 in reality this should be a check to the user account (is this in your queue of joined games)
                gameJoined = false;
            }

            public void updateView(ScavengerHuntGame game) {
                // set the updated information to the display
                Log.d(TAG, "updateView: updating with game object title " + game.getTitle() + " by " + game.getAdmin().displayName);
                String title = game.getTitle();
                String owner = game.getAdmin().displayName;

                titleView.setText(title);
                gameOwnerView.setText("By: ".concat(owner));
            }

            @Override
            public void onClick(View view) {
                String topic = this.gameID;      // topic will be the game you are joining/leaving

                Intent newGameIntent = new Intent(GameFeedActivity.this, ScavengerHuntActivity.class);

                int gameSelectedPosition = getAdapterPosition();
                ScavengerHuntGame gameSelected = games.get(gameSelectedPosition);

                newGameIntent.putExtra("title", gameSelected.getTitle());
                newGameIntent.putExtra("numScavLocs", gameSelected.getScavengerLocations().size());

                for (int i = 0; i < gameSelected.getScavengerLocations().size(); i++) {
                    LatLng latLngAtI = gameSelected.getScavengerLocations().get(i);
                    double[] latLongMarkerCords = { latLngAtI.latitude, latLngAtI.longitude };
                    newGameIntent.putExtra("cords_" + i , latLongMarkerCords);

                    Log.d(TAG, "onClick: put " + latLongMarkerCords.toString() + "into intent");
                }

                startGameLauncher.launch(newGameIntent);
            }
        }
        //---------------------------------------------
        // inherited functions from Custom adapter
        //---------------------------------------------
        @NonNull
        @Override
        public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(GameFeedActivity.this)
                    .inflate(R.layout.game_activity_card_view, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
            // get a video from the list at position an update the view with it
            ScavengerHuntGame video = games.get(position);
            Log.d(TAG, "onBindViewHolder: update at position " + position);
            holder.updateView(video);
        }

        @Override
        public int getItemCount() {
            return games.size();
        }
    }
}