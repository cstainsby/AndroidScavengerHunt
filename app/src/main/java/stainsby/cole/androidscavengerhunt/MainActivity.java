package stainsby.cole.androidscavengerhunt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.nio.channels.AsynchronousChannel;

public class MainActivity extends AppCompatActivity {

    DAOUser daoUser;
    EditText editTextFirst;
    EditText editTextLast;
    Button createButton;
    Button toFeedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        daoUser = new DAOUser();

        editTextFirst = findViewById(R.id.testFirebaseEntryfirst);
        editTextLast = findViewById(R.id.testFirebaseEntrylast);

        createButton = findViewById(R.id.createNewUserButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstName = editTextFirst.getText().toString();
                String lastName = editTextLast.getText().toString();

                daoUser.writeNewUser("1", firstName, lastName);
            }
        });

        // TODO this implementation should probably be changed
        toFeedButton = findViewById(R.id.toGameFeedButton);
        toFeedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GameFeedActivity.class);

                startActivity(intent);
            }
        });
    }
}