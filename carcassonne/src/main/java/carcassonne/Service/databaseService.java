package carcassonne.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import carcassonne.Model.GameState;
import carcassonne.Model.User;
import carcassonne.Model.Game;

public class databaseService {
    private static databaseService instance = null;
    private static final String URL = "jdbc:postgresql://localhost:5432/carcassonne";
    private static final String USER = "carcas";
    private static final String PASSWORD = "carcassonne1";

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

    public void setSavedGames(User user, boolean online, byte[] game_state) {
        int count = 0;

        String sql = "INSERT INTO games(online, game_state) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBytes(1, game_state);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();

            int id = -1;
            if (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setSaves(User user, int game_id) {
        int count = 0;

        String sql = "INSERT INTO saves(game_id, user_id) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, game_id);
            stmt.setInt(2, user.getId());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();

            int id = -1;
            if (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<GameState> getSavedGames(User user) {
        ArrayList<GameState> savedGames = new ArrayList<>();

        String sql = "SELECT s.game_id, g.online, g.updated_date " +
                "FROM saves s JOIN games g ON s.game_id = g.game_id " +
                "WHERE s.user_id = ? ORDER BY g.updated_date DESC";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, user.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    GameState gameState = new GameState();
                    gameState.id = rs.getInt("game_id");
                    gameState.online = rs.getBoolean("online");
                    gameState.updatedDate = rs.getDate("updated_date");
                    savedGames.add(gameState);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return savedGames;
    }

    public Game getGameState(int game_id) {
        byte[] bytes = new byte[1];
        String sql = "SELECT game_state FROM game WHERE game_id = ?";
        Game game = null;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, game_id);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Blob blob = rs.getBlob("game_state");
                    try (InputStream is = blob.getBinaryStream();
                         ObjectInputStream ois = new ObjectInputStream(is)) {
                        game = (Game) ois.readObject();
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return game;
    }
}
