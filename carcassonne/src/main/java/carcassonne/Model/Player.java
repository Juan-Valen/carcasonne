package carcassonne.Model;

import carcassonne.DataType.Color;

public class Player
{
    private Color color;
    private String user;
    private int points;

    public Player(Color color, String user)
    {
        this.color = color;
        this.user = user;
        this.points = 0;
    }

    public Player(Color color, String user, int points)
    {
        this.color = color;
        this.user = user;
        this.points = points;
    }

    public Color getColor()
    {
        return color;
    }

    public String getUser() {
        return user;
    }

    public int getPoints()
    {
        return points;
    }

    public void addPoints(int points) throws IllegalArgumentException
    {
        if(points < 0) throw new IllegalArgumentException("Can not add a negative amount of points");
        this.points += points;
    }
}
