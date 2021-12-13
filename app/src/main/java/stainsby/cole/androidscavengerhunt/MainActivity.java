package stainsby.cole.androidscavengerhunt;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityResultLauncher<Intent> launcher;

    DAOUser daoUser;
    EditText editTextUserName;
    EditText editTextPassword;
    Button createButton;
    Button toFeedButton;
    private DatabaseReference mDatabase;
    private ChildEventListener childEventListener;
    private FirebaseDatabase db;

    String userName = "Anonymous";
    List<ChatMessage> chatMessageList;

    FirebaseDatabase mFirebaseDatabase;

    DatabaseReference mMessagesDatabaseReference;
    ChildEventListener mMessagesChildEventListener;

    // firebase authentication fields
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

        chatMessageList = new ArrayList<>();

        //old sign in method-----------------------------------------
/*        createButton = findViewById(R.id.createNewUserButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userName = editTextUserName.getText().toString();
                String password = editTextPassword.getText().toString();
                boolean correctUser = true;
                boolean correctPass = true;


                if (userName.equals("")) {
                    Toast.makeText(MainActivity.this, "Enter a valid username", Toast.LENGTH_SHORT).show();
                    correctUser = false;
                } else {
                    correctUser = true;
                }

                if (password.equals("")) {
                    Toast.makeText(MainActivity.this, "Enter a valid password", Toast.LENGTH_SHORT).show();
                    correctPass = false;
                } else {
                    correctPass = true;
                }

                if (correctUser && correctPass) {
                    daoUser.writeNewUser(userName, password);
                }
            }
        });*/
        //-----------------------------------------------------------

        // TODO this implementation should probably be changed
        toFeedButton = findViewById(R.id.toGameFeedButton);
        toFeedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GameFeedActivity.class);

                startActivity(intent);
            }
        });

        setupFirebase();
    }

    private void setupFirebase() {
        // initialize the firebase references
        FirebaseApp.initializeApp(this);
        mFirebaseDatabase =
                FirebaseDatabase.getInstance();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // we have two auth states, signed in and signed out
                // get the get current user, if there is one
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // user is signed in
                    // step 4
                    setupUserSignedIn(user);
                } else {
                    // user is signed out
                    // step 5
                    // we need an intent
                    // the firebaseUI Github repo README.md
                    // we have used builders before in this class
                    // AlertDialog.Builder
                    // return instance to support chaining
                    Intent intent = AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setAvailableProviders(
                                    Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build()
                                    )
                            ).build();
                    launcher.launch(intent);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        // attach the authstatelistener
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // remove it
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        chatMessageList.clear();
    }

    private void setupUserSignedIn(FirebaseUser user) {
        // get the user's name
        userName = user.getDisplayName();
        // listen for database changes with childeventlistener
        // wire it up!
        mMessagesDatabaseReference
                .addChildEventListener(mMessagesChildEventListener);
    }

    public void onSendButtonClick(View view) {
        // show a log message
        Log.d(TAG, "onSendButtonClick: ");
        // push up to "messages" whatever is
        // in the edittext
        EditText editText = (EditText)
                findViewById(R.id.testFirebaseEntryUsername);
        String currText = editText.getText().toString();
        if (currText.isEmpty()) {
            Toast.makeText(this, "Please enter a message first", Toast.LENGTH_SHORT).show();
        } else {
            // we have a message to send
            // create a ChatMessage object to push
            // to the database
            ChatMessage chatMessage = new
                    ChatMessage(userName,
                    currText);
            mMessagesDatabaseReference
                    .push()
                    .setValue(chatMessage);
            // warmup task #1
            editText.setText("");
        }
    }
}