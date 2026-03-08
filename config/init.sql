CREATE USER carcas WITH PASSWORD 'carcassonne1';
CREATE DATABASE carcassonne;
GRANT ALL PRIVILEGES ON DATABASE carcassonne TO carcas;

CREATE TABLE users
(
  user_id SERIAL NOT NULL,
  username VARCHAR(50) NOT NULL,
  password VARCHAR(100) NOT NULL,
  PRIMARY KEY (user_id),
  UNIQUE (username)
);

CREATE TABLE games
(
  game_id SERIAL NOT NULL,
  game_state TEXT NOT NULL,
  updated_date DATE NOT NULL,
  online BOOLEAN NOT NULL,
  PRIMARY KEY (game_id)
);

CREATE TABLE saves
(
  game_id INT NOT NULL,
  user_id INT NOT NULL,
  PRIMARY KEY (game_id, user_id),
  FOREIGN KEY (game_id) REFERENCES games(game_id),
  FOREIGN KEY (user_id) REFERENCES users(user_id)
);

INSERT INTO users(username, password) 
VALUES 
    ('juan', '0j#mi9-fd2j_k4'),
    ('noah', '*rB#_Tz_6r2-'),
    ('nooah', '55PwgU39W!5#'),
    ('raphael','a-CY4E3a!8_j');

