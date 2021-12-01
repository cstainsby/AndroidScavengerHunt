// ---------------------------------------------------------------------------
// DESC:
//   This object will model the ScavengerHunt game,
// PROJECT:
//   Scavenger Hunt project
// SOURCES:
//   N/A
// DATE:
//   11/26/2021
// ---------------------------------------------------------------------------

package stainsby.cole.androidscavengerhunt;

import java.util.List;

public class ScavengerHuntGame {

    // the admin who will be in charge of running/setting up the game
    private GameAdmin admin;

    // the players that will be participating in the game
    private List<GameParticipant> players;

    private int id;

    private String title;


    public ScavengerHuntGame(String title) {
        this.id = -1;
        // TODO build this out, just using title for now, for demo purposes
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
