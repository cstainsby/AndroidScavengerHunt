package stainsby.cole.androidscavengerhunt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class createGameActivity extends AppCompatActivity {

    private EditText titleText;
    private EditText numPlayersText;
    private Button postButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

        titleText = findViewById(R.id.createGameTitleEditText);
        numPlayersText = findViewById(R.id.createGameNumPlayersEditText);

        postButton = findViewById(R.id.createGameButton);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = titleText.getText().toString();
                Integer numPlayers = Integer.valueOf(numPlayersText.getText().toString());
                // TODO make sure this is an integer

                Intent intent = getIntent();
                intent.putExtra("title", title);
                intent.putExtra("numPlayers", numPlayers);

                createGameActivity.this.finish();
            }
        });
    }
}