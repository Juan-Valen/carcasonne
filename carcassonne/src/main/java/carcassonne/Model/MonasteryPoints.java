package carcassonne.Model;


public class MonasteryPoints {


    private final Board board;


    public MonasteryPoints(Board board) {
        this.board = board;
    }


    /**
     * Classic monastery scoring:
     * - Returns 9 points if the monastery at (x, y) is complete:
     *   the 3x3 area centered at (x, y) is fully occupied (all 8 neighbors placed).
     * - Returns 0 otherwise (no partial scoring in classic mid-game).
     * - Requires that (x, y) holds a monastery tile (type 'A' or 'B'); otherwise returns 0.
     */
    public int calculateMonasteryPoints(int x, int y) {
        Tile center = safeGetTile(x, y);
        if (center == null || !isMonastery(center)) {
            return 0; // Not a monastery at this location
        }


        // Count placed tiles in the 3x3 area centered at (x, y)
        // Classic completion requires all 8 neighbors to be present (center is obviously present).
        int placedNeighbors = 0;


        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue; // skip center
                Tile t = safeGetTile(x + dx, y + dy);
                if (t != null) {
                    placedNeighbors++;
                }
            }
        }


        // Complete monastery only if all 8 neighbors are present
        return (placedNeighbors == 8) ? 9 : 0;
    }


    // ----------------- helpers -----------------


    private boolean isMonastery(Tile tile) {
        char t = tile.getType();
        return t == 'A' || t == 'B';
    }


    private Tile safeGetTile(int x, int y) {
        try {
            return board.getTile(x, y);
        } catch (Exception e) {
            // Out of bounds or other access issues → treat as no tile (not placed)
            return null;
        }
    }
}
