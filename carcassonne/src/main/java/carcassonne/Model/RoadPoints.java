package carcassonne.Model;

import carcassonne.DataType.TileSide;

import java.util.*;

public class RoadPoints {

    private final Board board;

    // N E S W
    private static final int[] dx = {0, 1, 0, -1};
    private static final int[] dy = {-1, 0, 1, 0};

    public RoadPoints(Board board) {
        this.board = board;
    }

    public int calculateRoadPoints(int startX, int startY, int startSide) {

        Tile startTile;

        try {
            startTile = board.getTile(startX, startY);
        } catch (Exception e) {
            return 0;
        }

        if (startTile == null) return 0;
        if (startTile.getSideType(startSide) != TileSide.ROAD) return 0;

        Set<String> visitedEdges = new HashSet<>();
        Set<String> visitedTiles = new HashSet<>();

        Queue<Node> queue = new LinkedList<>();
        queue.add(new Node(startX, startY, startSide));

        boolean complete = true;

        while (!queue.isEmpty()) {

            Node node = queue.poll();

            int x = node.x;
            int y = node.y;
            int side = node.side;

            String edgeKey = x + "," + y + "," + side;

            if (!visitedEdges.add(edgeKey)) continue;

            Tile tile = board.getTile(x, y);

            if (tile == null) continue;
            if (tile.getSideType(side) != TileSide.ROAD) continue;

            visitedTiles.add(x + "," + y);

            int nx = x + dx[side];
            int ny = y + dy[side];

            Tile neighbor;

            try {
                neighbor = board.getTile(nx, ny);
            } catch (Exception e) {
                neighbor = null;
            }

            if (neighbor == null) {
                complete = false;
                continue;
            }

            int opposite = (side + 2) % 4;

            if (neighbor.getSideType(opposite) != TileSide.ROAD) {
                complete = false;
                continue;
            }

            visitedTiles.add(nx + "," + ny);

            for (int nextSide = 0; nextSide < 4; nextSide++) {

                if (nextSide == opposite) continue;
                if (neighbor.getSideType(nextSide) != TileSide.ROAD) continue;

                int nx2 = nx + dx[nextSide];
                int ny2 = ny + dy[nextSide];

                Tile nextTile;

                try {
                    nextTile = board.getTile(nx2, ny2);
                } catch (Exception e) {
                    nextTile = null;
                }

                if (nextTile == null) continue;

                int nextOpp = (nextSide + 2) % 4;

                if (nextTile.getSideType(nextOpp) != TileSide.ROAD) continue;

                queue.add(new Node(nx, ny, nextSide));
            }
        }

        if (!complete) return 0;

        return visitedTiles.size();
    }

    private static class Node {
        int x;
        int y;
        int side;

        Node(int x, int y, int side) {
            this.x = x;
            this.y = y;
            this.side = side;
        }
    }

    private boolean isRoadEnd(Tile tile) {
        char t = tile.getType();
        return t == 'W' || t == 'A' || t == 'L' || t == 'S' || t == 'T' || t == 'X';
    }
}