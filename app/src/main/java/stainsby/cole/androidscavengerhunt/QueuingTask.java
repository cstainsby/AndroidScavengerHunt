// ---------------------------------------------------------------------------
// DESC:
//   This object will queue the player for a game
// PROJECT:
//   Scavenger Hunt project
// SOURCES:
//   N/A
// DATE:
//   11/26/2021
// ---------------------------------------------------------------------------


/*  THE CLIQUE GAME QUEUE ALGORITHM
This task will upload the user into a large game queue in a region, for now we'll just do Spokane
We will want the players playing the game to be close to each other so you don't have to travel miles to get to
    a scavenger location
A graph will be made with all players positions and cliques will be found among the queued players
Once a clique is found with the correct amount of players for each role, remove them from the queue and add them
    to a game
A timer should be started when the task starts, the longer the task ages, the higher priority it should
    have getting into a game
Distance to other people should always be considered, don't want to sacrifice quality of game to much
If one player is miles away from other players and has been waiting for a long time, they shouldn't get
    a game(the distance is too large). Putting players in regions should hopefully limit this issue.


if you invite a friend, we will go off the assumption that they are close to you
    don't allow friends to be a given distance from each other one will be the "party leader"
    and will decide which games to join


once a clique is established, it might be larger than what the game needs. Do an A* hueristic search
    to find the "best game". This search would take into account
    1) friends
    2) whether a role has been filled
    3) distance
    4)

TODO maybe because the admin is setting up the game we can center the game around him

TODO if you aren't in range of any admins hosting a game, show a recycler view of where they are,
 distance to them, and a button to join it when available


TODO: give players waiting in queue recommendation of where to move to improve chance of game connection
    this might mean move toward player center of mass, maybe find a close landmark to allow for easier
    search, possibly provide a google maps route to this landmark
 */
package stainsby.cole.androidscavengerhunt;

import android.os.AsyncTask;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// TODO location might need to be updated as the player waits
// AsyncTask takes
//  1) param - user id
//  2) progress - number of players found for game (how many left to find)
//  3) result - a key to the game JSON object created in firebase
public class QueuingTask extends AsyncTask<Integer, Integer, String> {
    private DatabaseReference mDatabase;

    // setup database for
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        FirebaseDatabase db = FirebaseDatabase.getInstance();

        // get a string referencing the className of the object that is stored in the database
        String className = User.class.getSimpleName();

        mDatabase = db.getReference(className);
    }

    //
    @Override
    protected String doInBackground(Integer... integers) {
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
