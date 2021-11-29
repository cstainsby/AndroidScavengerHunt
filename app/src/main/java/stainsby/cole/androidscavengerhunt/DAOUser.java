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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DAOUser {
    private DatabaseReference mDatabase;

    public DAOUser() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        // get a string referencing the className of the object that is stored in the database
        String className = User.class.getSimpleName();

        mDatabase = db.getReference(className);
    }

    // create a new user
    public void writeNewUser(String userId, String firstName, String lastName, String email) {
        User user = new User(firstName, lastName, email);
        mDatabase.child("users").child(userId).setValue(user);
    }

    // create a new user - without email
    public void writeNewUser(String userId, String firstName, String lastName) {
        User user = new User(firstName, lastName);
        mDatabase.child("users").child(userId).setValue(user);
    }

    // update an existing user
    public void updateExistingUserEmail(String userId, String email) {
        mDatabase.child("users").child(userId).child("email").setValue(email);
    }

    public void deleteUser(String userId) {
        mDatabase.child("users").child(userId).removeValue();
    }
}
