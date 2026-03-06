package carcassonne.Model;

import java.util.ArrayList;
import java.util.List;

public class AvailableSpots {
    private String direction;
    private List<Spot> spots = new ArrayList<Spot>();

    public AvailableSpots(String direction) {
        this.direction = direction;
    }

    public String getDirection() {
        return direction;
    }

    public void add(Spot spot) {
        spots.add(spot);
    }

    public List<Spot> getSpots() {
        return spots;
    }

    public void clear() {
        spots.clear();
    }

}
