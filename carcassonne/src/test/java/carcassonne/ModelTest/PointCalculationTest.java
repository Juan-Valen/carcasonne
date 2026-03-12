package carcassonne.ModelTest;

import carcassonne.DataType.TileSide;
import carcassonne.Model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

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

        Tile roadTileEW = new Tile('U', tiles.get('U'), 1);
        Tile village = new Tile('X', tiles.get('X'));

        Board board = new Board();

        board.updateSpots(70, 70, village);
        board.updateSpots(71, 70, roadTileEW);
        board.updateSpots(72, 70, roadTileEW);
        board.updateSpots(73, 70, village);

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

        Tile straightEW = new Tile('U', tiles.get('U'), 1);

        // Turn tile V (base shape: connects S→E)
        Tile turn = new Tile('V', tiles.get('V'), 3);

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

        Tile t1 = new Tile(('V'), tiles.get('V'), 2);
        Tile t2 = new Tile(('V'), tiles.get('V'), 3);
        Tile t3 = new Tile(('V'), tiles.get('V'), 0);
        Tile t4 = new Tile(('V'), tiles.get('V'), 1);

        // Loop layout:
        //  t4 → t3
        //  ↑    ↓
        //  t1 ← t2
        board.updateSpots(70, 70, t1);
        board.updateSpots(71, 70, t2);
        board.updateSpots(71, 69, t3);
        board.updateSpots(70, 69, t4);

        RoadPoints rp = new RoadPoints(board);

        int points = rp.calculateRoadPoints(70, 70, 1); // EAST

        Assertions.assertEquals(
                4,
                points,
                "A 4‑tile closed loop should return 4 points."
        );
    }

    @Test
    public void cityPointsTest() {
        Board board = new Board();
        // Center tile: all sides city, also has a shield (C is in bonusPoint set)
        Tile allCity = new Tile('C', tiles.get('C'));

        // Four distinct L tiles with correct orientations so that their city side faces the center
        Tile cityN = new Tile('L', tiles.get('L'), 0); // (70,71) needs CITY on its north side (0)
        Tile cityW = new Tile('L', tiles.get('L'), 1); // (71,70) needs CITY on its west side (3) → orientation 1
        Tile cityS = new Tile('L', tiles.get('L'), 2); // (70,69) needs CITY on its south side (2) → orientation 2
        Tile cityE = new Tile('L', tiles.get('L'), 3); // (69,70) needs CITY on its east side  (1) → orientation 3

        // Place center
        board.updateSpots(70, 70, allCity);

        // Place caps around the center
        board.updateSpots(70, 71, cityN); // below center; its NORTH connects to center's SOUTH
        board.updateSpots(71, 70, cityW); // right of center; its WEST connects to center's EAST
        board.updateSpots(70, 69, cityS); // above center; its SOUTH connects to center's NORTH
        board.updateSpots(69, 70, cityE); // left of center; its EAST connects to center's WEST

        CityPoints cp = new CityPoints(board);

        // Start from the bottom cap, going NORTH into the city
        int points = cp.calculateCityPoints(70, 71, 0);

        Assertions.assertEquals(
                12,
                points,
                "A all city tile with caps all around"
        );
    }

    @Test
    public void tubeCityPointsTest() {
        Board board = new Board();
        Tile tubeCity = new Tile('G', tiles.get('G'), 1);
        Tile cityN = new Tile('L', tiles.get('L'));
        Tile cityS = new Tile('L', tiles.get('L'), 2);

        board.updateSpots(70, 70, tubeCity);
        board.updateSpots(70, 71, cityN);
        board.updateSpots(70, 69, cityS);
        CityPoints cp = new CityPoints(board);
        int points = cp.calculateCityPoints(70, 71, 0);
        Assertions.assertEquals(
                6,
                points,
                "A tube city with 2 caps on it"
        );
    }

    @Test
    public void monasteryCompleteScoresNine() {
        Board board = new Board();

        // Place monastery at center (use A or B)
        Tile monastery = new Tile('A', tiles.get('A'));
        board.updateSpots(70, 70, monastery);

        // Surround with 8 tiles (any tiles count as placed neighbors)
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue; // skip center
                board.updateSpots(70 + dx, 70 + dy, new Tile('A', tiles.get('A')));
            }
        }

        MonasteryPoints mp = new MonasteryPoints(board);
        int points = mp.calculateMonasteryPoints(70, 70);

        Assertions.assertEquals(9, points, "Complete monastery should score 9 points.");

    }

    @Test
    public void monasteryIncompleteScoresZero() {
        Board board = new Board();

        // Place monastery at center
        Tile monastery = new Tile('B', tiles.get('B'));
        board.updateSpots(50, 50, monastery);

        // Place only some neighbors (e.g., 5 of 8)
        board.updateSpots(49, 49, new Tile('A', tiles.get('A'))); // NW
        board.updateSpots(50, 49, new Tile('A', tiles.get('A'))); // N
        board.updateSpots(51, 49, new Tile('A', tiles.get('A'))); // NE
        board.updateSpots(49, 50, new Tile('A', tiles.get('A'))); // W
        board.updateSpots(51, 50, new Tile('A', tiles.get('A'))); // E
        // Missing SW, S, SE

        MonasteryPoints mp = new MonasteryPoints(board);
        int points = mp.calculateMonasteryPoints(50, 50);

        Assertions.assertEquals(0, points, "Incomplete monastery should score 0 points (classic mid-game).");
    }

    @Test
    public void roadResultTracksSingleMajorityWinner() {
        Board board = new Board();

        Tile villageStart = new Tile('X', tiles.get('X'));
        Meeple playerOneMeepleA = new Meeple(1);
        playerOneMeepleA.setPosition(1);
        villageStart.setMeeple(playerOneMeepleA);

        Tile roadTileEW1 = new Tile('U', tiles.get('U'), 1);
        Meeple playerOneMeepleB = new Meeple(1);
        playerOneMeepleB.setPosition(1);
        roadTileEW1.setMeeple(playerOneMeepleB);

        Tile roadTileEW2 = new Tile('U', tiles.get('U'), 1);
        Meeple playerTwoMeeple = new Meeple(2);
        playerTwoMeeple.setPosition(3);
        roadTileEW2.setMeeple(playerTwoMeeple);

        Tile villageEnd = new Tile('X', tiles.get('X'));

        board.updateSpots(70, 70, villageStart);
        board.updateSpots(71, 70, roadTileEW1);
        board.updateSpots(72, 70, roadTileEW2);
        board.updateSpots(73, 70, villageEnd);

        RoadPoints roadPoints = new RoadPoints(board);
        RoadPoints.RoadResult result = roadPoints.calculateRoadResult(70, 70, 1);

        Assertions.assertEquals(4, result.getPoints());
        Assertions.assertEquals(Set.of(1), result.getWinnerPlayerIndices());
    }

    @Test
    public void roadResultTracksTiedMajorityWinners() {
        Board board = new Board();

        Tile villageStart = new Tile('X', tiles.get('X'));
        Meeple playerZeroMeeple = new Meeple(0);
        playerZeroMeeple.setPosition(1);
        villageStart.setMeeple(playerZeroMeeple);

        Tile roadTileEW1 = new Tile('U', tiles.get('U'), 1);
        Tile roadTileEW2 = new Tile('U', tiles.get('U'), 1);

        Tile villageEnd = new Tile('X', tiles.get('X'));
        Meeple playerTwoMeeple = new Meeple(2);
        playerTwoMeeple.setPosition(3);
        villageEnd.setMeeple(playerTwoMeeple);

        board.updateSpots(70, 70, villageStart);
        board.updateSpots(71, 70, roadTileEW1);
        board.updateSpots(72, 70, roadTileEW2);
        board.updateSpots(73, 70, villageEnd);

        RoadPoints roadPoints = new RoadPoints(board);
        RoadPoints.RoadResult result = roadPoints.calculateRoadResult(70, 70, 1);

        Assertions.assertEquals(4, result.getPoints());
        Assertions.assertEquals(Set.of(0, 2), result.getWinnerPlayerIndices());
    }

    @Test
    public void cityResultTracksSingleMajorityWinner() {
        Board board = new Board();

        Tile center = new Tile('C', tiles.get('C'));
        Meeple p1CenterMeeple = new Meeple(1);
        p1CenterMeeple.setPosition(0);
        center.setMeeple(p1CenterMeeple);

        Tile cityN = new Tile('L', tiles.get('L'), 0);
        Meeple p1NorthMeeple = new Meeple(1);
        p1NorthMeeple.setPosition(0);
        cityN.setMeeple(p1NorthMeeple);

        Tile cityW = new Tile('L', tiles.get('L'), 1);
        Tile cityS = new Tile('L', tiles.get('L'), 2);
        Tile cityE = new Tile('L', tiles.get('L'), 3);
        Meeple p2SouthMeeple = new Meeple(2);
        p2SouthMeeple.setPosition(2);
        cityS.setMeeple(p2SouthMeeple);

        board.updateSpots(70, 70, center);
        board.updateSpots(70, 71, cityN);
        board.updateSpots(71, 70, cityW);
        board.updateSpots(70, 69, cityS);
        board.updateSpots(69, 70, cityE);

        CityPoints cityPoints = new CityPoints(board);
        CityPoints.CityResult result = cityPoints.calculateCityResult(70, 71, 0);

        Assertions.assertEquals(12, result.getPoints());
        Assertions.assertEquals(Set.of(1), result.getWinnerPlayerIndices());
    }

    @Test
    public void cityResultTracksTiedMajorityWinners() {
        Board board = new Board();

        Tile center = new Tile('C', tiles.get('C'));
        Meeple p0CenterMeeple = new Meeple(0);
        p0CenterMeeple.setPosition(0);
        center.setMeeple(p0CenterMeeple);

        Tile cityN = new Tile('L', tiles.get('L'), 0);
        Tile cityW = new Tile('L', tiles.get('L'), 1);
        Tile cityS = new Tile('L', tiles.get('L'), 2);
        Tile cityE = new Tile('L', tiles.get('L'), 3);
        Meeple p2EastMeeple = new Meeple(2);
        p2EastMeeple.setPosition(1);
        cityE.setMeeple(p2EastMeeple);

        board.updateSpots(70, 70, center);
        board.updateSpots(70, 71, cityN);
        board.updateSpots(71, 70, cityW);
        board.updateSpots(70, 69, cityS);
        board.updateSpots(69, 70, cityE);

        CityPoints cityPoints = new CityPoints(board);
        CityPoints.CityResult result = cityPoints.calculateCityResult(70, 71, 0);

        Assertions.assertEquals(12, result.getPoints());
        Assertions.assertEquals(Set.of(0, 2), result.getWinnerPlayerIndices());
    }
}
