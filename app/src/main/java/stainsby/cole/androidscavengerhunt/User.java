//

package stainsby.cole.androidscavengerhunt;

public class User {
    // user info
    private final String firstName;
    private final String lastName;

    // stats related to the user
    // TODO: we can add and remove these depending on what we want to keep track of
    private Integer gamesWon;
    private Integer gamesPlayed;
    private Double gameWinRatio;
    private Integer totalScavLocCaptures;
    private Double scavLocsCapturedPerGame;


    // create a new player, with new stats
    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;

        this.gamesWon = 0;
        this.gamesPlayed = 0;
        this.gameWinRatio = 0.0;
        this.totalScavLocCaptures = 0;
        this.scavLocsCapturedPerGame = 0.0;
    }

    // load in an old player with
    public User(String firstName, String lastName,
                Integer gamesWon, Integer gamesPlayed, Double gameWinRatio, Integer totalScavLocCaptures, Double scavLocsCapturedPerGame) {

        this.firstName = firstName;
        this.lastName = lastName;

        this.gamesWon = gamesWon;
        this.gamesPlayed = gamesPlayed;
        this.gameWinRatio = gameWinRatio;
        this.totalScavLocCaptures = totalScavLocCaptures;
        this.scavLocsCapturedPerGame = scavLocsCapturedPerGame;
    }

    // TODO getters and setters for the user data
}
