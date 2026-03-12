package carcassonne.Model;


import carcassonne.DataType.TileSide;


import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
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


    public static class CityResult {
        private final int points;
        private final Set<Integer> winnerPlayerIndices;


        public CityResult(int points, Set<Integer> winnerPlayerIndices) {
            this.points = points;
            this.winnerPlayerIndices = Set.copyOf(winnerPlayerIndices);
        }


        public int getPoints() {
            return points;
        }


        public Set<Integer> getWinnerPlayerIndices() {
            return winnerPlayerIndices;
        }
    }


    public int calculateCityPoints(int startX, int startY, int startSide) {
        return calculateCityResult(startX, startY, startSide).getPoints();
    }


    public CityResult calculateCityResult(int startX, int startY, int startSide) {
        Tile start = safeGetTile(startX, startY);
        if (start == null) return new CityResult(0, Collections.emptySet());
        if (start.getSideType(startSide) != TileSide.CITY) return new CityResult(0, Collections.emptySet());


        // Track visited edges (tile,side) to prevent infinite loops
        Set<String> visitedEdges = new HashSet<>();
        // Track unique tiles for scoring
        Set<String> visitedTiles = new HashSet<>();
        // Track city sides (exits) of tiles in the city for majority-winner detection
        Map<String, Set<Integer>> citySidesByTile = new HashMap<>();

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
            String currentEdgeKey = edgeKey(x, y, side);
            if (!visitedEdges.add(currentEdgeKey)) continue;


            // Must be traversing city on this side
            if (tile.getSideType(side) != TileSide.CITY) continue;


            // Count this tile (once)
            String tKey = tileKey(x, y);
            if (visitedTiles.add(tKey) && tile.getBonusPoint()) {
                shieldCount++;
            }
            recordCitySide(citySidesByTile, x, y, side);


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
            recordCitySide(citySidesByTile, nx, ny, opp);


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


        int tiles = visitedTiles.size();
        boolean complete = (openEndCount == 0);
        int points = complete ? (2 * tiles + 2 * shieldCount) : (tiles + shieldCount);


        if (!complete || points == 0) {
            return new CityResult(points, Collections.emptySet());
        }


        return new CityResult(points, findMajorityWinners(visitedTiles, citySidesByTile));
    }


    // ----------------- helpers -----------------


    private Set<Integer> findMajorityWinners(Set<String> visitedTiles, Map<String, Set<Integer>> citySidesByTile) {
        Map<Integer, Integer> meeplesByPlayer = new HashMap<>();


        for (String key : visitedTiles) {
            Tile tile = tileFromKey(key);
            if (tile == null) continue;


            Meeple meeple = tile.getMeeple();
            if (meeple == null) continue;


            Set<Integer> citySides = citySidesByTile.get(key);
            if (citySides == null || !citySides.contains(meeple.getPosition())) continue;


            meeplesByPlayer.merge(meeple.getPlayerIndex(), 1, Integer::sum);
        }


        if (meeplesByPlayer.isEmpty()) {
            return Collections.emptySet();
        }


        int maxMeeples = Collections.max(meeplesByPlayer.values());
        Set<Integer> winners = new HashSet<>();
        for (Map.Entry<Integer, Integer> entry : meeplesByPlayer.entrySet()) {
            if (entry.getValue() == maxMeeples) {
                winners.add(entry.getKey());
            }
        }


        return winners;
    }


    private void recordCitySide(Map<String, Set<Integer>> citySidesByTile, int x, int y, int side) {
        citySidesByTile.computeIfAbsent(tileKey(x, y), ignored -> new HashSet<>()).add(side);
    }


    private Tile tileFromKey(String key) {
        String[] parts = key.split(",");
        int x = Integer.parseInt(parts[0]);
        int y = Integer.parseInt(parts[1]);
        return safeGetTile(x, y);
    }


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
