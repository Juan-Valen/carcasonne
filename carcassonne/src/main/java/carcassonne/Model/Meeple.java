package carcassonne.Model;

import java.io.Serializable;

public class Meeple implements Serializable {
    private int position = -1;
    private int playerIndex;

    public Meeple() {
    }

    public Meeple(int playerIndex) {
        this.playerIndex = playerIndex;
    };

    public void setPosition(int position) {
        this.position = position;
    }

    public void setPlayerIndex(int playerIndex) {
        this.playerIndex = playerIndex;
    }

    public int getPosition() {
        return position;
    }

    public int getPlayerIndex() {
        return playerIndex;
    }
}
