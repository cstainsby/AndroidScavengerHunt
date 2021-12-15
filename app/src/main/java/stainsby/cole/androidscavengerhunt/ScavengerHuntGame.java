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

import java.util.ArrayList;
import java.util.List;

public class ScavengerHuntGame {

    // the admin who will be in charge of running/setting up the game
    private GameAdmin admin;

    // the players that will be participating in the game
    private List<GameParticipant> players;

    private String id;

    private int numPlayers;

    private String title;

    private List<LatLng> scavengerLocations;

    private int numScavengerLocations;

    public ScavengerHuntGame() {
        players = new ArrayList<>();
        scavengerLocations = new ArrayList<>();

        this.id = "N/A";
        this.title = "N/A";
        this.numPlayers = -1;
        this.admin = new GameAdmin("");
        this.numScavengerLocations = 0;
    }


    public ScavengerHuntGame(String id, String title, int numPlayers, int numScavengerLocations, GameAdmin admin, List<LatLng> scavengerLocations) {
        players = new ArrayList<>();
        scavengerLocations = new ArrayList<>();

        this.id = id;
        this.title = title;
        this.numPlayers = numPlayers;
        this.admin = admin;
        this.numScavengerLocations = numScavengerLocations;
        this.scavengerLocations = scavengerLocations;
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

    public String getId() {
        return id;
    }

    public GameAdmin getAdmin() {
        return admin;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    //-------------------------------------------------
    // setters
    //-------------------------------------------------

    public void setId(String id) {
        this.id = id;
    }

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


    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    public void addScavengerLocation(LatLng latLng) {
        this.scavengerLocations.add(latLng);
    }

    public void setNumScavengerLocations(int numScavengerLocations) {
        this.numScavengerLocations = numScavengerLocations;
    }
}