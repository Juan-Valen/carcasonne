CREATE USER carcas WITH PASSWORD 'carcassonne1';
CREATE DATABASE carcassonne;
GRANT ALL PRIVILEGES ON DATABASE carcassonne TO carcas;

\c carcassonne;

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
  game_state BYTEA NULL,
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

CREATE TABLE languages
(
    language_id SERIAL NOT NULL,
    language CHAR(2) NOT NULL,
    PRIMARY KEY (language_id)
);

CREATE TABLE translations
(
    language_id INT NOT NULL,
    key VARCHAR(100) NOT NULL,
    translation VARCHAR(100) NOT NULL,
    UNIQUE (language_id, key),
    PRIMARY KEY (language_id, key),
    FOREIGN KEY (language_id) REFERENCES languages(language_id)
);

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO carcas;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO carcas;

INSERT INTO users(username, password)
VALUES
    ('carcassonne', 'password'),
    ('juan', '0j#mi9-fd2j_k4'),
    ('noah', '*rB#_Tz_6r2-'),
    ('nooa', '55PwgU39W!5#'),
    ('raphael','a-CY4E3a!8_j');

INSERT INTO languages(language)
VALUES
    ('en'),
    ('ch'),
    ('ru');

INSERT INTO translations (language_id, key, translation)
VALUES
    (1, 'english', 'English'),
    (1, 'chinese', 'Chinese'),
    (1, 'russian', 'Russian'),
    (1, 'players.prompt', 'Enter number of players'),
    (1, 'game.new', 'New Game'),
    (1, 'login', 'Login'),
    (1, 'user.change', 'Change user'),
    (1, 'username', 'username'),
    (1, 'password', 'password'),
    (1, 'back', 'Back'),
    (1, 'game', 'Carcassonne Game'),
    (1, 'game.saved', 'Saved Games'),
    (1, 'game.offline', 'offline'),
    (1, 'game.online', 'online'),
    (1, 'game.id', 'game id'),
    (1, 'game.saved.none', 'No saved games found.'),
    (1, 'rotate.tile', 'rotate tile'),
    (1, 'game.save.quit', 'Save & Quit'),
    (1, 'game.quit', 'Quit'),
    (1, 'open.secondary', 'Open Secondary'),
    (1, 'player', 'Player'),
    (1, 'points', 'Points'),
    (1, 'game.end.pts', 'pts');
INSERT INTO translations (language_id, key, translation)
VALUES
    (2, 'english', '英文'),
    (2, 'chinese', '中文'),
    (2, 'russian', '俄文'),
    (2, 'players.prompt', '玩家人数'),
    (2, 'game.new', '新游戏'),
    (2, 'login', '登陆'),
    (2, 'user.change', '更换玩家'),
    (2, 'username', '用户名'),
    (2, 'password', '密码'),
    (2, 'back', '返回'),
    (2, 'game', '卡卡颂'),
    (2, 'game.saved', '保存游戏'),
    (2, 'game.offline', '离线'),
    (2, 'game.online', '在线'),
    (2, 'game.id', '昵称'),
    (2, 'game.saved.none', '未找到存档'),
    (2, 'rotate.tile', '旋转地块'),
    (2, 'game.save.quit', '保存&离开'),
    (2, 'game.quit', '离开'),
    (2, 'open.secondary', '次级菜单栏'),
    (2, 'player', '玩家'),
    (2, 'points', '点数'),
    (2, 'game.end.pts', '积分'),
    (2, '0', '零'),
    (2, '1', '一'),
    (2, '2', '二'),
    (2, '3', '三'),
    (2, '4', '四'),
    (2, '5', '五'),
    (2, '6', '六'),
    (2, '7', '七'),
    (2, '8', '八'),
    (2, '9', '九');
INSERT INTO translations (language_id, key, translation)
VALUES
    (3, 'english', 'английский'),
    (3, 'chinese', 'китайский'),
    (3, 'russian', 'русский'),
    (3, 'players.prompt', 'Введите число игроков'),
    (3, 'game.new', 'Новая Игра'),
    (3, 'login', 'логин'),
    (3, 'user.change', 'Поменять пользователя'),
    (3, 'username', 'имя пользователя'),
    (3, 'password', 'пароль'),
    (3, 'back', 'Назад'),
    (3, 'game', 'Игра Каркассон'),
    (3, 'game.saved', 'Сохранения'),
    (3, 'game.offline', 'офлайн'),
    (3, 'game.online', 'онлайн'),
    (3, 'game.id', 'код игры'),
    (3, 'game.saved.none', 'Сохранения не найдены'),
    (3, 'rotate.tile', 'крутить плитку'),
    (3, 'game.save.quit', 'Сохранить и Выйти'),
    (3, 'game.quit', 'Выйти'),
    (3, 'open.secondary', 'Открыть меню'),
    (3, 'player', 'Игрок'),
    (3, 'points', 'Очки'),
    (3, 'game.end.pts', 'Оч.');

