package com.baller.game.players;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.baller.game.field.GameField;
import com.baller.game.players.Player;
import org.jetbrains.annotations.NotNull;
import com.baller.game.players.Player.*;

import java.util.*;
import java.util.function.Consumer;

public class Players {
public enum GameResult {Victory, Defeat, InProgress}
public static final int DEFAULT_PLAYER_CNT = 1;
private final List<Player> players;
private final Map<Player.Id, String> playersMap;
private int nextIndex;

public Players() {
      players = new ArrayList<>();
      playersMap = new HashMap<>();
      nextIndex = 0;
}

public Players(Player.Properties[] properties, Map<Integer, String> playersMap) {
      nextIndex = properties.length;//tricky step
      this.playersMap = new HashMap<>();
      this.players = new ArrayList<>();
      for (var props : properties){
	    Player player = props.construct();
	    players.add(player);
	    //todo: replace with beautiful solution
	    String name = playersMap.get(player.getId().value);
	    this.playersMap.put(player.getId(), name);
      }

}

public Player.Id add(String name) {
      Player.Id id = new Player.Id(nextIndex++);
      Player player = new Player(id);
      playersMap.put(id, name);
      players.add(player);
      return id;
}

public Optional<Player.Id> get(String name) {
      Player.Id id = null;
      for (var entry : playersMap.entrySet()) {
	    if (entry.getValue().equals(name)) {
		  id = entry.getKey();
		  break;
	    }
      }
      return Optional.ofNullable(id);
}

public Optional<Player> get(Player.Id id) {
      return players.stream()
		 .filter(player -> player.getId().equals(id))
		 .findFirst();
}

public void block(@NotNull Player.Id id) {
      var player = get(id);
      player.ifPresent(member -> member.setState(State.Blocked));
}

public void release(Player.Id id) {
      var player = get(id);
      player.ifPresent(member -> member.setState(State.Alive));
}

public void remove(Player.Id id) {
      var player = get(id);
      player.ifPresent(member -> {
	    member.setState(State.Blocked);
	    member.dispose();
	    players.remove(member);
      });
}

private @NotNull Player[] getVerifiedPlayers() {
      return players.stream()
		 .filter(player -> player.getState() == State.Alive)
		 .toArray(Player[]::new);
}

public void forEach(Player.State state, Consumer<Player> action) {
      Player[] players = new Player[0];
      switch (state) {
	    case Alive -> players = getVerifiedPlayers();
	    // TODO: 07/05/2023
	    case Blocked, Defeated -> {}
      }
      for (var player : players) {
	    action.accept(player);
      }
}
public @NotNull Player.Properties[] getVerified() {
      return Arrays.stream(getVerifiedPlayers())
		 .map(Player::getProperties)
		 .toArray(Player.Properties[]::new);

}

public @NotNull Player.Properties[] getAll() {
      return players.stream()
		 .map(Player::getProperties)
		 .toArray(Player.Properties[]::new);
}
public void removeAll() {
      players.forEach(Player::dispose);
      players.clear();
}

public void draw(SpriteBatch batch) {
      players.forEach(player -> player.draw(batch));
}

public void update(float dt) {
      Arrays.stream(getVerifiedPlayers())
	  .forEach(player -> player.update(dt));
}
//the great assumption... Only single player)
public GameResult getGameResult(){
      GameResult result = GameResult.Defeat;
      for (Player player : getVerifiedPlayers()) {
	    if (player.getState() == State.Victory) {
		result = GameResult.Victory;
		break;
	    }
	    //the player in game
	    if (player.isActive()) {
		  result = GameResult.InProgress;
	    }
      }
      return result;
}
}
