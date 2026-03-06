package carcassonne.Model;

import carcassonne.DataType.TileSide;
import javafx.scene.layout.Pane;

public class Tile {
    /// ID to indentify the displayed image
    private final char type;
    /// List of the type of sides of the tile
    private final TileSide[] sides;
    /// Angle of the tile (1/2/3/0 -> 90°/180°/270°/360°)
    private int orientation = 0;
    private Meple meple;
    private Pane pane;

    public Tile(char type, TileSide[] sides) {
        this.type = type;
        this.sides = sides;
    }

    public Tile(char type, TileSide[] sides, Pane pane) {
        this.type = type;
        this.sides = sides;
        this.pane = pane;
    }

    public Tile(char type, TileSide[] sides, int orientation) {
        this.type = type;
        this.sides = sides;
        this.orientation = orientation;
    }

    public char getType() {
        return type;
    }

    public void setPane(Pane pane) {
        this.pane = pane;
    }

    public int getOrientation() {
        return orientation;
    }

    public TileSide getSideType(int side) throws IndexOutOfBoundsException {
        if (side < 0 || side > 3)
            throw new IndexOutOfBoundsException("Side must be in range 0 to 3");
        return sides[(side + orientation) % 4];
    }

    public void rotateTile() {
        // // orientation += toRight ? 1 : 3; // -1 ≡ 3 [4]
        orientation += 3;
        orientation %= 4;
    }

    public Pane getPane() {
        return pane;
    }
}
