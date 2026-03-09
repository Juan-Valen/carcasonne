package carcassonne.ServiceTest;

import carcassonne.Model.Game;
import carcassonne.Model.Tile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import java.io.*;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SerializationTest
{
    Game game = new Game();

    @DisplayName("Amazingly incredibly beautiful test")
    @Test
    public void Test()
    {
        game.placeTile(72,72, null);
        carcassonne.Model.Tile tile = game.getCurrentTile();
        System.out.println(tile.getType());

        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);

            oos.writeObject(game);
            byte[] serializedData = baos.toByteArray();



            Blob blob = new SerialBlob(serializedData);

            ObjectInputStream ois = new ObjectInputStream(blob.getBinaryStream());
            Game retrievedGame = (Game) ois.readObject();

            carcassonne.Model.Tile tile3 = retrievedGame.getCurrentTile();
            System.out.println(tile3.getType());
            assertEquals(tile.getType(), tile3.getType());
            assertEquals(tile.getOrientation(), tile3.getOrientation());
            assertEquals(tile.getBonusPoint(), tile3.getBonusPoint());

        } catch (IOException ex) {
            System.out.println("IOException is caught");
        } catch (SerialException e) {
            System.out.println("SerialException is caught");
        } catch (SQLException e) {
            System.out.println("SQLException is caught");
        } catch (ClassNotFoundException e) {
            System.out.println("ClassNotFoundException is caught");
        }
    }

    @DisplayName("Amazingly incredibly beautiful test2")
    @Test
    public void Test2()
    {
        game.placeTile(72,72, null);
        carcassonne.Model.Tile tile = game.getCurrentTile();
        System.out.println(tile.getType());

        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);

            oos.writeObject(game);
            byte[] serializedData = baos.toByteArray();



            Blob blob = new SerialBlob(serializedData);

            ObjectInputStream ois = new ObjectInputStream(blob.getBinaryStream());
            Game retrievedGame = (Game) ois.readObject();

            carcassonne.Model.Tile tile3 = retrievedGame.getCurrentTile();
            System.out.println(tile3.getType());
            assertEquals(tile.getType(), tile3.getType());
            assertEquals(tile.getOrientation(), tile3.getOrientation());
            assertEquals(tile.getBonusPoint(), tile3.getBonusPoint());

        } catch (IOException ex) {
            System.out.println("IOException is caught");
        } catch (SerialException e) {
            System.out.println("SerialException is caught");
        } catch (SQLException e) {
            System.out.println("SQLException is caught");
        } catch (ClassNotFoundException e) {
            System.out.println("ClassNotFoundException is caught");
        }
    }
}
