package carcassonne.Model;

public class Cell {
    public final int row;
    public final int col;
    public Character tileId = null; // ID of the tile placed in this cell
    public boolean placed = false; // Whether a tile has been placed in this cell
    public int rotation = 0; // Rotation of the tile in this cell (0, 90, 180, 270)

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public boolean lessThan(int x, int y) {
        return row < x && col < y;
    }

    public boolean moreThan(int x, int y) {
        return row > x && col > y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Cell))
            return false;
        Cell cell = (Cell) o;
        return row == cell.row && col == cell.col;
    }

    @Override
    public int hashCode() {
        return 31 * row + col;
    }

    @Override
    public String toString() {
        return "Cell[" + row + "," + col + "]";
    }
}
