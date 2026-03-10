package carcassonne.Model;


import carcassonne.DataType.TileSide;


import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;


/**
 * Calculates points for city features starting from a specific tile side.
 * Rules:
 * - City is COMPLETE if:
 *   * every outward city edge connects to a CITY on the neighbor's opposite side; OR
 *   * any outward city edge that does not connect terminates at a CITY ENDER tile (H or I).
 * - City ENDER tiles are H and I (by type letter only, independent of shape).
 * - When reaching a city ender, we count it but DO NOT expand from it further.
 * - Scoring:
 *   * COMPLETE: 2 points per unique tile + 2 per shield (tile.getBonusPoint() == true)
 *   * INCOMPLETE: 1 point per unique tile + 1 per shield
 * - Counts each tile at most once (even if it has multiple city segments).
 */
public class CityPoints {


    private final Board board;


    // Directions: 0=N, 1=E, 2=S, 3=W
    private static final int[] dx = {0, 1, 0, -1};
    private static final int[] dy = {-1, 0, 1, 0};


    public CityPoints(Board board) {
        this.board = board;
    }


    public int calculateCityPoints(int startX, int startY, int startSide) {
        Tile start = safeGetTile(startX, startY);
        if (start == null) return 0;
        if (start.getSideType(startSide) != TileSide.CITY) return 0;


        // Track visited edges (tile,side) to prevent infinite loops
        Set<String> visitedEdges = new HashSet<>();
        // Track unique tiles for scoring
        Set<String> visitedTiles = new HashSet<>();
        // Shields counted per tile (bonusPoint == has shield)
        int shieldCount = 0;
        // Number of city edges that end in empty space or mismatch (from NON-ender tiles)
        int openEndCount = 0;


        Queue<Edge> q = new LinkedList<>();
        q.add(new Edge(startX, startY, startSide));


        while (!q.isEmpty()) {
            Edge e = q.poll();
            int x = e.x, y = e.y, side = e.side;


            Tile tile = safeGetTile(x, y);
            if (tile == null) continue;


            // Edge visitation guard
            String edgeKey = edgeKey(x, y, side);
            if (!visitedEdges.add(edgeKey)) continue;


            // Must be traversing city on this side
            if (tile.getSideType(side) != TileSide.CITY) continue;


            // Count this tile (once)
            String tKey = tileKey(x, y);
            if (visitedTiles.add(tKey) && tile.getBonusPoint()) {
                // Each of {C,F,M,O,Q,S} has exactly one shield
                shieldCount++;
            }


            // Step to neighbor in direction 'side'
            int nx = x + dx[side];
            int ny = y + dy[side];


            Tile neighbor = safeGetTile(nx, ny);


            // Edge into empty space (off board or no tile placed)
            if (neighbor == null) {
                // If current tile is not a city ender (H/I), this is an open end
                if (!isCityEnder(tile)) {
                    openEndCount++;
                }
                continue;
            }


            // Neighbor exists: it must connect back via opposite side as CITY
            int opp = (side + 2) % 4;
            if (neighbor.getSideType(opp) != TileSide.CITY) {
                // Mismatch; if current tile is not a city ender, it's an open end
                if (!isCityEnder(tile)) {
                    openEndCount++;
                }
                continue;
            }


            // Count neighbor tile (once) because it's part of the connected city
            String nKey = tileKey(nx, ny);
            if (visitedTiles.add(nKey) && neighbor.getBonusPoint()) {
                shieldCount++;
            }


            // If the neighbor is a city ender, DO NOT expand from it any further
            if (isCityEnder(neighbor)) {
                continue;
            }


            // Explore other CITY exits from the neighbor (excluding the way we came)
            for (int nextSide = 0; nextSide < 4; nextSide++) {
                if (nextSide == opp) continue; // don't go back immediately
                if (neighbor.getSideType(nextSide) != TileSide.CITY) continue;


                String nextEdge = edgeKey(nx, ny, nextSide);
                if (!visitedEdges.contains(nextEdge)) {
                    q.add(new Edge(nx, ny, nextSide));
                }
            }
        }


        // Final scoring
        int tiles = visitedTiles.size();
        boolean complete = (openEndCount == 0);


        if (complete) {
            return 2 * tiles + 2 * shieldCount;
        } else {
            return tiles + shieldCount;
        }
    }


    // ----------------- helpers -----------------


    private boolean isCityEnder(Tile tile) {
        char t = tile.getType();
        return t == 'H' || t == 'I';
    }


    private Tile safeGetTile(int x, int y) {
        try {
            return board.getTile(x, y);
        } catch (Exception e) {
            return null;
        }
    }


    private static String tileKey(int x, int y) {
        return x + "," + y;
    }


    private static String edgeKey(int x, int y, int side) {
        return x + "," + y + "," + side;
    }


    private static class Edge {
        final int x, y, side;
        Edge(int x, int y, int side) {
            this.x = x; this.y = y; this.side = side;
        }
    }
}
