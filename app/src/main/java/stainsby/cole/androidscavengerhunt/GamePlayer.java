// ---------------------------------------------------------------------------
// DESC:
//   during a game, all user accounts will be loaded into
//   a GamePlayer class. The admin player type and the
//   participant type will both inherit from this class
// ` This class should contain any base behavior for any player
// PROJECT:
//   Scavenger Hunt project
// SOURCES:
//   N/A
// DATE:
//   11/26/2021
// ---------------------------------------------------------------------------

package stainsby.cole.androidscavengerhunt;

public abstract class GamePlayer {
    // each player will have an attached user (account)
    User user;

    String displayName;


    // user getter
    public User getUser() {
        return user;
    }

    // user setter
    public void setUser(User user) {
        this.user = user;
    }
}
