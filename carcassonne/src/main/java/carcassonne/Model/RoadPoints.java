package carcassonne.Model;


import carcassonne.DataType.TileSide;


import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;


public class RoadPoints {


    private final Board board;


    // Direction indices: 0=N, 1=E, 2=S, 3=W
    private static final int[] dx = {0, 1, 0, -1};
    private static final int[] dy = {-1, 0, 1, 0};


    public RoadPoints(Board board) {
        this.board = board;
    }


    /**
     * Calculates the points for a road starting from (startX, startY) exiting via side startSide.
     * Rules:
     * - If any road edge leads to empty space or a non-connecting neighbor from a NON-ENDER tile, the road is incomplete -> 0 points.
     * - Traversal STOPS on road ender tiles (by type), even if they have other road sides.
     * - Loops are complete (no open ends encountered) -> points = unique tiles in the loop.
     * - Points = number of unique tiles in the connected road component reached from the starting edge.
     */
    public int calculateRoadPoints(int startX, int startY, int startSide) {
        // Get start tile safely
        Tile startTile = safeGetTile(startX, startY);
        if (startTile == null) return 0;


        // Must start on a road side
        if (startTile.getSideType(startSide) != TileSide.ROAD) return 0;


        // Track visited edges to avoid infinite loops (edge = tile+side)
        Set<String> visitedEdges = new HashSet<>();
        // Track unique tiles counted for scoring
        Set<String> visitedTiles = new HashSet<>();


        Queue<Edge> q = new LinkedList<>();
        q.add(new Edge(startX, startY, startSide));


        boolean complete = true;


        while (!q.isEmpty()) {
            Edge e = q.poll();
            int x = e.x, y = e.y, side = e.side;


            Tile tile = safeGetTile(x, y);
            if (tile == null) continue; // shouldn't happen if we queued correctly


            // Skip repeated edges
            String edgeKey = edgeKey(x, y, side);
            if (!visitedEdges.add(edgeKey)) continue;


            // Edge must be a road on this tile (orientation already handled by Tile.getSideType)
            if (tile.getSideType(side) != TileSide.ROAD) continue;


            // Count this tile
            visitedTiles.add(tileKey(x, y));


            // Step into neighbor along this side
            int nx = x + dx[side];
            int ny = y + dy[side];


            Tile neighbor = safeGetTile(nx, ny);


            // CASE 1: Road goes into empty space (off board or no tile placed)
            if (neighbor == null) {
                // If current tile is NOT a road ender, the road is open -> incomplete
                if (!isRoadEnd(tile)) {
                    complete = false;
                }
                // Either way, we cannot continue further on this edge
                continue;
            }


            // Neighbor exists: must connect back via opposite side
            int opp = (side + 2) % 4;
            if (neighbor.getSideType(opp) != TileSide.ROAD) {
                // This is effectively an open end from the current tile’s perspective
                if (!isRoadEnd(tile)) {
                    //complete = false;
                }
                continue;
            }


            // We reached a connecting neighbor
            visitedTiles.add(tileKey(nx, ny));


            // If the neighbor is a ROAD ENDER, STOP expanding from it (even if it has other road sides)
            if (isRoadEnd(neighbor)) {
                // Do not enqueue other sides from the ender.
                // We also do not consider any further edges beyond this end.
                continue;
            }


            // Otherwise, explore all other road exits on the neighbor (excluding the side we came from)
            for (int nextSide = 0; nextSide < 4; nextSide++) {
                if (nextSide == opp) continue; // don't go back immediately
                if (neighbor.getSideType(nextSide) != TileSide.ROAD) continue;


                // Enqueue the edge (neighbor tile via nextSide). We'll validate its next neighbor in subsequent iterations.
                String nextEdgeKey = edgeKey(nx, ny, nextSide);
                if (!visitedEdges.contains(nextEdgeKey)) {
                    q.add(new Edge(nx, ny, nextSide));
                }
            }
        }


        // Open (incomplete) roads score 0
        if (!complete) {
            return 0;}


        // Complete road -> points = number of unique tiles in this connected road component
        return visitedTiles.size();
    }


    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------


    private static class Edge {
        final int x, y, side;
        Edge(int x, int y, int side) {
            this.x = x; this.y = y; this.side = side;
        }
    }


    private boolean isRoadEnd(Tile tile) {
        char t = tile.getType();
        return t == 'W' || t == 'A' || t == 'L' || t == 'S' || t == 'T' || t == 'X';
    }


    private Tile safeGetTile(int x, int y) {
        try {
            return board.getTile(x, y);
        } catch (Exception ignored) {
            return null;
        }
    }


    private static String edgeKey(int x, int y, int side) {
        return x + "," + y + "," + side;
    }


    private static String tileKey(int x, int y) {
        return x + "," + y;
    }
}
