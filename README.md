# Carcasonne
Software version of the game Carcasonne.

## 1. Brief Summary
This project was carried out by the four contributors as part of the Software Engineering Project 1 course.  
During it, the work was separated into four sprints where we created a state-of-the-art replica of the board game Carcassonne.  
The project can be fully run on docker as it was an objetive of the course.  
<a id="professor"></a>Professor : [Amir Dirin](https://github.com/ADirin).  
<a id="contributors"></a>Contributors : 
[Juan-Valen](https://github.com/Juan-Valen), 
[NooA-V](https://github.com/NooaV-M), 
[Haon19](https://github.com/Haon19), 
[RForSwan](https://github.com/RForSwan).

## 2. Technology Stack
The project relies on the listed technologies to run :
* Code :
  * Java
  * JavaFX
* Database :
  * PostgreSQL
* DevOps methods :
  * Github
  * Jenkins
  * Docker
  * Kubernetes (W.I.P.)

## 3. Database Diagrams
[Entity relationship diagram](./Documents/img/entity_relationship.png)  
[Relational schema diagram](./Documents/img/relational_schema.png)

## 4. Entity relationship
The [entity relationship](./Documents/img/carcassonne_class.png) can be described as follows,  
The user interacts with the game.  
The user can connect to the game.  
The user, once connected, can start a game, play using the different functionalities and save it.  
The game, by the action of the player, can interact with the virtual tiles (turn and place them).  
The game can also interact with the database by saving or retrieving past or current games.  

## 5. Instruction for playing the game with docker.

### Run database for game with docker
First, follow the [database instructions](./database-instructions.md). Note: to run the instructions you will need to copy the [sql initialization file](./config/init.sql).

### Run the game with docker

For linux run:
```bash
docker run --name carcassonne_app \
  -e DISPLAY=$DISPLAY \
  -v /tmp/.X11-unix:/tmp/.X11-unix \
  juanvalenzuela101/carcassonne_v1_2026
```

For Windows/Mac run:
```bash
docker run --name carcassonne_app \
  -e DISPLAY=host.docker.internal:0 \
  -v /tmp/.X11-unix:/tmp/.X11-unix \
  juanvalenzuela101/carcassonne_v1_2026
```

## 5. Acknowledgments
> Thank you to all the [contributors](#contributors) and [teacher](#professor)
