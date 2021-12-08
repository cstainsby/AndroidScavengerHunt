package stainsby.cole.androidscavengerhunt;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseError;

import java.nio.channels.AsynchronousChannel;

public class MainActivity extends AppCompatActivity {

    DAOUser daoUser;
    EditText editTextUserName;
    EditText editTextPassword;
    Button createButton;
    Button toFeedButton;
    private DatabaseReference mDatabase;
    private ChildEventListener childEventListener;
    private FirebaseDatabase db;

    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseDatabase.getInstance();
        String className = User.class.getSimpleName();
        mDatabase = db.getReference(className);

        daoUser = new DAOUser();

        editTextUserName = findViewById(R.id.testFirebaseEntryUsername);
        editTextPassword = findViewById(R.id.testFirebaseEntryPassword);

        createButton = findViewById(R.id.createNewUserButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userName = editTextUserName.getText().toString();
                String password = editTextPassword.getText().toString();
                boolean correctUser = true;
                boolean correctPass = true;


                if( userName.equals("") ) {
                    Toast.makeText(MainActivity.this, "Enter a valid username", Toast.LENGTH_SHORT).show();
                    correctUser = false;
                } else {
                    correctUser = true;
                }

                if( password.equals("") ) {
                    Toast.makeText(MainActivity.this, "Enter a valid password", Toast.LENGTH_SHORT).show();
                    correctPass = false;
                } else {
                    correctPass = true;
                }

                if( correctUser && correctPass ) {
                    daoUser.writeNewUser(userName, password);
                }
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