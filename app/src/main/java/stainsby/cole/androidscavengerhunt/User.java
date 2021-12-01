// ---------------------------------------------------------------------------
// DESC:
//   This class will be the model for a user, it will store all user information
//   and metadata about their game performance
// PROJECT:
//   Scavenger Hunt project
// SOURCES:
//   N/A
// DATE:
//   11/26/2021
// ---------------------------------------------------------------------------

package stainsby.cole.androidscavengerhunt;

public class User {
    // user info
    private int id;
    private String userName;
    private String password;
    private String email;

    // stats related to the user
    // TODO: we can add and remove these depending on what we want to keep track of
    private Integer gamesWon;
    private Integer gamesPlayed;
    private Double gameWinRatio;
    private Integer totalScavLocCaptures;
    private Double scavLocsCapturedPerGame;


    // DVC - blank user
    public User() {
        this.id = -1;
        this.userName = "";
        this.password = "";
        this.email = "";

        this.gamesWon = 0;
        this.gamesPlayed = 0;
        this.gameWinRatio = 0.0;
        this.totalScavLocCaptures = 0;
        this.scavLocsCapturedPerGame = 0.0;
    }

    // EVC - for a user with no provided email
    public User(int id, String userName, String password) {
        this();
        this.id = id;
        this.userName = userName;
        this.password = password;
    }

    // EVC - for a new user with an email
    public User(int id, String userName, String password, String email) {
        this();
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.email = email;
    }

    // EVC - for an existing player
    public User(String userName, String password,
                Integer gamesWon, Integer gamesPlayed, Double gameWinRatio, Integer totalScavLocCaptures, Double scavLocsCapturedPerGame) {
        this.userName = userName;
        this.password = password;

        this.gamesWon = gamesWon;
        this.gamesPlayed = gamesPlayed;
        this.gameWinRatio = gameWinRatio;
        this.totalScavLocCaptures = totalScavLocCaptures;
        this.scavLocsCapturedPerGame = scavLocsCapturedPerGame;
    }

    // TODO getters and setters for the user data
    public Integer getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}
