package Version1_2_0.Actions;

import Version1_2_0.Player;
import Version1_2_0.Utility;

public abstract class Action {
    Player player;
    Utility u = new Utility();

    public Action(Player _player) {
        player = _player;
    }

    public abstract void takeAction();
    public abstract void undoAction();
}
