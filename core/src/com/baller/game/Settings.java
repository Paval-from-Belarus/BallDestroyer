package com.baller.game;

import com.baller.game.field.GameField;
import com.baller.game.players.Player;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

//import com.google.gson.Gson;
public class Settings {
private float soundLevel;
private int[] scoreTable;

public int[] getScoreTable() {
      return null;
}

public void setScoreTable(Player.Properties[] players){

}

public byte[] getSavedSession(String fileName) {
      return null;
}

public void saveSession(GameField field, List<Player> players) {

}
public void setResolution(float x, float y){


}
private void saveText(GameField field, List<Player> players) {
      try (BufferedWriter writer = new BufferedWriter(new FileWriter("game.back"))) {

      } catch (IOException e) {
	    throw new RuntimeException(e);
      }
}

public String getPlayerName(Player.Id id) {
      return null;
}

public void setPlayerName(Player.Id id, String name) {

}

}
