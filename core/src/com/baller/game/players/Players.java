package com.baller.game.players;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.baller.game.field.GameField;
import com.baller.game.players.Player;
import org.jetbrains.annotations.NotNull;
import com.baller.game.players.Player.*;

import java.util.*;

public class Players {
public static final int DEFAULT_PLAYER_CNT = 1;
private final List<Player> players;
private final Map<Player.Id, String> playersMap;
private int nextIndex;

public Players() {
      players = new ArrayList<>();
      playersMap = new HashMap<>();
      nextIndex = 0;
}

private Players(Player.Properties[] properties, Map<Player.Id, String> playersMap) {
      nextIndex = properties.length;//tricky step
      this.playersMap = playersMap;
      this.players = new ArrayList<>();
      for (var props : properties)
	    players.add(props.construct());

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
}
