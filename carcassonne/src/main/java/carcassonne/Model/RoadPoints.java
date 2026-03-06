package carcassonne.Model;

import java.util.*;

public class RoadPoints {
    private final Board board;

    public static final int ROAD = 1;

    // direction vectors: N, E, S, W
    private static final int[] dx = {0, 1, 0, -1};
    private static final int[] dy = {-1, 0, 1, 0};

    public RoadPoints(Board board) {
        this.board = board;
    }


    public int calculateRoadPoints(Spot start, int startSide) {

        if (!start.hasTile()) return 0;
        if (start.getTile().getSideType(startSide) != ROAD) return 0;

        // Track visited edges: "x,y,side"
        Set<String> visitedEdges = new HashSet<>();
        // Track unique tiles
        Set<Spot> visitedTiles = new HashSet<>();


        Queue<Edge> queue = new LinkedList<>();
        queue.add(new Edge(start, startSide));

        boolean complete = true;

        while (!queue.isEmpty()) {

            Edge e = queue.poll();
            Spot spot = e.spot;
            int side = e.side;

            Tile tile = spot.getTile();
            if (tile.getSideType(side) != ROAD) {
                continue;
            }

            String key = spot.getX() + "," + spot.getY() + "," + side;
            if (!visitedEdges.add(key)) {
                continue; // already processed
            }


            // Move to the neighbor tile
            int nx = spot.getX() + dx[side];
            int ny = spot.getY() + dy[side];

            Spot neighbor = null;
            try {
                neighbor = board.getSpot(nx, ny);
            } catch (IllegalArgumentException ex) {
                neighbor = null;
            }


            if (neighbor == null || !neighbor.hasTile()) {
                // If tile is NOT a valid road end, road is incomplete
                if (isRoadEnd(tile)) {
                    visitedTiles.add(spot);
                    continue;
                }
                complete = false;
                continue;

            }


            Tile neighborTile = neighbor.getTile();
            int oppositeSide = (side + 2) % 4;

            if (neighborTile.getSideType(oppositeSide) != ROAD) {
                complete = false;
                continue;
            }

            visitedTiles.add(neighbor);
            visitedTiles.add(spot);

            // EXPLORE ONLY VALID CONTINUATIONS OF THE ROAD
            for (int nextSide = 0; nextSide < 4; nextSide++) {

                // 1. Don't go backwards
                if (nextSide == oppositeSide) continue;

                // 2. Check neighbor tile has road on this side
                if (neighborTile.getSideType(nextSide) != ROAD) continue;

                // 3. Determine next tile's coordinates
                int nx2 = neighbor.getX() + dx[nextSide];
                int ny2 = neighbor.getY() + dy[nextSide];

                Spot nextSpot = null;
                try {
                    nextSpot = board.getSpot(nx2, ny2);
                } catch (IllegalArgumentException ex) {
                    nextSpot = null;
                }

                // 4. Must be a placed tile
                if (nextSpot == null || !nextSpot.hasTile()) continue;

                // 5. Opposite side must be a road too
                int nextOpp = (nextSide + 2) % 4;
                if (nextSpot.getTile().getSideType(nextOpp) != ROAD) continue;

                // 6. Valid next step → continue BFS
                queue.add(new Edge(neighbor, nextSide));
            }
        }

        // Final scoring
        if (!complete) return 0;

        return visitedTiles.size(); // Classic Carcassonne: 1 point per road tile
    }

    private static class Edge {
        Spot spot;
        int side;
        Edge(Spot spot, int side) {
            this.spot = spot;
            this.side = side;
        }
    }
    private boolean isRoadEnd(Tile tile) {
        char t = tile.getType();
        return t == 'W' || t == 'A' || t == 'L' || t == 'S' || t == 'T' || t == 'X';
    }

}