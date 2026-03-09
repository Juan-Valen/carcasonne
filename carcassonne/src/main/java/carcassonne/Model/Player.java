package carcassonne.Model;

import java.io.Serializable;
import java.util.ArrayList;

import carcassonne.DataType.Color;

public class Player implements Serializable {
    private Color color;
    private User user;
    private int points;
    private ArrayList<Meple> meples;

    public Player() {
        this.points = 0;
    }

    public Player(User user) {
        this.user = user;
        this.points = 0;
    }

    public Player(User user, int index) {
        this.user = user;
        this.points = 0;
        initMeple(index);
    }

    public Color getColor() {
        return color;
    }

    public User getUser() {
        return user;
    }

    public int getPoints() {
        return points;
    }

    public void addPoints(int points) throws IllegalArgumentException {
        if (points < 0)
            throw new IllegalArgumentException("Can not add a negative amount of points");
        this.points += points;
    }

    public void initMeple(int playerIndex) {
        meples = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            meples.add(new Meple(playerIndex));
        }
    };

    public int getMepleCount() {
        return meples.size();
    }

    public void addMeple(Meple meple) {
        meples.add(meple);
    }

    public Meple placeMeple() {
        if (meples.size() == 0)
            return null;
        return meples.remove(0);

    }
}
