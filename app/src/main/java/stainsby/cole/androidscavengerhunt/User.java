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
    private String firstName;
    private String lastName;
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
        this.firstName = "";
        this.lastName = "";
        this.email = "";

        this.gamesWon = 0;
        this.gamesPlayed = 0;
        this.gameWinRatio = 0.0;
        this.totalScavLocCaptures = 0;
        this.scavLocsCapturedPerGame = 0.0;
    }

    // EVC - for a user with no provided email
    public User(String firstName, String lastName) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // EVC - for a new user with an email
    public User(String firstName, String lastName, String email) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    // EVC - for an existing player
    public User(String firstName, String lastName,
                Integer gamesWon, Integer gamesPlayed, Double gameWinRatio, Integer totalScavLocCaptures, Double scavLocsCapturedPerGame) {
        this.id = -1;
        this.firstName = firstName;
        this.lastName = lastName;

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

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
