package com.baller.game.serializer;

import com.baller.game.field.BrickBlock;
import com.baller.game.field.GameField;
import com.baller.game.players.Ball;
import com.baller.game.players.Player;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextSerializer {
private String serializeField(GameField.Properties props) throws NoSuchFieldException, IllegalAccessException {
      final String[] infoHeader = {"Ratio=", "PlayerCnt=", "TrampolineCnt=", "Blocks="};
      StringBuilder strText = new StringBuilder();
      strText.append("#Field properties\n");
      var propsClass = props.getClass();
      var list = List.of(
	  propsClass.getDeclaredField("ratio"),
	  propsClass.getDeclaredField("playerCnt"),
	  propsClass.getDeclaredField("trampolineCnt"),
	  propsClass.getDeclaredField("blocks"));
      int index = 0;
      for (Field field : list) {
	    field.setAccessible(true);
	    strText.append(infoHeader[index]);
	    if (index == 3) {
		  strText
		      .append(Arrays.toString((BrickBlock.Type[]) field.get(props)));
	    } else {
		  strText
		      .append(field.get(props));
	    }
	    strText.append("\n");
	    index += 1;
      }
      return strText.append("\n").toString();
}

private String serializeBalls(Ball.Properties[] properties) throws IllegalAccessException {
      StringBuilder strBalls = new StringBuilder();
      strBalls.append("###Balls").append("\n");
      for (var props : properties) {
	    for (Field field : props.getClass().getDeclaredFields()) {
		  field.setAccessible(true);
		  strBalls.append(field.getName())
		      .append("=");
		  if (!(field.get(props) instanceof Point)) {
			strBalls.append(field.get(props));
		  } else {
			Point point = (Point) field.get(props);
			strBalls.append("(x=").append(point.x).append(",");
			strBalls.append("y=").append(point.y).append(")");
		  }
		  strBalls.append("\n");
	    }
      }
      return strBalls.toString();
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
	    var propsClass = props.getClass();
	    Field ballsField = propsClass.getDeclaredField("balls");
	    ballsField.setAccessible(true);
	    Ball.Properties[] ballsProps = (Ball.Properties[]) ballsField.get(props);
	    Player.Id id = props.getId();
	    Field idValue = id.getClass().getDeclaredField("value");
	    idValue.setAccessible(true);
	    strText
		.append("###Player").append("\n")
		.append("Id=").append(idValue.get(id)).append("\n")
		.append("Score=").append(props.getScore()).append("\n")
		.append("Trampolines=").append(props.getTrampolineCnt()).append("\n")
		.append(serializeBalls(ballsProps)).append("\n");
      }
      return strText.toString();

}

public String toText(Serializer source) {
      StringBuilder builder = new StringBuilder();
      try {
	    builder
		.append(serializeField(source.field))
		.append(serializePlayers(source.players, source.nameMapper));
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

private @Nullable List<Object> fromString(String content, Function<String, ?> mapper) {
      String[] items = content.split(", *");
      return Arrays.stream(items)
		 .map(mapper::apply)
		 .collect(Collectors.toList());
}

private Map<Integer, String> getNameMapper(String source) throws ReflectiveOperationException {
      Pattern pattern = Pattern.compile("([a-zA-Z]+=>[0-9]+\n)");
      Matcher matcher = pattern.matcher(source);
      if (!matcher.find() || matcher.groupCount() == 0)
	    throw new IllegalStateException("Incorrect params");
      Map<Integer, String> nameMapper = new HashMap<>();
      for (int i = 1; i < matcher.groupCount(); i++) {
	    String[] pieces = matcher.group(i).split("=>");
	    Integer id = Integer.parseInt(pieces[1]);
	    String name = pieces[0];
	    nameMapper.put(id, name);
      }
      return nameMapper;
}

private Ball.Properties extractBall(String[] rawBall) {
      	//todo: ideas about radius and etc
	return null;
}

private @Nullable Player.Properties extractPlayer(String rawPlayer) throws ReflectiveOperationException {
      final String[] fields = {
	  "id", "score", "trampolineCnt"
      };
      final String[] patterns = {
	  "Id=([0-9]+)", "Score=([0-9]+)", "Trampolines=([0-9]+)"
      };
      String[] lines = rawPlayer.split("\n");
      if (lines.length < 2)
	    return null;
      String line;
      int index = 0;
      var props = createInstance(Player.Properties.class);
      while (!(line = lines[index + 1]).matches("###Balls")) {
	    var rawField = getFirstGroup(line, patterns[index]);
	    if (rawField.isEmpty())
		  return null;
	    Field field = props.getClass().getDeclaredField(fields[index]);
	    field.setAccessible(true);
	    if (index == 0) {
		  var idGen = Player.Id.class.getDeclaredConstructor(int.class);
		  idGen.setAccessible(true);
		  field.set(props, idGen.newInstance(Integer.parseInt(rawField.get())));
	    } else {
		  Integer value = Integer.parseInt(rawField.get());
		  field.set(props, value);
	    }
	    index += 1;
      }
      index += 2; //skip ### Balls and set to correct field
      String[] ballLines = Arrays.copyOfRange(lines, index, lines.length);
      var ballProps = extractBall(ballLines);
      Field balls = props.getClass().getDeclaredField("balls");
      balls.setAccessible(true);
      balls.set(props, ballProps);
      return (Player.Properties) props;
}

private Player.Properties[] getPlayers(String source) throws ReflectiveOperationException {
      Pattern pattern = Pattern.compile("(###Player\n([^#]+\n)###Balls\n(.+\n)+\n)");
      Matcher matcher = pattern.matcher(source);
      if (!matcher.find() || matcher.groupCount() == 0)
	    throw new IllegalStateException("Incorrect file");
      List<Player.Properties> list = new ArrayList<>();
      int offset = 0;
      matcher.region(offset, source.length());
      while(matcher.find() && matcher.group(1) != null){
	    String rawPlayer = matcher.group(1);
	    offset = matcher.end();
	    var props = extractPlayer(rawPlayer);
	    if(props == null)
		  throw new IllegalStateException("Incorrect file");
	    list.add(props);
	    matcher.region(offset, source.length());
      }
//      for (int i = 1; i < matcher.groupCount(); i++) {
//	    System.out.println(i + " -> " + matcher.group(i));
//      }
      return null;
}

private GameField.Properties getFieldProperties(String source) throws ReflectiveOperationException {
      final List<String> patterns = List.of(
	  "Ratio=([0-9]+(\\.[0-9]+))?",
	  "PlayerCnt=([0-9]+)",
	  "TrampolineCnt=([0-9]+)",
	  "Blocks=\\[([a-zA-Z]+( *, *[a-zA-Z]+)*)]"
      );
      final List<String> fields = List.of(
	  "ratio",
	  "playerCnt",
	  "trampolineCnt",
	  "blocks"
      );
      GameField.Properties result =
	  (GameField.Properties) createInstance(GameField.Properties.class);
      int index = 0;
      for (String pattern : patterns) {
	    var first = getFirstGroup(source, pattern);
	    if (first.isEmpty())
		  throw new IllegalStateException("Incorrect props params");
	    Field field = result.getClass().getDeclaredField(fields.get(index));
	    field.setAccessible(true);
	    switch (index) {
		  case 0 -> field.set(result, Float.parseFloat(first.get()));
		  case 1, 2 -> field.set(result, Integer.parseInt(first.get()));
		  case 3 -> {
			var items = fromString(first.get(), BrickBlock.Type::valueOf);
			if (items == null)
			      throw new IllegalStateException("Incorrect params");
			field.set(result, items.toArray(new BrickBlock.Type[0]));//List<Object> to List<BrickBlock.Type>.toArray();
		  }
	    }
	    index += 1;
      }
      return result;
}

public Serializer fromText(String content) throws ReflectiveOperationException {
      Serializer result = new Serializer();
      result.field = getFieldProperties(content);
      result.nameMapper = getNameMapper(content);
      getPlayers(content);
      return result;
}
}
