package carcassonne.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class Player implements Serializable {
    private User user;
    private int points;
    private ArrayList<Meeple> meples = new ArrayList<>();

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
        initMeeple(index);
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

    public void initMeeple(int playerIndex) {
        meples = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            meples.add(new Meeple(playerIndex));
        }
    };

    public int getMeepleCount() {
        return meples.size();
    }

    public void addMeeple(Meeple meple) {
        meples.add(meple);
    }

    public Meeple placeMeeple() {
        if (meples.size() == 0)
            return null;
        return meples.remove(0);

    }
}
