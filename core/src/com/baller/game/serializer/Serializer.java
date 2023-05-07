package com.baller.game.serializer;

import com.baller.game.Settings;
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

public GameField getGameField() {
      return gameField;
}

public Players getPlayers() {
      return rawPlayers;
}

public Settings getSettings() {
      return rawSettings;
}

public enum SerializationMode {Json, Text}

public static final String DEFAULT_NAME = "game.back";
public GameField.Properties field;
public Player.Properties[] players;
public Map<Integer, String> nameMapper;
public Settings.Properties settings;
public transient String configPath;

private transient GameField gameField;
private transient Players rawPlayers;
private transient Settings rawSettings;

private void setPlayersProps(Players players) throws NoSuchFieldException, IllegalAccessException {
      Player.Properties[] properties = players.getAll();
      for (var props : properties) {
	    var player = players.get(props.getId());
	    if (player.isEmpty())
		  continue;
	    var balls = Arrays.stream(player.get().getBalls())
			    .map(Ball::getProperties)
			    .toArray(Ball.Properties[]::new);
	    var propsClass = props.getClass();
	    Field plBalls = propsClass.getDeclaredField("balls");
	    plBalls.setAccessible(true);
	    plBalls.set(props, balls);
      }
      this.players = properties;
}

@SuppressWarnings("unchecked")
private void setPlayerMapper(Players players) throws NoSuchFieldException, IllegalAccessException {
      var playersClass = players.getClass();
      Field map = playersClass.getDeclaredField("playersMap");
      map.setAccessible(true);
      var realMap = (Map<Player.Id, String>) map.get(players);
      this.nameMapper = new HashMap<>();
      for (var entry : realMap.entrySet()) {
	    var playerId = entry.getKey();
	    var playerIdClass = playerId.getClass();
	    Field value = playerIdClass.getDeclaredField("value");
	    value.setAccessible(true);
	    nameMapper.put((Integer) value.get(playerId), entry.getValue());
      }
}

public Serializer setPlayers(Players players) throws NoSuchFieldException, IllegalAccessException {
      setPlayersProps(players);
      setPlayerMapper(players);
      this.rawPlayers = players;
      return this;
}

public Serializer setGameField(GameField field) {
      this.field = field.serialize();
      this.gameField = field;
      return this;
}

public Serializer setSettings(Settings settings) {
      this.settings = settings.serializer();
      this.rawSettings = settings;
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

private void saveAsText(Path destPath) throws IOException {
      TextSerializer text = new TextSerializer();
      String content = text.toText(this);
      Files.writeString(destPath, content);
}

private void applyCopy(Serializer father) {
      this.nameMapper = father.nameMapper;
      this.players = father.players;
      this.field = father.field;
      this.settings = father.settings;
}

public boolean fromFile(@NotNull String sourceName, @NotNull SerializationMode mode) {
      Path path = Path.of(sourceName);
      boolean response = true;
      try {
	    String content = Files.readString(path);
	    Serializer father;
	    if (mode == SerializationMode.Json) {
		  father = new Gson().fromJson(content, Serializer.class);
	    } else {
		  father = new TextSerializer().fromText(content);
	    }
	    if (father != null) {
		  applyCopy(father);
	    } else {
		  response = false;
	    }
      } catch (Exception e) {
	    e.printStackTrace();
	    response = false;
      }
      return response;
}

/**
 * @param destName to use default backup name
 *                 pass null as parameter
 */
public boolean toFile(@Nullable String destName, @NotNull SerializationMode mode) {
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
