package carcassonne.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.*;
import java.util.ArrayList;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

import carcassonne.Model.GameInfo;
import carcassonne.Model.User;
import carcassonne.Model.Game;

public class databaseService {
    private static databaseService instance = null;
    private static final String URL = "jdbc:postgresql://localhost:5432/carcassonne";
    private static final String USER = "carcas";
    private static final String PASSWORD = System.getenv("APP_PASSWORD");

    private databaseService() {
    }

    public static databaseService getInstance() {
        if (instance == null)
            instance = new databaseService();
        return instance;
    }

    public User loginUser(String username, String password) {
        int user_id = 0;
        int count = 0;

        String sql = "SELECT user_id FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    user_id = rs.getInt("user_id");
                    count++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (count != 1)
            return null;

        return new User(user_id, username);
    }

    public ArrayList<GameInfo> getSavedGamesInfo(User user) {
        ArrayList<GameInfo> savedGames = new ArrayList<>();

        String sql = "SELECT s.game_id, g.online, g.updated_date " +
                "FROM saves s JOIN games g ON s.game_id = g.game_id " +
                "WHERE s.user_id = ? ORDER BY g.updated_date DESC";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, user.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    GameInfo gameInfo = new GameInfo();
                    gameInfo.id = rs.getInt("game_id");
                    gameInfo.online = rs.getBoolean("online");
                    gameInfo.updatedDate = rs.getDate("updated_date");
                    savedGames.add(gameInfo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return savedGames;
    }

    public Game getGameState(int game_id) {
        String sql = "SELECT game_state FROM games WHERE game_id = ?";
        Game game = null;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, game_id);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Blob blob = rs.getBytes("game_state");
                    // try (InputStream is = blob.getBinaryStream();
                    // ObjectInputStream ois = new ObjectInputStream(is)) {
                    // game = (Game) ois.readObject();
                    // } catch (IOException | ClassNotFoundException e) {
                    // e.printStackTrace();
                    // }
                    byte[] data = rs.getBytes("game_state"); // Use getBytes()
                    if (data != null) {
                        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
                                ObjectInputStream ois = new ObjectInputStream(bis)) {
                            game = (Game) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return game;
    }

    public void setSavedGames(User user, boolean online, Game game_state) {

        byte[] game_state_bytes = new byte[1];
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);

            oos.writeObject(game_state);
            game_state_bytes = baos.toByteArray();
        } catch (IOException ex) {
            System.out.println("IOException while serializing game state:");
            ex.printStackTrace();
            return;
        }
        String sql = "INSERT INTO games(online, game_state, updated_date) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            final Date date = Date.valueOf(LocalDate.now());
            stmt.setBoolean(1, online);
            stmt.setBytes(2, game_state_bytes);
            stmt.setDate(3, date);

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    if (id != 0)
                        setSaves(user, id);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setSaves(User user, int game_id) {
        String sql = "INSERT INTO saves(game_id, user_id) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, game_id);
            stmt.setInt(2, user.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, String> getTranslations(String language) {
        Map<String, String> dictionary = new HashMap<>();

        String sql = "SELECT key, translation FROM translations AS t LEFT JOIN languages AS l ON l.language_id = t.language_id WHERE language = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, language);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String key = rs.getString("key");
                    String value = rs.getString("translation");
                    dictionary.put(key, value);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dictionary;
    }

    public String[] getLanguages() {
        Set<String> languages = new HashSet<>();

        String sql = "SELECT language FROM languages WHERE active = 1";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    languages.add(rs.getString("language"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return languages.toArray(new String[0]);
    }

}
