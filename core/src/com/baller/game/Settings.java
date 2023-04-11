package com.baller.game;

import com.baller.game.serializer.AbstractSerializable;
import com.baller.game.serializer.Serializable;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.nio.file.Path;
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
private static final String JSON_BACK_FILE = "game.back.json";
private static final String TEXT_BACK_FILE = "game.back.txt";
public static Path getJsonBackPath(){
      return Path.of(JSON_BACK_FILE);
}
public static Path getTxtBackPath(){
      return Path.of(TEXT_BACK_FILE);
}
private Settings() {
      resolution = new Resolution(0, 0);
}
public Settings(float width, float height) {
      this();
      setResolution(width, height);
}
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

public static class Properties extends AbstractSerializable<Settings> {
      private float soundLevel;
      private int hardness;
      private Resolution resolution;
      private String skinName;

      public Properties() {
	    var patterns = List.of(
		"Resolution=(.+)", "Hardness=(.+)", "SoundLevel=(.+)", "SkinName=(.+)");
	    List<Consumer<String>> handlers = List.of(this::setResolution, this::setHardness,
		this::setSound, this::setSkin);
	    addPatterns(patterns);
	    addHandlers(handlers);
	    setEmptyState(true);
      }
      private Properties(Settings owner){
	    this();
	    this.hardness = owner.hardness;
	    this.soundLevel = owner.soundLevel;
	    this.resolution = owner.resolution;
	    this.skinName = owner.skinName;
	    setEmptyState(false);
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
      public @Nullable Settings construct() {
	    if (isEmpty())
		  return null;
	    Settings parent = new Settings();
	    parent.setSkin(this.skinName);
	    parent.setSoundLevel(this.soundLevel);
	    parent.setResolution(this.resolution.width, this.resolution.height);
	    parent.setHardness(this.hardness);
	    return parent;
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

}
