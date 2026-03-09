package carcassonne.Model;

import carcassonne.DataType.TileSide;

import java.io.Serializable;
import java.util.*;

public class CastlePoints implements Serializable {

    private final Board board;

    // N E S W
    private static final int[] dx = {0, 1, 0, -1};
    private static final int[] dy = {-1, 0, 1, 0};

    public CastlePoints(Board board) {
        this.board = board;
    }

    public int calculateCityPoints(int startX, int startY, int startSide) {

        Tile startTile;

        try {
            startTile = board.getTile(startX, startY);
        } catch (Exception e) {
            return 0;
        }

        if (startTile == null) return 0;
        if (startTile.getSideType(startSide) != TileSide.CITY) return 0;

        Set<String> visitedEdges = new HashSet<>();
        Set<String> visitedTiles = new HashSet<>();

        Queue<Edge> queue = new LinkedList<>();
        queue.add(new Edge(startX, startY, startSide));

        boolean complete = true;
        int pennants = 0;

        while (!queue.isEmpty()) {

            Edge e = queue.poll();

            int x = e.x;
            int y = e.y;
            int side = e.side;

            String edgeKey = x + "," + y + "," + side;
            if (!visitedEdges.add(edgeKey)) continue;

            Tile tile = board.getTile(x, y);

            if (tile == null) continue;

            String tileKey = x + "," + y;

            if (visitedTiles.add(tileKey)) {
                if (tile.getBonusPoint()) {
                    pennants++;
                }
            }

            int nx = x + dx[side];
            int ny = y + dy[side];

            Tile neighbor;

            try {
                neighbor = board.getTile(nx, ny);
            } catch (Exception ex) {
                complete = false;
                continue;
            }

            if (neighbor == null) {
                complete = false;
                continue;
            }

            int opposite = (side + 2) % 4;

            if (neighbor.getSideType(opposite) != TileSide.CITY) {
                complete = false;
                continue;
            }

            for (int nextSide = 0; nextSide < 4; nextSide++) {

                if (neighbor.getSideType(nextSide) != TileSide.CITY) continue;

                queue.add(new Edge(nx, ny, nextSide));
            }
        }

        if (!complete) return 0;

        return (visitedTiles.size() * 2) + (pennants * 2);
    }

    private static class Edge {
        int x;
        int y;
        int side;

        Edge(int x, int y, int side) {
            this.x = x;
            this.y = y;
            this.side = side;
        }
    }
}