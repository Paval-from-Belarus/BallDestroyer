package com.baller.game.serializer;

import com.baller.game.Settings;
import com.baller.game.field.BrickBlock;
import com.baller.game.field.GameField;
import com.baller.game.players.Ball;
import com.baller.game.players.Player;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.OpenOption;
import java.util.List;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextSerializer {
private String serializeField(GameField.Properties props) {
      return "#Field's properties\n" + props.serialize() + "\n";
}
private String serializePlayers(Player.Properties[] properties, Map<Integer, String> nameMapper) throws NoSuchFieldException, IllegalAccessException {
      StringBuilder strText = new StringBuilder();
      strText.append("#Players' properties\n");
      strText.append("##Name mapping\n");
      for (var entry : nameMapper.entrySet())
	    strText.append(entry.getValue())
		.append("=>")
		.append(entry.getKey())
		.append("\n");
      strText.append("\n");
      strText.append("##Players' configuration").append("\n");
      for (var props : properties) {
	    strText.append("###Player\n");
	    strText.append(props.serialize());
      }
      strText.append("\n");
      return strText.toString();

}

private String serializeSettings(Settings.Properties props) {
      return "#Settings\n" + props.serialize() + "\n";
}

public String toText(Serializer source) {
      StringBuilder builder = new StringBuilder();
      try {
	    builder
		.append(serializeField(source.field))
		.append(serializePlayers(source.players, source.nameMapper))
		.append(serializeSettings(source.settings));
      } catch (NoSuchFieldException | IllegalAccessException e) {
	    throw new RuntimeException(e);
      }
      return builder.toString();

}

/**
 * @param regExp always should be contained second group as result
 *               Besides result as self pattern
 */
private Optional<String> getFirstGroup(String source, String regExp) {
      Pattern pattern = Pattern.compile(regExp);
      Matcher matcher = pattern.matcher(source);
      String result = null;
      if (matcher.find() && matcher.groupCount() > 0) {
	    result = matcher.group(1);
      }
      return Optional.ofNullable(result);
}

private Object createInstance(Class<?> type) throws ReflectiveOperationException {
      Constructor<?> instance = type.getDeclaredConstructor();
      instance.setAccessible(true);
      return instance.newInstance();
}

public static @Nullable List<Object> arrayFromString(String content, Function<String, ?> mapper){
      String[] items = content.split(", *");
      return Arrays.stream(items)
		 .map(mapper::apply)
		 .collect(Collectors.toList());
}

private Optional<Map<Integer, String>> getNameMapper(String source) {
      Pattern pattern = Pattern.compile("([a-zA-Z]+=>[0-9]+\n)");
      Matcher matcher = pattern.matcher(source);
      if (!matcher.find())
	    return Optional.empty();
      Map<Integer, String> nameMapper = new HashMap<>();
      for (int i = 1; i < matcher.groupCount(); i++) {
	    String[] pieces = matcher.group(i).split("=>");
	    Integer id = Integer.parseInt(pieces[1]);
	    String name = pieces[0];
	    nameMapper.put(id, name);
      }
      return Optional.of(nameMapper);
}

private Optional<Player.Properties[]> getPlayers(String source) throws ReflectiveOperationException {
	String[] items = source.split("##Players' configuration");
	if(items.length < 2)
	      return Optional.empty();
	String[] players = items[1].split("###Player\n");
	if(players.length < 2)
	      return Optional.empty();
	Player.Properties[] props = new Player.Properties[players.length - 1];
	boolean allCompleted = true;
	for(int i = 1; allCompleted &&  i < players.length; i++){
	      props[i - 1] = new Player.Properties();
	      props[i - 1].deserialize(players[i]);
	      allCompleted = !props[i - 1].isEmpty();
	}
	if(!allCompleted)
	      return Optional.empty();
	return Optional.of(props);
}

private Optional<GameField.Properties> getFieldProperties(String source) {
      final Matcher matcher =
	  Pattern.compile("#Field's properties\n((.+\n)+)\n")
	      .matcher(source);
      if(!matcher.find())
	    return Optional.empty();
      Optional<GameField.Properties> result;
      GameField.Properties props = new GameField.Properties();
      props.deserialize(matcher.group(1));
      if(props.isEmpty())
	    result = Optional.empty();
      else
	    result = Optional.of(props);
      return result;
}

public Optional<Settings.Properties> getSettings(String content) {
      Pattern pattern = Pattern.compile("#Settings\n((.+\n)+)\n");
      Matcher matcher = pattern.matcher(content);
      if (!matcher.find()) {
	    return Optional.empty();
      }
      Settings.Properties props = new Settings.Properties();
      props.deserialize(matcher.group(1));
      if (props.isEmpty())
	    props = null;
      return Optional.ofNullable(props);
}

public @Nullable Serializer fromText(String content) throws ReflectiveOperationException {
      Serializer result = new Serializer();
      var field = getFieldProperties(content);
      var mapper = getNameMapper(content);
      var players = getPlayers(content);
      var settings = getSettings(content);
      if(field.isEmpty() || mapper.isEmpty() || players.isEmpty() || settings.isEmpty())
	    return null;
      result.field = field.get();
      result.nameMapper = mapper.get();
      result.players = players.get();
      result.settings = settings.get();
      return result;
}
}
