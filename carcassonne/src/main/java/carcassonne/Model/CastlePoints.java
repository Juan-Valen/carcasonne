package carcassonne.Model;

import java.util.*;

public class CastlePoints {

    private final Board board;

    public static final int CITY = 2;

    // N, E, S, W
    private static final int[] dx = {0, 1, 0, -1};
    private static final int[] dy = {-1, 0, 1, 0};

    public CastlePoints(Board board) {
        this.board = board;
    }

    public int calculateCityPoints(Spot start, int startSide) {

        if (!start.hasTile()) return 0;
        if (start.getTile().getSideType(startSide) != CITY) return 0;

        Set<String> visited = new HashSet<>();
        Set<Spot> cityTiles = new HashSet<>();

        Queue<State> queue = new LinkedList<>();
        queue.add(new State(start, startSide));

        boolean complete = true;
        int pennants = 0;

        while (!queue.isEmpty()) {

            State s = queue.poll();
            Spot spot = s.spot;
            int entrySide = s.side;

            String key = spot.getX() + "," + spot.getY() + "," + entrySide;
            if (!visited.add(key)) continue;

            Tile tile = spot.getTile();

            // Count tile once
            if (cityTiles.add(spot)) {
                if (tile.hasPennant()) {
                    pennants++;
                }
            }

            // Explore all city exits except where we entered
            for (int side = 0; side < 4; side++) {

                if (side == entrySide) continue;
                if (tile.getSideType(side) != CITY) continue;

                int nx = spot.getX() + dx[side];
                int ny = spot.getY() + dy[side];

                Spot neighbor;

                try {
                    neighbor = board.getSpot(nx, ny);
                } catch (IllegalArgumentException ex) {
                    complete = false;
                    continue;
                }

                if (!neighbor.hasTile()) {
                    complete = false;
                    continue;
                }

                int opposite = (side + 2) % 4;

                if (neighbor.getTile().getSideType(opposite) != CITY) {
                    complete = false;
                    continue;
                }

                queue.add(new State(neighbor, opposite));
            }
        }

        if (!complete) return 0;

        return (cityTiles.size() * 2) + (pennants * 2);
    }

    private static class State {
        Spot spot;
        int side;

        State(Spot spot, int side) {
            this.spot = spot;
            this.side = side;
        }
    }
}