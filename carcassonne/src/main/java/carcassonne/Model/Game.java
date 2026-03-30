package carcassonne.Model;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import carcassonne.DataType.TileSide;
import javafx.scene.layout.Pane;

public class Game implements Serializable {
    private int game_id;
    private boolean online;
    private Date updated_date;
    /// List of the remaining unused tiles
    private List<Tile> deck = new ArrayList<>();
    /// Indexes whose player's turn it is
    private int activePlayer = 0;
    /// List of all the players
    private Player[] players = new Player[2];
    /// Current state of the game board
    private Board board = new Board();
    private Map<Character, TileSide[]> tiles = Map.ofEntries(
            Map.entry('A', new TileSide[] { TileSide.FIELD, TileSide.FIELD, TileSide.ROAD, TileSide.FIELD }),
            Map.entry('B', new TileSide[] { TileSide.FIELD, TileSide.FIELD, TileSide.FIELD, TileSide.FIELD }),
            Map.entry('C', new TileSide[] { TileSide.CITY, TileSide.CITY, TileSide.CITY, TileSide.CITY }),
            Map.entry('D', new TileSide[] { TileSide.CITY, TileSide.ROAD, TileSide.FIELD, TileSide.ROAD }),
            Map.entry('E', new TileSide[] { TileSide.CITY, TileSide.FIELD, TileSide.FIELD, TileSide.FIELD }),
            Map.entry('F', new TileSide[] { TileSide.FIELD, TileSide.CITY, TileSide.FIELD, TileSide.CITY }),
            Map.entry('G', new TileSide[] { TileSide.FIELD, TileSide.CITY, TileSide.FIELD, TileSide.CITY }),
            Map.entry('H', new TileSide[] { TileSide.CITY, TileSide.FIELD, TileSide.CITY, TileSide.FIELD }),
            Map.entry('I', new TileSide[] { TileSide.CITY, TileSide.CITY, TileSide.FIELD, TileSide.FIELD }),
            Map.entry('J', new TileSide[] { TileSide.CITY, TileSide.ROAD, TileSide.ROAD, TileSide.FIELD }),
            Map.entry('K', new TileSide[] { TileSide.CITY, TileSide.FIELD, TileSide.ROAD, TileSide.ROAD }),
            Map.entry('L', new TileSide[] { TileSide.CITY, TileSide.ROAD, TileSide.ROAD, TileSide.ROAD }),
            Map.entry('M', new TileSide[] { TileSide.CITY, TileSide.CITY, TileSide.FIELD, TileSide.FIELD }),
            Map.entry('N', new TileSide[] { TileSide.CITY, TileSide.CITY, TileSide.FIELD, TileSide.FIELD }),
            Map.entry('O', new TileSide[] { TileSide.CITY, TileSide.ROAD, TileSide.ROAD, TileSide.CITY }),
            Map.entry('P', new TileSide[] { TileSide.CITY, TileSide.ROAD, TileSide.ROAD, TileSide.CITY }),
            Map.entry('Q', new TileSide[] { TileSide.CITY, TileSide.CITY, TileSide.FIELD, TileSide.CITY }),
            Map.entry('R', new TileSide[] { TileSide.CITY, TileSide.CITY, TileSide.FIELD, TileSide.CITY }),
            Map.entry('S', new TileSide[] { TileSide.CITY, TileSide.CITY, TileSide.ROAD, TileSide.CITY }),
            Map.entry('T', new TileSide[] { TileSide.CITY, TileSide.CITY, TileSide.ROAD, TileSide.CITY }),
            Map.entry('U', new TileSide[] { TileSide.ROAD, TileSide.FIELD, TileSide.ROAD, TileSide.FIELD }),
            Map.entry('V', new TileSide[] { TileSide.FIELD, TileSide.FIELD, TileSide.ROAD, TileSide.ROAD }),
            Map.entry('W', new TileSide[] { TileSide.FIELD, TileSide.ROAD, TileSide.ROAD, TileSide.ROAD }),
            Map.entry('X', new TileSide[] { TileSide.ROAD, TileSide.ROAD, TileSide.ROAD, TileSide.ROAD }));

    public Game() {
        initDeck();
    }

    public int getActivePlayer() {
        return activePlayer;
    }

    public int getMaxPlayers() {
        return players.length;
    }

    public void setMaxPlayer(int length) {
        players = new Player[length];
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player();
            players[i].initMeeple(i);
        }
    }

    public int[] getPlayersMeepleCount() {
        int[] meples = new int[players.length];
        // Initialize each player with the starting number of meeples (e.g., 7 or 8)
        for (int i = 0; i < players.length; i++) {
            if (players[i] != null)
                meples[i] = players[i].getMeepleCount();
            else
                meples[i] = 0;
        }

        return meples;
    }

    public int[] getPlayersScores() {
        int[] scores = new int[players.length];
        for (int i = 0; i < players.length; i++) {
            if (players[i] != null)
                scores[i] = players[i].getPoints();
            else
                scores[i] = 0;
        }
        return scores;
    }

    public Tile getCurrentTile() {
        if (deck.isEmpty())
            return null;
        return deck.getFirst();
    }

    public Board getBoard() {
        return board;
    }

    public Spot getMin() {
        Spot spot = board.getMinSpot();
        return spot;
    }

    public Spot getMax() {
        Spot spot = board.getMaxSpot();
        return spot;

    }

    public boolean hasPlacedTiles() {
        return board.getFreeSpots().size() > 1;
    }

    public List<Spot> getAvailableSpots() {
        int orientation = getCurrentTile().getOrientation();
        return board.getAvailableSpots(orientation);
    }

    public void placeTile(int x, int y, Pane pane) {
        Tile tile = board.getTile(x, y);
        if (tile != null)
            return;
        tile = deck.removeFirst();

        if (deck.isEmpty()) {
            return;
        }

        tile.setPane(pane);
        board.updateSpots(x, y, tile);
        board.updateAvailableSpots(deck.getFirst());
        while (!board.hasAvailableSpots()) {
            deck.add(deck.removeFirst());
            board.updateAvailableSpots(deck.getFirst());
        }
        activePlayer++;
        activePlayer %= getMaxPlayers();

    }

    public void rotateMeeple() {
        Tile tile = getCurrentTile();
        Meeple meple = tile.getMeeple();
        if (meple == null)
            return;
        int position = meple.getPosition();
        if (position == -1)
            return;
        position += 1;
        position %= 4;
        meple.setPosition(position);
    }

    public void placeMeeple(int direction) {
        Tile tile = getCurrentTile();
        Meeple meple = tile.getMeeple();
        if (meple == null) {
            if (direction == -1) {
                return;
            }
            meple = players[getActivePlayer()].placeMeeple();
            if (meple == null) {
                return;
            }
        }
        if (direction == -1) {
            players[getActivePlayer()].addMeeple(meple);
            tile.setMeeple(null);
            return;
        }
        meple.setPosition(direction);
        tile.setMeeple(meple);
    }

    public void rotateTile(boolean right) {
        Tile tile = deck.getFirst();
        tile.rotateTile();

    }

    public void calculatePoints(int startX, int startY) {
        RoadPoints roadPoints = new RoadPoints(board);
        CityPoints cityPoints = new CityPoints(board);
        MonasteryPoints monasteryPoints = new MonasteryPoints(board);

        // Calculate possible points for each side of the tile at (startX, startY)
        for (int i = 0; i < 3; i++) {
            RoadPoints.RoadResult roadResult = roadPoints.calculateRoadResult(startX, startY, i);
            if (roadResult.getPoints() > 0) {
                for (int playerIndex : roadResult.getWinnerPlayerIndices()) {
                    players[playerIndex].addPoints(roadResult.getPoints());
                }
            }
            CityPoints.CityResult cityResult = cityPoints.calculateCityResult(startX, startY, i);
            if (cityResult.getPoints() > 0) {
                for (int playerIndex : cityResult.getWinnerPlayerIndices()) {
                    players[playerIndex].addPoints(cityResult.getPoints());
                }
            }
            // Monastery point calculation incomplete at the moment, as it requires checking the 8 surrounding tiles
        }
    }

    public Player[] getPlayers() {
        return players;
    }

    public boolean endGame() {
        if (deck.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    private void initDeck() {
        Map<Character, Integer> dictionary = new HashMap<>();
        dictionary.put('D', 4);
        dictionary.put('A', 2);
        dictionary.put('B', 4);
        dictionary.put('C', 1);
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
        Random rand = new Random();
        int size = deck.size();
        for (int i = 1; i < size; i++) {
            int index = rand.nextInt(size - i) + 1;
            deck.add(deck.remove(index));
        }
    }

    private void addTileToDeck(char letter) {
        TileSide[] sides = tiles.get(letter);

        deck.add(new Tile(letter, sides));

    }
}
