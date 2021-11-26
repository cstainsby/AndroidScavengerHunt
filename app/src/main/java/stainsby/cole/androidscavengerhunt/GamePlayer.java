//-----------------------------------------------------------
// DESC:
//   during a game, all user accounts will be loaded into
//   a
//-----------------------------------------------------------

package stainsby.cole.androidscavengerhunt;

public abstract class GamePlayer {
    // each player will have an attached user (account)
    User user;


    // user getter
    public User getUser() {
        return user;
    }

    // user setter
    public void setUser(User user) {
        this.user = user;
    }
}
