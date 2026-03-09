package carcassonne.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import carcassonne.DataType.TileSide;

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
    private Board board = new Board();

    /// Indexes whose player's turn it is
    private int activePlayer;

    /// Number of available spots to play the current tile
    private int availableSpots;

    public Game() {
    }

    private void setDeck() {
        Map<Character, Integer> dictionary = new HashMap<>();
        dictionary.put('A', 2);
        dictionary.put('B', 4);
        dictionary.put('C', 1);
        dictionary.put('D', 4);
        dictionary.put('E', 5);
        dictionary.put('F', 2);
        dictionary.put('G', 1);
        dictionary.put('H', 3);
        dictionary.put('I', 2);
        dictionary.put('J', 3);
        dictionary.put('K', 3);
        dictionary.put('L', 3);
        dictionary.put('M', 2);
        dictionary.put('N', 3);
        dictionary.put('O', 2);
        dictionary.put('P', 3);
        dictionary.put('Q', 1);
        dictionary.put('R', 3);
        dictionary.put('S', 2);
        dictionary.put('T', 1);
        dictionary.put('U', 8);
        dictionary.put('V', 9);
        dictionary.put('W', 4);
        dictionary.put('X', 1);
        for (Map.Entry<Character, Integer> entry : dictionary.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                addTileToDeck(entry.getKey());
            }
        }
    }

    private void addTileToDeck(char letter) {
        TileSide[] sides;
        switch (letter) {
            case 'A':
                sides = new TileSide[] { TileSide.FIELD, TileSide.FIELD, TileSide.ROAD, TileSide.FIELD };
                break;
            case 'B':
                sides = new TileSide[] { TileSide.FIELD, TileSide.FIELD, TileSide.FIELD, TileSide.FIELD };
                break;
            case 'C':
                sides = new TileSide[] { TileSide.CITY, TileSide.CITY, TileSide.CITY, TileSide.CITY };
                break;
            case 'D':
                sides = new TileSide[] { TileSide.CITY, TileSide.ROAD, TileSide.FIELD, TileSide.ROAD };
                break;
            case 'E':
                sides = new TileSide[] { TileSide.CITY, TileSide.FIELD, TileSide.FIELD, TileSide.FIELD };
                break;
            case 'F':
                sides = new TileSide[] { TileSide.FIELD, TileSide.CITY, TileSide.FIELD, TileSide.CITY };
                break;
            case 'G':
                sides = new TileSide[] { TileSide.CITY, TileSide.FIELD, TileSide.CITY, TileSide.FIELD };
                break;
            case 'H':
                sides = new TileSide[] { TileSide.FIELD, TileSide.CITY, TileSide.FIELD, TileSide.CITY };
                break;
            case 'I':
                sides = new TileSide[] { TileSide.FIELD, TileSide.CITY, TileSide.CITY, TileSide.FIELD };
                break;
            case 'J':
                sides = new TileSide[] { TileSide.CITY, TileSide.ROAD, TileSide.ROAD, TileSide.FIELD };
                break;
            case 'K':
                sides = new TileSide[] { TileSide.ROAD, TileSide.CITY, TileSide.FIELD, TileSide.ROAD };
                break;
            case 'L':
                sides = new TileSide[] { TileSide.ROAD, TileSide.CITY, TileSide.ROAD, TileSide.ROAD };
                break;
            case 'M':
                sides = new TileSide[] { TileSide.CITY, TileSide.FIELD, TileSide.FIELD, TileSide.CITY };
                break;
            case 'N':
                sides = new TileSide[] { TileSide.CITY, TileSide.FIELD, TileSide.FIELD, TileSide.CITY };
                break;
            case 'O':
                sides = new TileSide[] { TileSide.CITY, TileSide.ROAD, TileSide.ROAD, TileSide.CITY };
                break;
            case 'P':
                sides = new TileSide[] { TileSide.CITY, TileSide.ROAD, TileSide.ROAD, TileSide.CITY };
                break;
            case 'Q':
                sides = new TileSide[] { TileSide.CITY, TileSide.CITY, TileSide.FIELD, TileSide.CITY };
                break;
            case 'R':
                sides = new TileSide[] { TileSide.CITY, TileSide.CITY, TileSide.FIELD, TileSide.CITY };
                break;
            case 'S':
                sides = new TileSide[] { TileSide.CITY, TileSide.CITY, TileSide.ROAD, TileSide.CITY };
                break;
            case 'T':
                sides = new TileSide[] { TileSide.CITY, TileSide.CITY, TileSide.ROAD, TileSide.CITY };
                break;
            case 'U':
                sides = new TileSide[] { TileSide.ROAD, TileSide.FIELD, TileSide.ROAD, TileSide.FIELD };
                break;
            case 'V':
                sides = new TileSide[] { TileSide.FIELD, TileSide.FIELD, TileSide.ROAD, TileSide.ROAD };
                break;
            case 'W':
                sides = new TileSide[] { TileSide.FIELD, TileSide.ROAD, TileSide.ROAD, TileSide.ROAD };
                break;
            case 'X':
                sides = new TileSide[] { TileSide.ROAD, TileSide.ROAD, TileSide.ROAD, TileSide.ROAD };
                break;
            default:
                return;
        }
        deck.add(new Tile(letter, sides));
    }

    public Player getActivePlayer() {
        return players[activePlayer];
    }

    public Tile getCurrentTile() {
        return deck.getFirst();
    }

    public Board getBoard() {
        return board;
    }

    public void placeTile(int x, int y) throws IllegalArgumentException {
        Spot spot = board.getSpot(x, y);
        if (spot.hasTile())
            throw new IllegalArgumentException("Chosen spot already has a tile");
        spot.setTile(deck.removeFirst());
        board.updateSpots(spot);
    }
}
