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

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class ScavengerHuntGame {

    // the admin who will be in charge of running/setting up the game
    private GameAdmin admin;

    // the players that will be participating in the game
    private List<GameParticipant> players;

    private int id;

    private int lobbySize;

    private String title;

    private List<LatLng> scavengerLocations;

    private List<ChatMessage> messageHistory;


    public ScavengerHuntGame(String title, int lobbySize) {
        this.id = -1;
        // TODO build this out, just using title for now, for demo purposes
        this.title = title;
        this.lobbySize = lobbySize;
    }

    //-------------------------------------------------
    // getters
    //-------------------------------------------------
    public String getTitle() {
        return title;
    }

    public List<LatLng> getScavengerLocations() {
        return scavengerLocations;
    }

    //-------------------------------------------------
    // setters
    //-------------------------------------------------

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAdmin(GameAdmin admin) {
        this.admin = admin;
    }

    public void setPlayers(List<GameParticipant> players) {
        this.players = players;
    }

    public void setScavengerLocations(List<LatLng> scavengerLocations) {
        this.scavengerLocations = scavengerLocations;
    }

    public void addMessage(ChatMessage message) {

    }
}