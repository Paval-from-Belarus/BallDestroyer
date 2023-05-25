package com.baller.game.serializer;

import com.baller.game.Settings;
import com.baller.game.field.GameField;
import com.baller.game.players.Player;
import com.baller.game.players.Players;
import org.javatuples.Pair;
import org.jetbrains.annotations.Nullable;

import java.awt.geom.Point2D;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextSerializer {
private static String serialize(java.io.Serializable... objects) {
      StringBuilder strText = new StringBuilder();
      for (var object : objects) {
	    Class<?> classType = object.getClass();
	    Field[] fields = classType.getDeclaredFields();
	    for (Field field : fields) {
		  if (!Modifier.isTransient(field.getModifiers()) || field.isAnnotationPresent(Serial.class)) {

		  }
	    }
      }
      return null;
}
private String collectFields(Serializable<?> serializable) throws IllegalAccessException {
      String[] labels = serializable.getFieldNames();
      Field[] fields = serializable.getClass().getDeclaredFields();
      StringBuilder strText = new StringBuilder();
      int index = 0;
      for(Field field : fields){
	    if(index >= labels.length)
		  break;
	    if(field.getName().equals(labels[index])){
		  field.setAccessible(true);
		  var instance = field.get(serializable);
		  strText.append(instance.toString());
	    }
	    index += 1;
      }
      return strText.toString();
}
private String serializeField(GameField field) throws IllegalAccessException {
      	String fieldString = collectFields(field.serialize());
	return "#Field's properties\n" + fieldString;
}

private String serializePlayers(Player.Properties[] players, Map<Integer, String> nameMapper) throws NoSuchFieldException, IllegalAccessException {
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
      for (var player : players) {
	    strText.append("###Player\n");
	    strText.append(player.serialize());
      }
      strText.append("\n");
      return strText.toString();

}
private String serializeSettings(Settings settings) {
      return "#Settings\n" + settings.serializer().serialize() + "\n";
}
private String serializeStatistics(List<Pair<String, Integer>> statistics) {
      StringBuilder strText = new StringBuilder();
      strText.append("#Statistics");
      for (var pair : statistics) {
	    strText.append("[").append(pair.getValue0()).append("=>").append(pair.getValue1()).append("]\n");
      }
      return strText.toString();
}
private String serializeElapsedTime(Long time) {
      return "#Elapsed time:(" + time + ")\n";
}
public String toText(Serializer source) {
      StringBuilder builder = new StringBuilder();
      try {
	    builder
		.append(serializeField(source.field))
		.append(serializePlayers(source.players, source.nameMapper))
		.append(serializeSettings(source.settings))
		.append(serializeStatistics(source.statistics))
		.append(serializeElapsedTime(source.elapsedTime));
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
private Optional<List<Pair<String,Integer>>> getStatistics(String source) {
      String[] items = source.split("#Statistics");
      if (items.length < 2) {
	    return Optional.empty();
      }
      source = items[1];
      Pattern pattern = Pattern.compile("\\[([a-zA-Z]+=>[0-9]+)]");
      Matcher matcher = pattern.matcher(source);
      List<Pair<String, Integer>> statistics = new ArrayList<>();
      while (matcher.find()) {
	    String[] pieces = matcher.group(1).split("=>");
	    Integer score = Integer.parseInt(pieces[1]);
	    String name = pieces[0];
	    statistics.add(new Pair<>(name, score));
      }
      if (statistics.isEmpty()){
	    return Optional.empty();
      }
      return Optional.of(statistics);
}

private Optional<Map<Integer, String>> getNameMapper(String source) {
      String[] items = source.split("##Name mapping");
      if (items.length < 2) {
	    return Optional.empty();
      }
      source = items[1];
      Pattern pattern = Pattern.compile("([a-zA-Z]+=>[0-9]+)");
      Matcher matcher = pattern.matcher(source);
      Map<Integer, String> nameMapper = new HashMap<>();
      while (matcher.find()) {
	    String[] pieces = matcher.group(1).split("=>");
	    Integer id = Integer.parseInt(pieces[1]);
	    String name = pieces[0];
	    nameMapper.put(id, name);
      }
      if (nameMapper.isEmpty())
	    return Optional.empty();
      return Optional.of(nameMapper);
}
private Optional<Long> getElapsedTimme(String source) {
      Pattern pattern = Pattern.compile("#Elapsed Time:\\([0-9]+\\)");
      Matcher matcher = pattern.matcher(source);
      Optional<Long> result = Optional.empty();
      if (matcher.find() && matcher.groupCount() == 1) {
	    try {
		  Long time = Long.parseLong(matcher.group(1));
		  result = Optional.of(time);
	    } catch (NumberFormatException ignored) {
	    }
      }
      return result;
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
      var statistics = getStatistics(content);
      var time = getElapsedTimme(content);
      if (statistics.isEmpty() || time.isEmpty())
	    return  null;
      if(field.isEmpty() || mapper.isEmpty() || players.isEmpty() || settings.isEmpty())
	    return null;
      result.field = field.get();
      result.nameMapper = mapper.get();
      result.players = players.get();
      result.settings = settings.get();
      result.statistics = statistics.get();
      result.setElapsedTime(time.get());
      return result;
}
private String serializeField(GameField.Properties props){
      return "#Field's properties\n" + props.serialize() + "\n";
}
private String serializeSettings(Settings.Properties settings) {
      return "#Settings\n" + settings.serialize() + "\n";
}

}
