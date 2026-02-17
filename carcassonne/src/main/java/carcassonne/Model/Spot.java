package carcassonne.Model;

public class Spot
{

    private int x;
    private int y;
    private Tile tile;

    public Spot(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public int getX()
    {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean hasTile()
    {
        return tile != null;
    }

    public Tile getTile()
    {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }
}
