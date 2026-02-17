package carcassonne.Model;

import java.util.ArrayList;
import java.util.List;

public class Board {

    // Available spots is where you can place the current tile
    // Taken spots is where the tiles are currently

    private Spot[][] board;
    private List<Spot> availableSpots;
    private List<Spot> takenSpots;

    public Board()
    {
        board = new Spot[145][145];
        for(int x = 0; x < 145; x++)
        {
            for (int y = 0; y < 145; y++)
            {
                board[x][y] = new Spot(x, y);
            }
        }

        availableSpots = new ArrayList<>();
        takenSpots = new ArrayList<>();

    }

    public Spot getSpot(int x, int y) throws IllegalArgumentException
    {
        if(x<0 || x>144 || y<0 || y>144) throw new IllegalArgumentException("Coordinates can't exceed board size");

        return board[x][y];
    }

    public void updateSpots(Spot spot) throws IllegalArgumentException
    {
        if(takenSpots.contains(spot)) throw new IllegalArgumentException("Spot already played");

        takenSpots.add(spot);
        availableSpots.remove(spot);

        int x = spot.getX();
        int y = spot.getY();

        if(x>0)
        {
            Spot spotN = board[x-1][y];
            if (!spotN.hasTile() && !availableSpots.contains(spotN)) availableSpots.add(spotN);
        }

        if(x<144)
        {
            Spot spotS = board[x+1][y];
            if (!spotS.hasTile() && !availableSpots.contains(spotS)) availableSpots.add(spotS);
        }

        if(y>0)
        {
            Spot spotW = board[x][y-1];
            if (!spotW.hasTile() && !availableSpots.contains(spotW)) availableSpots.add(spotW);
        }

        if(y<144)
        {
            Spot spotE = board[x][y+1];
            if(!spotE.hasTile() && !availableSpots.contains(spotE)) availableSpots.add(spotE);
        }
    }

    public List<Spot> getAvailableSpots()
    {
        return availableSpots;
    }

    public List<Spot> getTakenSpots()
    {
        return takenSpots;
    }
}
