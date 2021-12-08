package stainsby.cole.androidscavengerhunt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Random;

public class GameFeedActivity extends AppCompatActivity {

    private List<ScavengerHuntGame> games;
    private CustomAdapter adapter;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mScavengerGameDatabase;
    private ChildEventListener mGameChildEventListener;


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

        setupFirebase();
    }

    private void setupFirebase() {
        FirebaseApp.initializeApp(GameFeedActivity.this);
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();

        // get a string referencing the className of the object (pathname)
        String pathName = ScavengerHuntGame.class.getSimpleName();

        mScavengerGameDatabase = mFirebaseDatabase.getReference().child(pathName);


        mGameChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ScavengerHuntGame game = snapshot.getValue(ScavengerHuntGame.class);
                games.add(game);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
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
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

        class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView titleView;

            public CustomViewHolder(@NonNull View itemView) {
                super(itemView);

                // find and store the views text and image view
                titleView = findViewById(R.id.gameTitleCardView);

                // connect on click listeners to the view holder
                itemView.setOnClickListener(this);
            }

            public void updateView(ScavengerHuntGame game) {
                // set the updated information to the display
                titleView.setText(game.getTitle());
            }

            @Override
            public void onClick(View view) {
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
            holder.updateView(video);
        }

        @Override
        public int getItemCount() {
            return games.size();
        }
    }
}