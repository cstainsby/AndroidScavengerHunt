package stainsby.cole.androidscavengerhunt;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class DAOScavengerHuntGame {
    private DatabaseReference mDatabase;

    public DAOScavengerHuntGame() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        // get a string referencing the className of the object that is stored in the database
        String className = ScavengerHuntGame.class.getSimpleName();

        mDatabase = db.getReference(className);
    }

    // create a new user
    public void writeNewScavengerHuntGame(String title) {
        ScavengerHuntGame game = new ScavengerHuntGame(title);
        mDatabase.child("game").child(title).setValue(game);
    }


    public void deleteUser(String userId) {
        mDatabase.child("game").child(userId).removeValue();
    }

    public int getDatabaseSize() {
        return 0;
    }
}
