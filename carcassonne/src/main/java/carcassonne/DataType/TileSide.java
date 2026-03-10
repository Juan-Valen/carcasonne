package carcassonne.DataType;

import java.io.Serializable;

public enum TileSide implements Serializable {
    FIELD(0), ROAD(1), CITY(2), RIVER(3);

    private int id;

    TileSide(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
