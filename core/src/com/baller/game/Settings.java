package com.baller.game;

import com.baller.game.serializer.Serializable;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

//import com.google.gson.Gson;
public class Settings {
private Settings() {
      resolution = new Resolution(0, 0);
}

public Settings(float width, float height) {
      this();
      setResolution(width, height);
}

public static class Resolution {
      public float width;
      public float height;

      Resolution(float width, float height) {
	    this.width = width;
	    this.height = height;
      }

      private void update(float width, float height) {
	    this.width = width;
	    this.height = height;
      }

      @Override
      public String toString() {
	    return "(" + width + ", " + height + ")";
      }
}

public static class Properties implements Serializable<Settings> {
      private static List<String> patterns = List.of(
	  "Resolution", "Hardness", "SoundLevel", "SkinName");
      private float soundLevel;
      private int hardness;
      private Resolution resolution;
      private String skinName;
      private boolean isEmpty;

      public Properties() {
	    isEmpty = true;

      }
      private Properties(Settings owner){
	    this.hardness = owner.hardness;
	    this.soundLevel = owner.soundLevel;
	    this.resolution = owner.resolution;
	    this.skinName = owner.skinName;
	    isEmpty = false;
      }

      @Override
      public String toString() {
	    return "Resolution=" + resolution.toString() + "\n" +
		       "Hardness=" + hardness + "\n" +
		       "SoundLevel=" + soundLevel + "\n" +
		       "SkinName=" + skinName + "\n";
      }

      @Override
      public String serialize() {
	    return this.toString();
      }


      @Override
      public void deserialize(String serialized) {
	    List<Matcher> matchers = patterns.stream()
					 .map(pattern -> Pattern.compile(pattern + "=(.|\n)+"))
					 .map(pattern -> pattern.matcher(serialized))
					 .collect(Collectors.toList());
	    List<Consumer<String>> handlers = List.of(this::setResolution, this::setHardness,
		this::setSound, this::setSkin);
	    boolean holdsGroups = true;
	    int index = 0;
	    for (Matcher matcher : matchers) {
		  if (!matcher.find() || matcher.groupCount() == 0) {
			holdsGroups = false;
			break;
		  }
		  try {
			handlers.get(index).accept(matcher.group(1));
		  } catch (NumberFormatException e) {
			holdsGroups = false;
			break;
		  }
	    }
	    if (holdsGroups) {
		  isEmpty = false;
	    } else {
		  isEmpty = true;
		  clear();
	    }
      }

      @Override
      public boolean isEmpty() {
	    return isEmpty;
      }

      @Override
      public @Nullable Settings construct() {
	    if (isEmpty)
		  return null;
	    Settings parent = new Settings();
	    parent.setSkin(this.skinName);
	    parent.setSoundLevel(this.soundLevel);
	    parent.setResolution(this.resolution.width, this.resolution.height);
	    parent.setHardness(this.hardness);
	    return parent;
      }

      private void clear() {
	    this.resolution = null;
	    this.skinName = null;
      }

      private void setResolution(String source) throws NumberFormatException {
	    Pattern pattern = Pattern.compile("\\(([0-9\\.]+),([0-9\\.]+)\\)");
	    Matcher matcher = pattern.matcher(source);
	    if (matcher.groupCount() < 2)
		  throw new NumberFormatException("Illegal resolution format");
	    float width = Float.parseFloat(matcher.group(1));
	    float height = Float.parseFloat(matcher.group(2));
	    this.resolution = new Resolution(width, height);
      }

      private void setHardness(String source) {
	    this.hardness = Integer.parseInt(source);
      }

      private void setSound(String source) {
	    this.soundLevel = Integer.parseInt(source);
      }

      private void setSkin(String source) {
	    this.skinName = source;
      }
}


private float soundLevel;
private int hardness;
private final Resolution resolution;
private String skinName;

public void setResolution(float width, float height) {
      this.resolution.update(width, height);
}

public void setHardness(int level) {
      this.hardness = level;
}

public void setSkin(String skinName) {
      this.skinName = skinName;
}

/**
 * @param level relative value from 0 to 1
 * @throws IllegalArgumentException if values not in defined range
 */
public void setSoundLevel(float level) {
      if (level < 0f || level > 1f)
	    throw new IllegalArgumentException("Incorrect sound level");
      this.soundLevel = level;
}

public Properties serializer() {
      return new Properties(this);
}

}
