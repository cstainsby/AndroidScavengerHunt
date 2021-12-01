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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DAOUser {
    private DatabaseReference mDatabase;
    private int id = -1;

    public DAOUser() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        // get a string referencing the className of the object that is stored in the database
        String className = User.class.getSimpleName();

        mDatabase = db.getReference(className);
    }

    // create a new user
    public void writeNewUser(String userName, String password, String email) {
        int id = getNextId();
        User user = new User(id, userName, password, email);
        mDatabase.child("users").child(Integer.toString(id)).setValue(user);
    }

    // create a new user - without email
    public void writeNewUser(String userName, String password) {
        int id = getNextId();
        User user = new User(id, userName, password);
        mDatabase.child("users").child(Integer.toString(id)).setValue(user);
    }

    // update an existing user
    public void updateExistingUserEmail(String userId, String email) {
        mDatabase.child("users").child(userId).child("email").setValue(email);
    }

    public void deleteUser(String userId) {
        mDatabase.child("users").child(userId).removeValue();
    }

    private int getNextId() {
        id++;
        return id;
    }
}
