// ---------------------------------------------------------------------------
// DESC:
//   Data Access Object for a user
// PROJECT:
//   Scavenger Hunt project
// SOURCES:
//   https://firebase.google.com/docs/database/android/read-and-write
// DATE:
//   11/28/2021
// ---------------------------------------------------------------------------

package stainsby.cole.androidscavengerhunt;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.atomic.AtomicInteger;

public class DAOUser {
    private DatabaseReference mDatabase;
    private ChildEventListener childEventListener;
    private FirebaseDatabase db;
    private int id;
    final AtomicInteger count = new AtomicInteger();

    public DAOUser() {
        db = FirebaseDatabase.getInstance();

        // get a string referencing the className of the object that is stored in the database
        String className = User.class.getSimpleName();

        mDatabase = db.getReference(className);
    }

    /*
    // create a new user
    public void writeNewUser(String userName, String password, String email) {
        User user = new User(id, userName, password, email);
        mDatabase.child("users").child(userName).setValue(user);
    }
     */

    // create a new user - without email
    public void writeNewUser(String userName, String password) {
        User user = new User(id, userName, password);
        mDatabase.child("users").child(userName).setValue(user);
    }
    /*
    // update an existing user
    public void updateExistingUserEmail(String userName, String email) {
        mDatabase.child("users").child(userName).child("email").setValue(email);
    }
     */

    public void incrementId() {
        id = count.incrementAndGet();
    }

    public void deleteUser(String userName) {
        mDatabase.child("users").child(userName).removeValue();
    }
}
