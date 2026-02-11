package carcassonne.Model;

import java.util.ArrayList;
import java.util.List;

public class Game {
    /// List of the remaining unused tiles
    private List<Tile> deck = new ArrayList<>();

    /// Number of players on the game
    private int totalPlayers;

    /// List of all the players
    private Player[] players;

    /// Is the game local (false) or online (true)
    private boolean online;

    /// Current state of the game board
    private Board board;

    /// Indexes who player's turn it is
    private int activePlayer;

    /// Number of available spots to play the current tile
    private int availableSpots;

    public Player getActivePlayer()
    {
        return players[activePlayer];
    }

    public Tile getCurrentTile()
    {
        return deck.getFirst();
    }
}
