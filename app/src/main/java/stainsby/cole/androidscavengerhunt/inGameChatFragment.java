package stainsby.cole.androidscavengerhunt;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link inGameChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class inGameChatFragment extends Fragment {
    static final String TAG = "InGameMessage";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    String userName;

    List<ChatMessage> messageHistory;
    DatabaseReference mMessagesDatabaseReference;
    ChildEventListener mMessagesChildEventListener;

    FirebaseDatabase mFirebaseDatabase;
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;

    CustomAdapter adapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    RecyclerView recyclerView;

    public inGameChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ingame_chat.
     */
    // TODO: Rename and change types and number of parameters
    public static inGameChatFragment newInstance(String param1, String param2) {
        inGameChatFragment fragment = new inGameChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        messageHistory = new ArrayList<>();

        setupFirebase();
        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        setupUserSignedIn(user);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_ingame_chat, container, false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(inGameChatFragment.this.getContext());
        layoutManager.setStackFromEnd(true);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(inGameChatFragment.this.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        adapter = new CustomAdapter();
        recyclerView.setAdapter(adapter);

        //EditText message = getView().findViewById(R.id.EnterMessage);
        Button send = (Button) view.findViewById(R.id.SendButton);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSendButtonClick(send);
                Log.d(TAG,"-----------------------------------------------------------");
                //ChatMessage m = new ChatMessage(userName,message.toString());
                //messageHistory.add(m);
            }
        });

        return inflater.inflate(R.layout.fragment_ingame_chat, container, false);
    }

    private void setupFirebase() {
        // initialize the firebase references
        FirebaseApp.initializeApp(inGameChatFragment.this.getContext());
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
                messageHistory.add(chatMessage);
                adapter.notifyDataSetChanged();
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
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        messageHistory.clear();
    }

    private void setupUserSignedIn(FirebaseUser user) {
        // get the user's name

        userName = user.getDisplayName();
        // listen for database changes with childeventlistener
        // wire it up!
        mMessagesDatabaseReference
                .addChildEventListener(mMessagesChildEventListener);
    }

    //TODO hook up messaging service to firebase
    //maybe not enough time
    public void onSendButtonClick(View view) {
        // show a log message
        Log.d(TAG, "onSendButtonClick: ");
        // push up to "messages" whatever is
        // in the edittext
        EditText editText = (EditText) view.findViewById(R.id.EnterMessage);
        String currText = editText.getText().toString();
        if (currText.isEmpty()) {
            Toast.makeText(inGameChatFragment.this.getContext(), "Please enter a message first", Toast.LENGTH_SHORT).show();
        }
        else {
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

            //TODO add message to local list and to database

        }
    }

    class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {
        class CustomViewHolder extends RecyclerView.ViewHolder {
            TextView text1;

            public CustomViewHolder(@NonNull View itemView) {
                super(itemView);
                text1 = itemView.findViewById(android.R.id.text1);
            }

            public void updateView(ChatMessage c) {
                text1.setText(c.toString());
            }
        }

        @NonNull
        @Override
        public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(inGameChatFragment.this.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
            holder.updateView(messageHistory.get(position));
        }

        @Override
        public int getItemCount() {
            return messageHistory.size();
        }
    }
}