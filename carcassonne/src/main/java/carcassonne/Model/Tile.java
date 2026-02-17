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

    public int getOrientation()
    {
        return orientation;
    }

    public int getSideType(int side) throws IndexOutOfBoundsException
    {
        if(side < 0 || side > 3) throw new IndexOutOfBoundsException("Side must be in range 0 to 3");
        return sides[(side + orientation) % 4];
    }

    public void rotateTile(boolean toRight)
    {
        orientation += toRight ? 1 : 3; // -1 ≡ 3 [4]
        orientation %= 4;
    }
}

