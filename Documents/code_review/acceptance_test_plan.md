|  ID  | Product Backlog Item  |
|:----:|:----:|
| PB‑01 | Tiles can be created using modular tile superclass |
| PB‑02 | Tiles can be added to a backend grid |
| PB‑03 | Game checks which grid spaces a tile can be placed
on |
| PB‑04 | Frontend receives placeable tiles from backend |
| PB‑05 | Frontend displays the current grid |
| PB‑06 | Frontend displays the next tile |
| PB‑07 | Next tile can be rotated from frontend |
| PB‑08 | Frontend main menu |
| PB‑09 | Game state can be saved |
| PB‑10 | Game state can be saved to database |

# Functional Acceptance Tests

## AT‑F‑01: Modular Tile Creation
Related Backlog: PB‑01.

Description:
Verify that tiles can be instantiated using the modular tile superclass.

Preconditions:
- Tile superclass is implemented

Test Steps:
1. Create a new tile instance from the superclass
2. Assign specific tile attributes (shape, orientation)

Expected Result:
- Tile instance is successfully created
- Tile properties match assigned values
## AT‑F‑02: Add Tile to Backend Grid
Related Backlog: PB‑02.

Description:
Verify tiles can be added to the backend game grid.

Test Steps:
1. Select a valid grid position
2. Submit tile placement to backend

Expected Result:
- Tile is added to the backend grid
- Grid state updates correctly

## AT‑F‑03: Validate Placeable Grid Spaces
Related Backlog: PB‑03.

Description:
Verify the game correctly determines valid placement locations.

Test Steps:
1. Select a tile
2. Request valid grid positions from backend

Expected Result:
- Only valid grid spaces are returned
- Invalid placements are excluded
## AT‑F‑04: Frontend Receives Valid Placement Data
Related Backlog: PB‑04.

Description:
Verify backend sends placeable tile positions to frontend.

Test Steps:
1. Request placement options from frontend
2. Backend responds with valid positions

Expected Result:
- Frontend receives correct placement data
- Data matches backend validation
## AT‑F‑06: Display Current Grid
Related Backlog: PB‑05.

Description:
Verify the frontend displays the updated game grid.

Expected Result:
- Grid matches backend state
- Tile positions render correctly
## AT‑F‑07: Display Next Tile
Related Backlog: PB‑06.

Description:
Verify the next tile is visible to the player.

Expected Result:
- Next tile appears in designated UI area
- Displayed tile matches backend queue
## AT‑F‑08: Rotate Next Tile
Related Backlog: PB‑07.

Description:
Verify the player can rotate the next tile.

Test Steps:
1. Select rotate action
2. Observe tile orientation

Expected Result:
- Tile rotation updates correctly
- Rotation is reflected visually
## AT‑F‑09: Main Menu Navigation
Related Backlog: PB‑08.

Description:
Verify frontend main menu functionality.

Expected Result:
- Menu loads successfully
- Navigation options respond correctly
## AT‑F‑10: Save Game State
Related Backlog: PB‑09.

Description:
Verify the game state can be saved.

Expected Result:
- Current grid, score, and tile state are captured
## AT‑F‑11: Save Game State to Database
Related Backlog: PB‑10.

Description:
Verify saved game state persists in database.

Expected Result:
- Game state is retrievable after reload
- No data loss occurs
# Metrics
## AT‑P‑01: Tile Placement Response Time
Related Backlog: PB‑02, PB‑03.

Expected Result:
- Placement validation occurs within 1 second
## AT‑P‑02: Frontend‑Backend Sync
Related Backlog: PB‑04, PB‑05.

Expected Result:
- No visible desynchronization between views
## AT‑P‑03: Game State Persistence
Related Backlog: PB‑09, PB‑10.

Expected Result:
- Saved games load consistently without corruption
