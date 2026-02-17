package carcassonne.DataType;

public enum Color {
    RED("red", "#FF0000"), GREEN("green", "#00FF00"), BLUE("blue", "#0000FF");

    private String name;
    private String hexCode;

    Color(String name, String hexCode)
    {
        this.name = name;
        this.hexCode = hexCode;
    }

    public String getName()
    {
        return name;
    }

    public String getHexCode()
    {
        return hexCode;
    }
}
