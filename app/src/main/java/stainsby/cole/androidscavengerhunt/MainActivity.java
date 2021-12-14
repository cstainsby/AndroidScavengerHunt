package stainsby.cole.androidscavengerhunt;

import static android.content.ContentValues.TAG;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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
import com.google.firebase.auth.UserProfileChangeRequest;
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

    User currentUser;
    String currentUID;

    DAOUser daoUser;
    EditText editTextUserName;
    EditText editTextPassword;
    Button createButton;
    Button toFeedButton;
    Button toTestGameButton;

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

        chatMessageList = new ArrayList<>();

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Toast.makeText(MainActivity.this, "You are now signed in", Toast.LENGTH_SHORT).show();
                    }
                    else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        // they backed out of the sign in activity
                        // let's exit
                        finish();
                    }
                }
            });

        //TODO make username request only display once
        editTextUserName = findViewById(R.id.testFirebaseEntryUsername);
        createButton = findViewById(R.id.createNewUserButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( !editTextUserName.getText().toString().equals("") )
                userName = editTextUserName.getText().toString();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                user.getUid();

                currentUser = new User(currentUID, userName);
                mDatabase.child("users").child(currentUID).setValue(currentUser);

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(userName).build();

                user.updateProfile(profileUpdates);

                toFeedButton.setVisibility(1);
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

        setupFirebase();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private void setupFirebase() {
        // initialize the firebase references
        FirebaseApp.initializeApp(this);
        mFirebaseDatabase =
                FirebaseDatabase.getInstance();

        mMessagesDatabaseReference =
                mFirebaseDatabase.getReference()
                        .child("messages");
        mMessagesChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // called for each message already in our db
                // called for each new message add to our db
                // dataSnapshot stores the ChatMessage
                Log.d(TAG, "onChildAdded: " + s);
                ChatMessage chatMessage =
                        dataSnapshot.getValue(ChatMessage.class);
                // add it to our list and notify our adapter
                chatMessageList.add(chatMessage);
                //adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // we have two auth states, signed in and signed out
                // get the get current user, if there is one
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    if( !user.getDisplayName().equals("") ) {
                        createButton.setText("Reset Username");
                    } else {
                       toFeedButton.setVisibility(0);
                    }
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
        Log.d(TAG, user.getDisplayName());

        // listen for database changes with childeventlistener
        // wire it up!
        mMessagesDatabaseReference.addChildEventListener(mMessagesChildEventListener);
    }
}