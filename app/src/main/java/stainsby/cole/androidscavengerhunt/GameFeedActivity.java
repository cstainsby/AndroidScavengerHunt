package stainsby.cole.androidscavengerhunt;

import androidx.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameFeedActivity extends AppCompatActivity {

    private List<ScavengerHuntGame> games;

    private CustomAdapter adapter;

    DAOScavengerHuntGame db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_feed);

        games = new ArrayList<>();

        db = new DAOScavengerHuntGame();

        RecyclerView recyclerView = findViewById(R.id.gameFeedRecyclerView);

        // set up the layout manager for recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // set up custom adapter for the recyclerView
        // implementation below
        adapter = new CustomAdapter();
        recyclerView.setAdapter(adapter);
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
                Toast.makeText(this, "new game created", Toast.LENGTH_SHORT).show();

                Random random = new Random();
                int randomInt = random.nextInt(1000);

                db.writeNewScavengerHuntGame(Integer.toString(randomInt));

                adapter.notifyDataSetChanged();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

        class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

            TextView cardText;

            public CustomViewHolder(@NonNull View itemView) {
                super(itemView);

                // find and store the views text and image view
                cardText = null;

                // connect on click listeners to the view holder
                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }

            public void updateView(ScavengerHuntGame game) {
                // set the updated information to the display
                cardText.setText(game.getTitle());

                this.itemView.setBackgroundResource(R.color.white);
            }

            @Override
            public void onClick(View view) {
            }

            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        }

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