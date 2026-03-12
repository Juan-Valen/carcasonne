package carcassonne.Model;


import carcassonne.DataType.TileSide;


import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
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


    public static class RoadResult {
        private final int points;
        private final Set<Integer> winnerPlayerIndices;

        public RoadResult(int points, Set<Integer> winnerPlayerIndices) {
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


    /**
     * Calculates the points for a road starting from (startX, startY) exiting via side startSide.
     * Rules:
     * - If any road edge leads to empty space or a non-connecting neighbor from a NON-ENDER tile, the road is incomplete -> 0 points.
     * - Traversal STOPS on road ender tiles (by type), even if they have other road sides.
     * - Loops are complete (no open ends encountered) -> points = unique tiles in the loop.
     * - Points = number of unique tiles in the connected road component reached from the starting edge.
     */
    public int calculateRoadPoints(int startX, int startY, int startSide) {
        return calculateRoadResult(startX, startY, startSide).getPoints();
    }


    public RoadResult calculateRoadResult(int startX, int startY, int startSide) {
        // Get start tile safely
        Tile startTile = safeGetTile(startX, startY);
        if (startTile == null) return new RoadResult(0, Collections.emptySet());


        // Must start on a road side
        if (startTile.getSideType(startSide) != TileSide.ROAD) return new RoadResult(0, Collections.emptySet());


        // Track visited edges to avoid infinite loops (edge = tile+side)
        Set<String> visitedEdges = new HashSet<>();
        // Track unique tiles counted for scoring
        Set<String> visitedTiles = new HashSet<>();
        // Track road sides (exits) of tiles in the road for majority-winner detection
        Map<String, Set<Integer>> roadSidesByTile = new HashMap<>();


        Queue<Edge> q = new LinkedList<>();
        q.add(new Edge(startX, startY, startSide));


        boolean complete = true;


        while (!q.isEmpty()) {
            Edge e = q.poll();
            int x = e.x, y = e.y, side = e.side;


            Tile tile = safeGetTile(x, y);
            if (tile == null) continue; // shouldn't happen if we queued correctly


            // Skip repeated edges
            String currentEdgeKey = edgeKey(x, y, side);
            if (!visitedEdges.add(currentEdgeKey)) continue;


            // Edge must be a road on this tile (orientation already handled by Tile.getSideType)
            if (tile.getSideType(side) != TileSide.ROAD) continue;


            // Count this tile
            visitedTiles.add(tileKey(x, y));
            recordRoadSide(roadSidesByTile, x, y, side);


            // If the tile is not a road ender, enqueue all its other road sides for exploration
            if (!isRoadEnd(tile)) {
                enqueueOtherRoadSides(q, visitedEdges, tile, x, y, side);
            }


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
                    complete = false;
                }
                continue;
            }


            // We reached a connecting neighbor
            visitedTiles.add(tileKey(nx, ny));
            recordRoadSide(roadSidesByTile, nx, ny, opp);


            // If the neighbor is a ROAD ENDER, STOP expanding from it (even if it has other road sides)
            if (isRoadEnd(neighbor)) {
                // Do not enqueue other sides from the ender.
                // We also do not consider any further edges beyond this end.
                continue;
            }


            // Otherwise, explore all other road exits on the neighbor (excluding the side we came from)
            enqueueOtherRoadSides(q, visitedEdges, neighbor, nx, ny, opp);
        }


        // Open (incomplete) roads score 0
        if (!complete) {
            return new RoadResult(0, Collections.emptySet());
        }


        // Complete road -> points = number of unique tiles in this connected road component
        return new RoadResult(visitedTiles.size(), findMajorityWinners(visitedTiles, roadSidesByTile));
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


    private void enqueueOtherRoadSides(Queue<Edge> q, Set<String> visitedEdges, Tile tile, int x, int y, int excludedSide) {
        for (int nextSide = 0; nextSide < 4; nextSide++) {
            if (nextSide == excludedSide) continue;
            if (tile.getSideType(nextSide) != TileSide.ROAD) continue;


            // Enqueue the edge (neighbor tile via nextSide). We'll validate its next neighbor in subsequent iterations.
            String nextEdgeKey = edgeKey(x, y, nextSide);
            if (!visitedEdges.contains(nextEdgeKey)) {
                q.add(new Edge(x, y, nextSide));
            }
        }
    }


    private Set<Integer> findMajorityWinners(Set<String> visitedTiles, Map<String, Set<Integer>> roadSidesByTile) {
        Map<Integer, Integer> meeplesByPlayer = new HashMap<>();

        for (String key : visitedTiles) {
            Tile tile = tileFromKey(key);
            if (tile == null) continue;

            Meeple meeple = tile.getMeeple();
            if (meeple == null) continue;

            Set<Integer> roadSides = roadSidesByTile.get(key);
            if (roadSides == null || !roadSides.contains(meeple.getPosition())) continue;

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


    private Tile tileFromKey(String key) {
        String[] parts = key.split(",");
        int x = Integer.parseInt(parts[0]);
        int y = Integer.parseInt(parts[1]);
        return safeGetTile(x, y);
    }


    private void recordRoadSide(Map<String, Set<Integer>> roadSidesByTile, int x, int y, int side) {
        roadSidesByTile.computeIfAbsent(tileKey(x, y), ignored -> new HashSet<>()).add(side);
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
