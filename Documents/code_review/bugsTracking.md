# Bugs
## Summary

| Category | Count |
|:--------:|:-----:|
|Total Bugs |	61 |
|High Priority |	2 |
|Medium Priority |	59  |
|EI_EXPOSE_REP (Mutable object exposed via getter) |	10 |
|EI_EXPOSE_REP2 (Mutable object stored in field) |	12 |
|NP_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD |	9 |
|UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD |	9 |
|DLS_DEAD_LOCAL_STORE |	3 |
|RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT |	3 |
|SING_SINGLETON_GETTER_NOT_SYNCHRONIZED |	2 |
|MS_EXPOSE_REP |	2 |
|URF_UNREAD_FIELD |	2 |
|Other |	9 |


## Bug list
| Bug ID | Description | Importance  | Status | Assigned |
|:------:|:------------|:-----------:|:------:|:--------:|
| UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD	| Unwritten public or protected field: carcassonne.View.LoginView.btnBack — LoginView.java line 25 | Medium	|Done	| Juan |
| UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD	|	Unwritten public or protected field: carcassonne.View.LoginView.btnLogin — LoginView.java line 26 |	Medium	|	Done	|	Juan	|
| UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD	|	Unwritten public or protected field: carcassonne.View.LoginView.errorLabel — LoginView.java line 45 |	Medium	|	Done	|	Juan	|
| NP_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD |	Read of unwritten public or protected field langComboBox in carcassonne.View.StartView.initialize() — StartView.java line 37 |	Medium	|	Done	|	Nooa	|
| NP_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD |	Read of unwritten public or protected field loginButton in carcassonne.View.StartView.onViewShow() — StartView.java line 89	|	Medium	|	Done	|	Nooa	|
| NP_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD |	Read of unwritten public or protected field btnNewGame in carcassonne.View.StartView.setLang() — StartView.java line 127	|	Medium	|	Done	|	Noah	|
| NP_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD |	Read of unwritten public or protected field langComboBox in carcassonne.View.StartView.setLang() — StartView.java line 139	|	Medium	|	Done	|	Noah	|
| NP_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD |	Read of unwritten public or protected field loginButton in carcassonne.View.StartView.setLang() — StartView.java line 125	|	Medium	|	Done	|	Noah	|
| UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD |	Unwritten public or protected field: carcassonne.View.StartView.btnNewGame — StartView.java line 127	|	Medium	|	Done	|	Raphael	|
| UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD |	Unwritten public or protected field: carcassonne.View.StartView.langComboBox — StartView.java line 37	|	Medium	|	Done	|	Raphael	|
| UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD |	Unwritten public or protected field: carcassonne.View.StartView.loginButton — StartView.java line 89	|	Medium	|	Done	|	Raphael	|
| ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD |	Writing to a static field from an instance method: static field carcassonne.App.instance is written from instance method new carcassonne.App() — App.java line 22 |	High |	Open |	Raphael |
| EI_EXPOSE_REP |	A method may expose its internal representation by returning a reference to a mutable object: method carcassonne.Controller.GameController.getCurrentUser() may expose internal representation by returning carcassonne.Controller.GameController.currentUser — GameController.java line 271 |	Medium |	Open |	Raphael |
| EI_EXPOSE_REP2 |	A method exposes its internal representation by storing a reference to a mutable external object: method carcassonne.Controller.GameController.setCurrentUser(User) may expose internal representation by storing mutable external object in carcassonne.Controller.GameController.currentUser — GameController.java line 267 |	Medium |	Open |	Raphael |
| EI_EXPOSE_REP2 |	A method exposes its internal representation by storing a reference to a mutable external object: method carcassonne.Controller.GameController.setView(GameView) may expose internal representation by storing mutable external object in carcassonne.Controller.GameController.view — GameController.java line 36 |	Medium |	Open |	Raphael |
| MS_EXPOSE_REP |	Public static method may expose internal representation by returning a mutable object or array: static method carcassonne.Controller.GameController.getInstance() may expose internal representation by returning carcassonne.Controller.GameController.instance — GameController.java line 32 |	Medium |	Open |	Raphael |
| SING_SINGLETON_GETTER_NOT_SYNCHRONIZED |	Instance-getter method of class using singleton design pattern is not synchronized (carcassonne.Controller.GameController) — GameController.java lines 29-32 |	Medium-High |	Open |	Nooa |
| BC_VACUOUS_INSTANCEOF |	instanceof will always return true in carcassonne.DataTypeTest.ColorTest.testSerializable(), since every java.io.Serializable is an instance of ColorTest.java line 58 |	Medium |	Open |	Noah |
| EI_EXPOSE_REP |	A method may expose its internal representation by returning a reference to a mutable object: method carcassonne.Model.AvailableSpots.getSpots() may expose internal representation by returning carcassonne.Model.AvailableSpots.spots — AvailableSpots.java line 24 |	Medium |	Open |	Juan |
| DLS_DEAD_LOCAL_STORE |	Dead store to a local variable in method new carcassonne.Model.Board(): local variable 'sp' assigned but never used — Board.java line 34 |	Medium |	Open |	Juan |
| EI_EXPOSE_REP |	A method may expose its internal representation by returning a reference to a mutable object: method carcassonne.Model.Board.getFreeSpots() may expose internal representation by returning carcassonne.Model.Board.freeSpots — Board.java line 45 |	Medium |	Open |	Juan |
| EI_EXPOSE_REP |	A method may expose its internal representation by returning a reference to a mutable object: method carcassonne.Model.Board.getMaxSpot() may expose internal representation by returning carcassonne.Model.Board.maxSpot — Board.java line 147 |	Medium |	Open |	Juan |
| EI_EXPOSE_REP |	A method may expose its internal representation by returning a reference to a mutable object: method carcassonne.Model.Board.getMinSpot() may expose internal representation by returning carcassonne.Model.Board.minSpot — Board.java line 143 |	Medium |	Open |	Juan |
| EI_EXPOSE_REP2 |	A method exposes its internal representation by storing a reference to a mutable external object: constructor new carcassonne.Model.CityPoints(Board) may expose internal representation by storing mutable external object in carcassonne.Model.CityPoints.board — CityPoints.java line 41 |	Medium |	Open |	Juan |
| DLS_DEAD_LOCAL_STORE |	Dead store to a local variable in method carcassonne.Model.Game.calculatePoints(int, int): local variable 'monasteryPoints' assigned but never used — Game.java line 194 |	High |	Open |	Noah |
| EI_EXPOSE_REP |	A method may expose its internal representation by returning a reference to a mutable object: method carcassonne.Model.Game.getBoard() may expose internal representation by returning carcassonne.Model.Game.board — Game.java line 106 |	Medium |	Open |	Nooa |
| EI_EXPOSE_REP |	A method may expose its internal representation by returning a reference to a mutable object: method carcassonne.Model.Game.getPlayers() may expose internal representation by returning carcassonne.Model.Game.players — Game.java line 215 |	Medium |	Open |	Nooa |
| EI_EXPOSE_REP2 |	A method exposes its internal representation by storing a reference to a mutable external object: constructor new carcassonne.Model.MonasteryPoints(Board) may expose internal representation by storing mutable external object in carcassonne.Model.MonasteryPoints.board — MonasteryPoints.java line 11 |	Medium |	Open |	Raphael |
| EI_EXPOSE_REP |	A method may expose its internal representation by returning a reference to a mutable object: method carcassonne.Model.Player.getUser() may expose internal representation by returning carcassonne.Model.Player.user — Player.java line 27 |	Medium |	Open |	Raphael |
| EI_EXPOSE_REP2 |	A method exposes its internal representation by storing a reference to a mutable external object: constructor new carcassonne.Model.Player(User) may expose internal representation by storing mutable external object in carcassonne.Model.Player.user — Player.java line 16 |	Medium |	Open |	Raphael |
| EI_EXPOSE_REP2 |	A method exposes its internal representation by storing a reference to a mutable external object: constructor new carcassonne.Model.Player(User, int) may expose internal representation by storing mutable external object in carcassonne.Model.Player.user — Player.java line 21 |	Medium |	Open |	Raphael |
| EI_EXPOSE_REP2 |	A method exposes its internal representation by storing a reference to a mutable external object: constructor new carcassonne.Model.RoadPoints(Board) may expose internal representation by storing mutable external object in carcassonne.Model.RoadPoints.board — RoadPoints.java line 28 |	Medium |	Open |	Raphael |
| EI_EXPOSE_REP |	A method may expose its internal representation by returning a reference to a mutable object: method carcassonne.Model.Spot.getTile() may expose internal representation by returning carcassonne.Model.Spot.tile — Spot.java line 36 |	Medium |	Open |	Raphael |
| EI_EXPOSE_REP2 |	A method exposes its internal representation by storing a reference to a mutable external object: constructor new carcassonne.Model.Spot(int, int, Tile) may expose internal representation by storing mutable external object in carcassonne.Model.Spot.tile — Spot.java line 19 |	Medium |	Open |	Raphael |
| EI_EXPOSE_REP2 |	A method exposes its internal representation by storing a reference to a mutable external object: method carcassonne.Model.Spot.setTile(Tile) may expose internal representation by storing mutable external object in carcassonne.Model.Spot.tile — Spot.java line 40 |	Medium |	Open |	Raphael |
| EI_EXPOSE_REP |	A method may expose its internal representation by returning a reference to a mutable object: method carcassonne.Model.Tile.getMeeple() may expose internal representation by returning carcassonne.Model.Tile.meple — Tile.java line 80 |	Medium |	Open |	Juan |
| EI_EXPOSE_REP2 |	A method exposes its internal representation by storing a reference to a mutable external object: constructor new carcassonne.Model.Tile(char, TileSide[]) may expose internal representation by storing mutable external object in carcassonne.Model.Tile.sides — Tile.java line 21 |	Medium |	Open |	Juan |
| EI_EXPOSE_REP2 |	A method exposes its internal representation by storing a reference to a mutable external object: constructor new carcassonne.Model.Tile(char, TileSide[], int) may expose internal representation by storing mutable external object in carcassonne.Model.Tile.sides — Tile.java line 34 |	Medium |	Open |	Juan |
| EI_EXPOSE_REP2 |	A method exposes its internal representation by storing a reference to a mutable external object: constructor new carcassonne.Model.Tile(char, TileSide[], Pane) may expose internal representation by storing mutable external object in carcassonne.Model.Tile.sides — Tile.java line 27 |	Medium |	Open |	Juan |
| EI_EXPOSE_REP2 |	A method exposes its internal representation by storing a reference to a mutable external object: method carcassonne.Model.Tile.setMeeple(Meeple) may expose internal representation by storing mutable external object in carcassonne.Model.Tile.meple — Tile.java line 48 |	Medium |	Open |	Juan |
| SE_NO_SERIALVERSIONID |	Class is Serializable but does not define serialVersionUID: carcassonne.Model.Tile is Serializable; consider declaring serialVersionUID — Tile.java lines 14-80 |	Medium |	Open |	Raphael |
| SE_TRANSIENT_FIELD_NOT_RESTORED |	Transient field not set during deserialization: field carcassonne.Model.Tile.pane is transient but is not set during deserialization — Tile.java |	Medium |	Open |	Raphael |
| RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT |	Return value of method without side effect is ignored: return value of carcassonne.Model.Tile.getSideType(int) is ignored but the method has no side effect — TileTest.java line 66 |	Medium |	Open |	Noah |
| RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT |	Return value of method without side effect is ignored: return value of carcassonne.Model.Tile.getSideType(int) is ignored but the method has no side effect — TileTest.java line 71 |	Medium |	Open |	Noah |
| RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT |	Return value of method without side effect is ignored: return value of carcassonne.Model.Tile.getSideType(int) is ignored but the method has no side effect — TileTest.java line 51 |	Medium |	Open |	Noah |
| DLS_DEAD_LOCAL_STORE |	Dead store to a local variable in method carcassonne.Service.databaseService.setSavedGames(User, boolean, Game): local variable 'game_state_bytes' assigned but never used — databaseService.java line 129 |	Medium |	Open |	Juan |
| MS_EXPOSE_REP |	Public static method may expose internal representation by returning a mutable object or array: static method carcassonne.Service.databaseService.getInstance() may expose internal representation by returning carcassonne.Service.databaseService.instance — databaseService.java line 34 |	Medium |	Open |	Juan |
| NM_CLASS_NAMING_CONVENTION |	Class name should start with an uppercase letter: carcassonne.Service.databaseService does not start with an uppercase letter — databaseService.java lines 23-216 |	Medium |	Open |	Juan |
| SING_SINGLETON_GETTER_NOT_SYNCHRONIZED |	Instance-getter method of class using singleton design pattern is not synchronized (carcassonne.Service.databaseService) — databaseService.java lines 32-34 |	Medium-High |	Open |	Juan |



