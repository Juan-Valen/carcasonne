package carcassonne.ModelTest;

import carcassonne.DataType.TileSide;
import carcassonne.Model.Board;
import carcassonne.Model.RoadPoints;
import carcassonne.Model.Tile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class PointCalculationTest {
    private static Map<Character, TileSide[]> tiles;
    @BeforeAll
    static void initAll() {
        tiles = Map.ofEntries(
                Map.entry('A', new TileSide[]{TileSide.FIELD, TileSide.FIELD, TileSide.ROAD, TileSide.FIELD}),
                Map.entry('B', new TileSide[]{TileSide.FIELD, TileSide.FIELD, TileSide.FIELD, TileSide.FIELD}),
                Map.entry('C', new TileSide[]{TileSide.CITY, TileSide.CITY, TileSide.CITY, TileSide.CITY}),
                Map.entry('D', new TileSide[]{TileSide.CITY, TileSide.ROAD, TileSide.FIELD, TileSide.ROAD}),
                Map.entry('E', new TileSide[]{TileSide.CITY, TileSide.FIELD, TileSide.FIELD, TileSide.FIELD}),
                Map.entry('F', new TileSide[]{TileSide.FIELD, TileSide.CITY, TileSide.FIELD, TileSide.CITY}),
                Map.entry('G', new TileSide[]{TileSide.FIELD, TileSide.CITY, TileSide.FIELD, TileSide.CITY}),
                Map.entry('H', new TileSide[]{TileSide.CITY, TileSide.FIELD, TileSide.CITY, TileSide.FIELD}),
                Map.entry('I', new TileSide[]{TileSide.CITY, TileSide.CITY, TileSide.FIELD, TileSide.FIELD}),
                Map.entry('J', new TileSide[]{TileSide.CITY, TileSide.ROAD, TileSide.ROAD, TileSide.FIELD}),
                Map.entry('K', new TileSide[]{TileSide.CITY, TileSide.FIELD, TileSide.ROAD, TileSide.ROAD}),
                Map.entry('L', new TileSide[]{TileSide.CITY, TileSide.ROAD, TileSide.ROAD, TileSide.ROAD}),
                Map.entry('M', new TileSide[]{TileSide.CITY, TileSide.CITY, TileSide.FIELD, TileSide.FIELD}),
                Map.entry('N', new TileSide[]{TileSide.CITY, TileSide.CITY, TileSide.FIELD, TileSide.FIELD}),
                Map.entry('O', new TileSide[]{TileSide.CITY, TileSide.ROAD, TileSide.ROAD, TileSide.CITY}),
                Map.entry('P', new TileSide[]{TileSide.CITY, TileSide.ROAD, TileSide.ROAD, TileSide.CITY}),
                Map.entry('Q', new TileSide[]{TileSide.CITY, TileSide.CITY, TileSide.FIELD, TileSide.CITY}),
                Map.entry('R', new TileSide[]{TileSide.CITY, TileSide.CITY, TileSide.FIELD, TileSide.CITY}),
                Map.entry('S', new TileSide[]{TileSide.CITY, TileSide.CITY, TileSide.ROAD, TileSide.CITY}),
                Map.entry('T', new TileSide[]{TileSide.CITY, TileSide.CITY, TileSide.ROAD, TileSide.CITY}),
                Map.entry('U', new TileSide[]{TileSide.ROAD, TileSide.FIELD, TileSide.ROAD, TileSide.FIELD}),
                Map.entry('V', new TileSide[]{TileSide.FIELD, TileSide.FIELD, TileSide.ROAD, TileSide.ROAD}),
                Map.entry('W', new TileSide[]{TileSide.FIELD, TileSide.ROAD, TileSide.ROAD, TileSide.ROAD}),
                Map.entry('X', new TileSide[]{TileSide.ROAD, TileSide.ROAD, TileSide.ROAD, TileSide.ROAD}));
    }

    @Test
    public void roadPointCalculationTest() {

        Tile roadTileEW = new Tile('U', tiles.get('U'),1);
        Tile village = new Tile('X', tiles.get('X'));

        Board board = new Board();

        board.updateSpots(70, 70,village);
        board.updateSpots(71, 70,roadTileEW);
        board.updateSpots(72,70,roadTileEW);
        board.updateSpots(73,70,village);

        RoadPoints rp = new RoadPoints(board);

        int points = rp.calculateRoadPoints(70, 70, 1); // EAST side of start

        Assertions.assertEquals(4, points);
    }


    @Test
    public void roadWithTurnAndVillageEndsTest() {

        Board board = new Board();

        // --- Tile definitions ---

        // Village tile W (roads N/E/W)
        Tile villageTop = new Tile('W', tiles.get('W'));
        Tile villageBottom = new Tile('W', tiles.get('W'));

        Tile straightEW = new Tile('U', tiles.get('U'),1);

        // Turn tile V (base shape: connects S→E)
        Tile turn = new Tile('V', tiles.get('V'));
        // To get E→S, rotate once
        turn.rotateTile();

        // Straight NORTH-SOUTH = U tile rotated twice
        Tile straightNS = new Tile('U', tiles.get('U'));
        straightNS.rotateTile();
        straightNS.rotateTile();

        // --- Place tiles using new updateSpots ---
        board.updateSpots(70, 70, villageTop);     // top village
        board.updateSpots(71, 70, straightEW);     // east-west
        board.updateSpots(72, 70, turn);           // turn down
        board.updateSpots(72, 69, straightNS);     // vertical road down
        board.updateSpots(72, 68, villageBottom);  // bottom village

        RoadPoints roadPoints = new RoadPoints(board);

        // Start scoring at top village tile, EAST side
        int points = roadPoints.calculateRoadPoints(70, 70, 1);

        Assertions.assertEquals(
                5,
                points,
                "Road with 90° turn and two 3-road villages should be 5 tiles long"
        );
    }
    @Test
    public void roadWithTurnAndSurroundingNonRoadTilesTest() {

        Board board = new Board();

        // --- Correct tile definitions ---
        Tile villageTop = new Tile('W', tiles.get('W'));
        Tile villageBottom = new Tile('W', tiles.get('W'));

        // Straight east-west: U tile rotated once
        Tile straightEW = new Tile('U', tiles.get('U'));
        straightEW.rotateTile(); // EW

        // Turn V: base connects S→E, rotate once to get E→S
        Tile turnEN = new Tile('V', tiles.get('V'));
        turnEN.rotateTile(); // E → S

        // Straight north-south: rotate EW twice
        Tile straightNS = new Tile('U', tiles.get('U'));
        straightNS.rotateTile();
        straightNS.rotateTile();

        // Non-road filler tiles
        Tile grassTile = new Tile('G', new TileSide[]{
                TileSide.FIELD, TileSide.FIELD, TileSide.FIELD, TileSide.FIELD
        });
        Tile cityTile = new Tile('C', new TileSide[]{
                TileSide.CITY, TileSide.CITY, TileSide.CITY, TileSide.CITY
        });

        // --- Place road path tiles ---
        board.updateSpots(70, 70, villageTop);
        board.updateSpots(71, 70, straightEW);
        board.updateSpots(72, 70, turnEN);
        board.updateSpots(72, 69, straightNS);
        board.updateSpots(72, 68, villageBottom);

        // --- Surrounding filler tiles ---
        board.updateSpots(69, 70, grassTile); // left of start
        board.updateSpots(72, 67, cityTile);  // above the downward road
        board.updateSpots(73, 70, grassTile); // right of turn
        board.updateSpots(71, 68, cityTile);  // left of bottom village

        RoadPoints rp = new RoadPoints(board);

        int points = rp.calculateRoadPoints(70, 70, 1); // EAST

        Assertions.assertEquals(
                5,
                points,
                "Road with 90° turn and village ends should be 5 tiles long."
        );
    }
    @Test
    public void shortRoadTest() {

        Board board = new Board();

        Tile village = new Tile('W', tiles.get('W'));

        board.updateSpots(70, 70, village);
        board.updateSpots(71, 70, village);
        board.updateSpots(72, 70, village);

        RoadPoints rp = new RoadPoints(board);

        int points = rp.calculateRoadPoints(70, 70, 1);

        Assertions.assertEquals(
                2,
                points,
                "Short road with two villages should return 2."
        );
    }

    @Test
    public void fourTileRoadLoopTest() {

        Board board = new Board();

        TileSide[] baseTurn = tiles.get('V');

        Tile t1 = new Tile('V', baseTurn,3); // original orientation: S → E
        Tile t2 = new Tile('V', baseTurn);              // E → S
        Tile t3 = new Tile('V', baseTurn, 1); // S → W
        Tile t4 = new Tile('V', baseTurn,2);  // W → N

        // Loop layout:
        //  t1 → t2
        //   ↑     ↓
        //  t4 ← t3
        board.updateSpots(70, 70, t1);
        board.updateSpots(71, 70, t2);
        board.updateSpots(71, 71, t3);
        board.updateSpots(70, 71, t4);

        RoadPoints rp = new RoadPoints(board);

        int points = rp.calculateRoadPoints(70, 70, 1); // EAST

        Assertions.assertEquals(
                4,
                points,
                "A 4‑tile closed loop should return 4 points."
        );
    }
}
