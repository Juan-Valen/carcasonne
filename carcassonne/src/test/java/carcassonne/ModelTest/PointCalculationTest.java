package carcassonne.ModelTest;

import carcassonne.Model.Board;
import carcassonne.Model.RoadPoints;
import carcassonne.Model.Spot;
import carcassonne.Model.Tile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PointCalculationTest {

    @Test
    public void roadPointCalculationTest() {

        // Road tile: road on east and west
        Tile roadTile = new Tile('U', new int[]{1, 0, 1, 0},1);
        Tile villageTile = new Tile('X', new int[]{1, 1, 1, 1});

        Board board = new Board();

        // Create and place 4 connected road tiles horizontally
        Spot s1 = board.getSpot(70, 70);
        Spot s2 = board.getSpot(71, 70);
        Spot s3 = board.getSpot(72, 70);
        Spot s4 = board.getSpot(73, 70);

        s1.setTile(villageTile);
        s2.setTile(roadTile);
        s3.setTile(roadTile);
        s4.setTile(villageTile);

        board.updateSpots(s1);
        board.updateSpots(s2);
        board.updateSpots(s3);
        board.updateSpots(s4);

        RoadPoints roadPoints = new RoadPoints(board);

        int points = roadPoints.calculateRoadPoints(s1, 1); // start at s1 east side

        Assertions.assertEquals(4, points, "Road should consist of 4 tiles");
    }

    @Test
    public void roadWithTurnAndVillageEndsTest() {

        Board board = new Board();

        // --- Tile definitions ---
        Tile village3Top = new Tile('W', new int[]{1, 1, 0, 1}); // N,E,W roads
        Tile straight = new Tile('U', new int[]{1, 0, 1, 0},1); // E-W straight
        Tile turn = new Tile('V', new int[]{0, 1, 1, 0},2); // W -> S
        Tile straightSouth = new Tile('U', new int[]{1, 0, 1, 0}); // N-S straight
        Tile village3Bottom = new Tile('W', new int[]{1, 1, 0, 1},1);

        // --- Place spots ---
        Spot s1 = board.getSpot(70, 70); // top village
        Spot s2 = board.getSpot(71, 70); // straight
        Spot s3 = board.getSpot(72, 70); // turn
        Spot s4 = board.getSpot(72, 69); // straight down
        Spot s5 = board.getSpot(72, 68); // bottom village

        // Assign tiles
        s1.setTile(village3Top);
        s2.setTile(straight);
        s3.setTile(turn);
        s4.setTile(straightSouth);
        s5.setTile(village3Bottom);

        // Update board spots
        board.updateSpots(s1);
        board.updateSpots(s2);
        board.updateSpots(s3);
        board.updateSpots(s4);
        board.updateSpots(s5);

        RoadPoints roadPoints = new RoadPoints(board);

        // Start scoring at top village, going EAST (side 1)
        int points = roadPoints.calculateRoadPoints(s1, 1);

        // We placed 5 road tiles → score = 5
        Assertions.assertEquals(5, points, "Road with 90° turn and two 3-road villages should be 5 tiles long");
    }
    @Test
    public void roadWithTurnAndSurroundingNonRoadTilesTest() {

        Board board = new Board();

        // --- Tile definitions ----
        Tile village3Top = new Tile('W', new int[]{1, 1, 0, 1});  // roads N,E,W
        Tile straightEW  = new Tile('U', new int[]{1, 0, 1, 0}, 1); // EW straight
        Tile turnEN      = new Tile('V', new int[]{0, 1, 1, 0}, 2); // E -> N
        Tile straightNS  = new Tile('U', new int[]{1, 0, 1, 0}); // NS straight
        Tile village3Bot = new Tile('W', new int[]{1, 1, 0, 1}, 1);

        // Non-road tiles (grass / city)
        Tile grassTile = new Tile('G', new int[]{0,0,0,0});
        Tile cityTile  = new Tile('C', new int[]{2,2,2,2});

        // --- Road tile positions ---
        Spot s1 = board.getSpot(70, 70);
        Spot s2 = board.getSpot(71, 70);
        Spot s3 = board.getSpot(72, 70);
        Spot s4 = board.getSpot(72, 69);
        Spot s5 = board.getSpot(72, 68);

        s1.setTile(village3Top);
        s2.setTile(straightEW);
        s3.setTile(turnEN);
        s4.setTile(straightNS);
        s5.setTile(village3Bot);

        // --- Surrounding tiles ---
        // Left of start
        Spot g1 = board.getSpot(69, 70);
        g1.setTile(grassTile);

        // Above the turn
        Spot g2 = board.getSpot(72, 67);
        g2.setTile(cityTile);

        // Right of the turn
        Spot g3 = board.getSpot(73, 70);
        g3.setTile(grassTile);

        // Left of bottom village
        Spot g4 = board.getSpot(71, 68);
        g4.setTile(cityTile);


        // --- Update board spots ---
        board.updateSpots(s1);
        board.updateSpots(s2);
        board.updateSpots(s3);
        board.updateSpots(s4);
        board.updateSpots(s5);

        board.updateSpots(g1);
        board.updateSpots(g2);
        board.updateSpots(g3);
        board.updateSpots(g4);

        RoadPoints roadPoints = new RoadPoints(board);

        // Start at top village, road going EAST = side 1
        int points = roadPoints.calculateRoadPoints(s1, 1);

        Assertions.assertEquals(
                5,
                points,
                "Road with 90° turn and village ends should score 5 even with non-road tiles around."
        );
    }
    @Test
    public void shortRoadTest() {
        Board board = new Board();
        Tile village3Top = new Tile('W', new int[]{1, 1, 0, 1});

        Spot s1 = board.getSpot(70, 70);
        Spot s2 = board.getSpot(71, 70);

        s1.setTile(village3Top);
        s2.setTile(village3Top);

        board.updateSpots(s1);
        board.updateSpots(s2);
        RoadPoints roadPoints = new RoadPoints(board);
        int points = roadPoints.calculateRoadPoints(s1, 1);

        Assertions.assertEquals(2,points,"Short road with 2 villages next to each other.");
    }
    @Test
    public void fourTileRoadLoopTest() {

        Board board = new Board();

        // Base turn tile (N, E, S, W) = road, road, grass, grass
        int[] turn = {0, 1, 1, 0};

        // Create 4 turn tiles with correct rotations
        Tile t1 = new Tile('V', turn.clone(), 0); // connects N→E
        Tile t2 = new Tile('V', turn.clone(), 3); // connects E→S
        Tile t3 = new Tile('V', turn.clone(), 2); // connects S→W
        Tile t4 = new Tile('V', turn.clone(), 1); // connects W→N

        // Layout:
        // (70,70) t1
        // (71,70) t2
        // (71,71) t3
        // (70,71) t4

        Spot s1 = board.getSpot(70, 70);
        Spot s2 = board.getSpot(71, 70);
        Spot s3 = board.getSpot(71, 71);
        Spot s4 = board.getSpot(70, 71);

        s1.setTile(t1);
        s2.setTile(t2);
        s3.setTile(t3);
        s4.setTile(t4);

        board.updateSpots(s1);
        board.updateSpots(s2);
        board.updateSpots(s3);
        board.updateSpots(s4);

        RoadPoints roadPoints = new RoadPoints(board);

        // Start at tile (70,70) going EAST
        int points = roadPoints.calculateRoadPoints(s1, 1);

        Assertions.assertEquals(
                4,
                points,
                "A 4-tile closed road loop should score 4 points."
        );
    }



}
