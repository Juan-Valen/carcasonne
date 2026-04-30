|  ID  | Product Backlog Item  |
|:----:|:----:|
| PB‑01 | Player can place a tile to the grid |
| PB‑02 | Player can rotate the tile to any orientation |
| PB‑03 | Player can see all placeable cells in the grid |
| PB‑04 | Player can see how many Meeples they have |
| PB‑05 | Player can create an account |
| PB‑06 | Player can save a game state |
| PB‑07 | Player can load a game state |
| PB‑08 | Player can place a Meeple into the tile |
| PB‑09 | Player can login |
| PB‑10 | Player can finish the game and see the results |

# Functional Acceptance Tests

## AT‑F‑01: Tile placement
Related Backlog: PB‑01.

Description:
Player can place a tile to the grid.

Preconditions:
- The app has been launched.

Test Steps:
1. Press "New Game".
2. Select yellow cells.

Expected Result:
1. Redirected to game screen.
2. Tile is placed in the clicked cell.

## AT‑F‑02: Tile rotation
Related Backlog: PB‑02.

Description:
Player can rotate the tile to any orientation.

Test Steps:
1. Press "New Game".
2. Press "Rotate tile".
3. Press "Rotate tile".
4. Press "Rotate tile".
5. Press "Rotate tile".

Expected Result:
1. Redirected to game screen.
2. Tile rotates 90 degrees clockwise to orientation 90.
3. Tile rotates 90 degrees clockwise to orientation 180.
4. Tile rotates 90 degrees clockwise to orientation 270.
5. Tile rotates 90 degrees clockwise back to orientation 0.



## AT‑F‑03: 
Related Backlog: PB‑03.

Description:
Player can see all placeable cells in the grid.

Preconditions:
- The app has been launched.

Test Steps:
1. Press “New Game”.
2. Place a tile.


Expected Result:
1. Redirected to game screen.
2. yelllow cells indicating placeable spots appear.



## AT‑F‑04: 
Related Backlog: PB‑04.

Description:
Player can see how many Meeples they have.

Preconditions:
- The app has been launched.

Test Steps:
1. Press “New Game”.


Expected Result:
1. Redirected to game screen and there are colored circles indicating the amount of Meeples

## AT‑F‑05: 
Related Backlog: PB‑05.

Description:
Player can create an account.

Preconditions:
- The app has been launched.

Test Steps:
1. Press “Login”.
2. Press “create new account”.
3. Enter username “user123”.
4. Enter password “UserPass123”.
5. Press “Create”.


Expected Result:
1. Redirected to Login screen.
2. Create new account screen will be shown.
3. Username input cell displays “user123”.
4. Password input cell displays “UserPass123”.
5. Redirected to Main menu screen while login in with the new user.


## AT‑F‑06: Save game
Related Backlog: PB‑06.

Description:
Player can save a game state.

Preconditions:
- The app has been launched.

Test Steps:
1. Press “Login”.
2. Enter username “carcassonne”.
3. Enter password “password”.
4. Press “Login”.
5. Press “New Game”.
6. Press yellow cell.
7. Press “save & quit”.
8. Press “Saved Games”.


Expected Result:
1. Redirected to Login screen
2. Username input cell displays “carcassonne”.
3. Password input cell displays “password”.
4. Redirected to Main menu screen.
5. Redirected to game screen.
6. Tile is placed in the grid.
7. Redirected to Main menu screen.
8. Redirected to screen with the previously saved game.


## AT‑F‑07: Load game
Related Backlog: PB‑07.

Description:
Player can load a game state.

Preconditions:
- The app has been launched.

Test Steps:
1. Press “Login”.
2. Enter username “carcassonne”.
3. Enter password “password”.
4. Press “Login”.
5. Press “New Game”.
6. Press yellow cell.
7. Press “save & quit”.
8. Press “Saved Games”.
9. Press on the game with id “1”.


Expected Result:
1. Redirected to Login screen.
2. Username input cell displays “carcassonne”.
3. Password input cell displays “password”.
4. Redirected to Main menu screen.
5. Redirected to game screen.
6. Tile is placed in the grid.
7. Redirected to Main menu screen.
8. Redirected to screen with the previously saved game.
9. Redirected to game screen and player can see the same tile that was placed previously.


## AT‑F‑08: Place Meeple
Related Backlog: PB‑08.

Description:
Player can place a Meeple into the tile.

Preconditions:
- The app has been launched.

Test Steps:
1. Press “New Game”
2. Press on a colored circle in the tile
3. Press yellow cell


Expected Result:
1. Redirected to game screen
2. The pressed circle changes to a brighter color indicating that a Meeple has been placed there
3. Tile is placed in the grid with the Meeple been shown as a colored circle and player Meeple count is reduced


## AT‑F‑09: Login
Related Backlog: PB‑09.

Description:
Player can login.

Preconditions:
- The app has been launched.

Test Steps:
1. Press “Login”
2. Enter username “carcassonne”
3. Enter password “password”
4. Press “Login”


Expected Result:
1. Redirected to Login screen
2. Username input cell displays “carcassonne”
3. Password input cell displays “password”
4. Redirected to Main menu screen and “Change user” button appears


## AT‑F‑10: 
Related Backlog: PB‑10.

Description:
Player can finish the game and see the results.

Preconditions:
- The app has been launched.

Test Steps:
1. Press “New Game”.
2. Press yellow cell or “rotate tile” button if yellow cells aren’t shown on the grid.


Expected Result:
1. Redirected to game screen.
2. Tile is placed in the grid or last tile is placed and Score screen appears.


# Metrics
## Results
### Results 23.4.2026

| ID     |                     Test Case                      | Test engineer 1 | Test engineer 2 | Test engineer 3 | Test engineer 4 |
|:------:|:--------------------------------------------------:|:---------------:|:---------------:|:---------------:|:---------------:|
| PB‑01  |  Player can place a tile to the grid               |      PASS       |      PASS       |      PASS       |      PASS       |
| PB‑02  |  Player can rotate the tile to any orientation     |      PASS       |      PASS       |      PASS       |      PASS       |
| PB‑03  |  Player can see all placeable cells in the grid    |     FAILED      |      PASS       |      PASS       |      PASS       |
| PB‑04  |  Player can see how many Meeples they have         |      PASS       |      PASS       |      PASS       |      PASS       |
| PB‑05  |  Player can create an account                      |     FAILED      |     FAILED      |     FAILED      |     FAILED      |
| PB‑06  |  Player can save a game state                      |      PASS       |      PASS       |      PASS       |      PASS       |
| PB‑07  |  Player can load a game state                      |      PASS       |      PASS       |      PASS       |      PASS       |
| PB‑08  |  Player can place a Meeple into the tile           |      PASS       |      PASS       |      PASS       |      PASS       |
| PB‑09  |  Player can login                                  |     FAILED      |      PASS       |      PASS       |      PASS       |
| PB‑10  |  Player can finish the game and see the results    |      PASS       |      PASS       |      PASS       |     FAILED      |
