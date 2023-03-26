package com.baller.game;

import com.baller.game.players.Ball;
import com.baller.game.players.Player;
import com.baller.game.field.GameField;
import com.baller.game.players.Players;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Serializer {

public enum SerializationMode {Json, Text}

public static final String DEFAULT_NAME = "game.back";
public  GameField.Properties field;
public Player.Properties[] players;
public Map<Integer, String> nameMapper;
public String configPath;


private void setPlayersProps(Players players) throws NoSuchFieldException, IllegalAccessException {
      Player.Properties[] properties = players.getAll();
      for (var props : properties) {
	    var player = players.get(props.getId());
	    if (player.isEmpty())
		  continue;
	    var balls = Arrays.stream(player.get().getBalls())
			    .map(Ball::getProperties)
			    .toArray(Ball.Properties[]::new);
	    Class propsClass = props.getClass();
	    Field plBalls = propsClass.getDeclaredField("balls");
	    plBalls.setAccessible(true);
	    plBalls.set(props, balls);
      }
      this.players = properties;
}
private void setPlayerMapper(Players players) throws NoSuchFieldException, IllegalAccessException {
      Class playersClass = players.getClass();
      Field map = playersClass.getDeclaredField("playersMap");
      map.setAccessible(true);
      Map<Player.Id, String> realMap = (Map<Player.Id, String>) map.get(players);
      this.nameMapper = new HashMap<>();
      for(var entry : realMap.entrySet()){
	    var playerId = entry.getKey();
	    var playerIdClass = playerId.getClass();
	    Field value = playerIdClass.getDeclaredField("value");
	    value.setAccessible(true);
	    nameMapper.put((Integer)value.get(playerId), entry.getValue());
      }
}
public Serializer setPlayers(Players players) throws NoSuchFieldException, IllegalAccessException {
      setPlayersProps(players);
      setPlayerMapper(players);
      return this;
}
public Serializer setGameField(GameField field){

      return this;
}
public Serializer() {}

public void addConfig(Path configPath) {
      this.configPath = configPath.toAbsolutePath().toString();
}

public void addHistory() {

}

private void saveAsJson(Path destPath) throws IOException {
      Gson gson = new Gson();
      String text = gson.toJson(this);
      Files.writeString(destPath, text);
}

private void saveAsText(Path destPath) {

}

/**
 * @param destName to use default backup name
 *                 pass null as parameter
 */
public boolean start(@Nullable String destName, @NotNull SerializationMode mode) {
      if (destName == null)
	    destName = DEFAULT_NAME;
      Path path = Path.of(destName);
      try {
	    if (mode == SerializationMode.Json)
		  saveAsJson(path);
	    else
		  saveAsText(path);
      } catch (Exception e) {
	    System.out.println(e.getMessage());
	    return false;
      }
      return true;


}
}
