package carcassonne.Model;

public class Tile {
    /// ID to indentify the displayed image
    private final int type;

    /// List of the type of sides of the tile
    private final int[] sides;

    /// Angle of the tile (1/2/3/0 -> 90°/180°/270°/360°)
    private int orientation = 0;

    public Tile(int type, int[] sides)
    {
        this.type = type;
        this.sides = sides;
    }

    public Tile(int type, int[] sides, int orientation)
    {
        this.type = type;
        this.sides = sides;
        this.orientation = orientation;
    }

    public int getType()
    {
        return type;
    }

    public int getSideType(int side)
    {
        return sides[(side + orientation) % 4];
    }

    public void rotateTile(int rotation)
    {
        orientation = (orientation + rotation) % 4;
    }
}

