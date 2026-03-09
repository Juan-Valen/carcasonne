package carcassonne.Model;

import java.util.Objects;

public class Spot {

    private int x;
    private int y;
    private Tile tile;

    public Spot(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public Spot(int x, int y, Tile tile){
        this.x = x;
        this.y = y;
        this.tile = tile;
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean hasTile() {
        return tile != null;
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Spot)) {
            return false;
        }
        Spot spot = (Spot) obj;
        return spot.getX() == x && spot.getY() == y;
    }
}
