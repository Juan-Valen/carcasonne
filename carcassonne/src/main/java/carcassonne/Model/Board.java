package carcassonne.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board {

    // Available spots is where you can place the current tile
    // Taken spots is where the tiles are currently

    private Tile[][] board;
    private AvailableSpots[] availableSpots;
    private List<Spot> freeSpots;
    private Spot minSpot = new Spot(73, 73);
    private Spot maxSpot = new Spot(73, 73);

    public Board() {
        board = new Tile[144][144];

        availableSpots = new AvailableSpots[] {
                new AvailableSpots("up"),
                new AvailableSpots("right"),
                new AvailableSpots("down"),
                new AvailableSpots("left"),
        };
        Spot center = new Spot(72, 72);
        for (int i = 0; i < availableSpots.length; i++) {
            availableSpots[i].add(center);
        }
        freeSpots = new ArrayList<Spot>();

        freeSpots.add(center);
        List<Spot> sp = availableSpots[0].getSpots();
    }

    public Tile getTile(int x, int y) throws IllegalArgumentException {
        if (x < 0 || x > 144 || y < 0 || y > 144)
            throw new IllegalArgumentException("Coordinates can't exceed board size");

        return board[x][y];
    }

    public List<Spot> getFreeSpots() {
        return freeSpots;
    }

    public void updateSpots(int x, int y, Tile tile) throws IllegalArgumentException {
        if (board[x][y] != null)
            return;

        board[x][y] = tile;
        updateFreeSpots(x, y);
    }

    public boolean hasAvailableSpots() {
        for (AvailableSpots as : availableSpots) {
            if (!as.getSpots().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public void updateAvailableSpots(Tile tile) {
        if (tile == null) {
            return;
        }
        clearAvailableSpots();
        for (Spot free : freeSpots) {
            for (int i = 0; i < 4; i++) {
                tile.rotateTile();
                int x = free.getX();
                int y = free.getY();
                boolean available = true;
                // Check if Left is available
                Tile left = board[x - 1][y];
                if (left != null)
                    available = available && (left.getSideType(1) == tile.getSideType(3));
                Tile right = board[x + 1][y];
                if (right != null)
                    available = available && (right.getSideType(3) == tile.getSideType(1));
                Tile up = board[x][y - 1];
                if (up != null)
                    available = available && (up.getSideType(2) == tile.getSideType(0));
                Tile down = board[x][y + 1];
                if (down != null)
                    available = available && (down.getSideType(0) == tile.getSideType(2));

                if (available) {
                    availableSpots[tile.getOrientation()].add(new Spot(x, y));
                }
            }
        }
    }

    private void updateFreeSpots(int x, int y) {
        Spot spot = new Spot(x, y);
        freeSpots.remove(spot);
        setMinMax(spot);
        // Check if Left is available
        if (x > 0) {
            Tile left = board[x - 1][y];
            if (left == null)
                freeSpots.add(new Spot(x - 1, y));
        }

        if (x < 144) {
            Tile right = board[x + 1][y];
            if (right == null)
                freeSpots.add(new Spot(x + 1, y));
        }

        if (y > 0) {
            Tile up = board[x][y - 1];
            if (up == null)
                freeSpots.add(new Spot(x, y - 1));
        }

        if (y < 144) {
            Tile down = board[x][y + 1];
            if (down == null)
                freeSpots.add(new Spot(x, y + 1));
        }
    }

    private void setMinMax(Spot spot) {
        if (spot.getX() < minSpot.getX()) {
            minSpot.setX(spot.getX());
        }
        if (spot.getY() < minSpot.getY()) {
            minSpot.setY(spot.getY());
        }
        if (spot.getX() > maxSpot.getX()) {
            maxSpot.setX(spot.getX());
        }
        if (spot.getY() > maxSpot.getY()) {
            maxSpot.setY(spot.getY());
        }
    }

    public Spot getMinSpot() {
        return minSpot;
    }

    public Spot getMaxSpot() {
        return maxSpot;
    }

    private void clearAvailableSpots() {
        for (AvailableSpots as : availableSpots) {
            as.clear();
        }
    }

    public List<Spot> getAvailableSpots(int orientation) {
        return availableSpots[orientation].getSpots();
    }

    public Tile getVisibleTile(int x, int y) {
        return board[x][y];
    }
}
