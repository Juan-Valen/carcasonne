package carcassonne.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jdk.internal.org.jline.terminal.impl.PosixPtyTerminal;

public class Board {

    // Available spots is where you can place the current tile
    // Taken spots is where the tiles are currently

    private Spot[][] board;
    private Map<Integer, List<Spot>> availableSpots; // Cells next to the tiles
    private List<Spot> posibleAvailableSpots; // Cells next to the tiles
    private List<Spot> takenSpots; // Cells with tiles
    private Spot minSpot = new Spot(2, 8);
    private Spot maxSpot = new Spot(5, 8);

    public Board() {
        board = new Spot[145][145];
        for (int x = 0; x < 145; x++) {
            for (int y = 0; y < 145; y++) {
                board[x][y] = new Spot(x, y);
            }
        }

        availableSpots = new ArrayList<>();

        // Middle spot can receive a tile
        availableSpots.add(board[72][72]);

        takenSpots = new ArrayList<>();

    }

    public Spot getSpot(int x, int y) throws IllegalArgumentException {
        if (x < 0 || x > 144 || y < 0 || y > 144)
            throw new IllegalArgumentException("Coordinates can't exceed board size");
        return board[x][y];
    }

    public boolean isSpotInBoard(Spot spot) {
        int x = spot.getX();
        int y = spot.getY();
        if (x < 0 || x > 144 || y < 0 || y > 144)
            return false;
        return board[x][y] == spot;
    }

    public void updateSpots(Spot spot) {
        if (takenSpots.contains(spot))
            return;

        takenSpots.add(spot);
        posibleAvailableSpots.remove(spot);
        // add available spots according to the current tile rotation

        setMinMax(spot);
        setAvailableSpots(spot);
    }

    private void setAvailableSpots(Spot spot) {
        int x = spot.getX();
        int y = spot.getY();

        if (x > 0) {
            Spot spotN = board[x - 1][y];
            if (!spotN.hasTile() && !availableSpots.contains(spotN))
                availableSpots.add(spotN);
        }

        if (x < 144) {
            Spot spotS = board[x + 1][y];
            if (!spotS.hasTile() && !availableSpots.contains(spotS))
                availableSpots.add(spotS);
        }

        if (y > 0) {
            Spot spotW = board[x][y - 1];
            if (!spotW.hasTile() && !availableSpots.contains(spotW))
                availableSpots.add(spotW);
        }

        if (y < 144) {
            Spot spotE = board[x][y + 1];
            if (!spotE.hasTile() && !availableSpots.contains(spotE))
                availableSpots.add(spotE);
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

    public List<Spot> getAvailableSpots() {
        return availableSpots;
    }

    public List<Spot> getTakenSpots() {
        return takenSpots;
    }
}
