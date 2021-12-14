// setup android FCM and tokens: https://firebase.google.com/docs/cloud-messaging/android/client

package stainsby.cole.androidscavengerhunt;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Random;

public class GameFeedActivity extends AppCompatActivity {

    private static final String TAG = "GameFeedActivity";

    private List<ScavengerHuntGame> games;
    private CustomAdapter adapter;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseMessagingService messagingService;
    private DatabaseReference mScavengerGameDatabase;
    private ChildEventListener mGameChildEventListener;

    private ActivityResultLauncher<Intent> launcher;


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

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        // load in created game contents
                        // load it into firebase
                        if(result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();

                            String gameTitle = data.getStringExtra("title");
                            Integer numPlayers = data.getIntExtra("numPlayers", 0);

                            // append a hash code to the end of the title as an id to prevent collisions
                            String gameID = gameTitle + "_" + hashCode();

                            // add game items to firebase
                            mScavengerGameDatabase.child(gameID);
                            mScavengerGameDatabase.child(gameID).child("title").setValue(gameTitle);
                            mScavengerGameDatabase.child(gameID).child("numPlayers").setValue(numPlayers);
                            Log.d(TAG, "onActivityResult: add game: " + gameID + " to database");
                            Toast.makeText(GameFeedActivity.this, "Game Posted", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Log.d(TAG, "onActivityResult: error on intent result");
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

                for (DataSnapshot game : snapshot.getChildren()) {
                    String gameID = "";
                    String title = "";
                    String numPlayers = "";
                    try {
                        gameID = game.getValue().toString();
                        title = game.child("title").getValue().toString();
                        numPlayers = game.child("numPlayers").getValue().toString();
                    } catch(NullPointerException e) {
                        e.printStackTrace();
                    }
                    if(!title.equals("") && !numPlayers.equals("")) {
                        Log.d(TAG, "onDataChange: making game object \n    title " + title + "\n    " + numPlayers);
                        games.add(new ScavengerHuntGame(
                                title,
                                Integer.parseInt(numPlayers)
                        ));
                        adapter.notifyItemChanged(i);
                        i++;
                    }
                    else {
                        Log.d(TAG, "onDataChange: error, empty input");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: Error reading from database on change");
            }
        });

        // setup the token for the client app instance
        //
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        String msg = "new token: " + token;
                        Log.d(TAG, msg);
                        Toast.makeText(GameFeedActivity.this, msg, Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(this, createGameActivity.class);

                Log.d(TAG, "onOptionsItemSelected: Going into create activity");
                Toast.makeText(this, "Create game", Toast.LENGTH_SHORT).show();
                launcher.launch(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }



    class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

        class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private TextView titleView;

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
                Log.d(TAG, "updateView: updating with game object title " + game.getTitle());
                String title = game.getTitle();
                titleView.setText(title);
            }

            @Override
            public void onClick(View view) {
                String topic = this.gameID;      // topic will be the game you are joining/leaving
                /*Message.builder()
                        .putData("score", "850")
                        .putData("time", "2:45")
                        .setTopic(topic)
                        .build();*/

                if(!gameJoined) {
                    // subscribe to the id of the game that the view is holding
                    FirebaseMessaging.getInstance().subscribeToTopic("")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    String msg = "Game joined";
                                    if (!task.isSuccessful()) {
                                        msg = "Failed to join game";
                                    }
                                    Log.d(TAG, "onComplete: " + msg);
                                    Toast.makeText(GameFeedActivity.this, msg, Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    String msg = "You have left the game queue";
                                    if (!task.isSuccessful()) {
                                        msg = "Failed to leave game";
                                    }
                                    Log.d(TAG, "onComplete: " + msg);
                                    Toast.makeText(GameFeedActivity.this, msg, Toast.LENGTH_SHORT).show();
                                }
                            });
                }
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