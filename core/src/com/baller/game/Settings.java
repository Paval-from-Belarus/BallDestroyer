package com.baller.game;

import com.baller.game.serializer.AbstractSerializable;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

import static com.baller.game.Game.*;

//import com.google.gson.Gson;
public class Settings {
private static final String JSON_BACK_FILE = "game.back.json";
private static final String TEXT_BACK_FILE = "game.back.txt";
private static final String TEXT_INTERNAL_BACK_FILE = "auto.back.txt";
private static final String JSON_INTERNAL_BACK_FILE = "auto.back.json";
public static Path getJsonBackPath(){
      return Path.of(JSON_BACK_FILE);
}
public static Path getTxtBackPath(){
      return Path.of(TEXT_BACK_FILE);
}
public static Path getTxtAutoBackPath(){
      return Path.of(TEXT_INTERNAL_BACK_FILE);
}
public static Path getJsonAutoBackPath(){
      return Path.of(JSON_INTERNAL_BACK_FILE);
}
private Settings() {
}
public Settings(ResolutionMode mode) {
      setResolution(mode);
}
public void setResolution(ResolutionMode mode) {
      this.resolution = mode;
}

public void setHardness(HardnessLevel level) {
      this.hardness = level;
}

public void setSkin(String skinName) {
      this.skinName = skinName;
}

/**
 * @param level relative value from 0 to 1
 * @throws IllegalArgumentException if values not in defined range
 */
public void setLuckyLevel(float level) {
      if (level < 0f || level > 1f)
	    throw new IllegalArgumentException("Incorrect sound level");
      this.luckyLevel = level;
}
public float getLuckyLevel(){
      return luckyLevel;
}
public HardnessLevel getHardness(){
      return hardness;
}
public ResolutionMode getResolution(){
      return resolution;
}
public Properties serializer() {
      return new Properties(this);
}

public static class Properties extends AbstractSerializable<Settings> {
      private float luckyLevel;
      private HardnessLevel hardness;
      private ResolutionMode resolution;
      private String skinName;

      public Properties() {
	    var patterns = List.of(
		"Resolution=(.+)", "Hardness=(.+)", "LuckyLevel=(.+)", "SkinName=(.+)");
	    List<Consumer<String>> handlers = List.of(this::setResolution, this::setHardness,
		this::setLuckyLevel, this::setSkin);
	    addPatterns(patterns);
	    addHandlers(handlers);
	    setEmptyState(true);
      }
      private Properties(Settings owner){
	    this();
	    this.hardness = owner.hardness;
	    this.luckyLevel = owner.luckyLevel;
	    this.resolution = owner.resolution;
	    this.skinName = owner.skinName;
	    setEmptyState(false);
      }

      @Override
      public String toString() {
	    return "Resolution=" + resolution.toString() + "\n" +
		       "Hardness=" + hardness + "\n" +
		       "LuckyLevel=" + luckyLevel + "\n" +
		       "SkinName=" + skinName + "\n";
      }

      @Override
      public String serialize() {
	    return this.toString();
      }

      @Override
      public @Nullable Settings construct() {
//	    if (isEmpty())
//		  return null;
	    Settings parent = new Settings();
	    parent.setSkin(this.skinName);
	    parent.setLuckyLevel(this.luckyLevel);
	    parent.setResolution(this.resolution);
	    parent.setHardness(this.hardness);
	    return parent;
      }

      @Override
      public String[] getFieldNames() {
	    return new String[]{"luckyLevel", "hardness", "resolution", "skinName"};
      }

      private void setResolution(String source) throws NumberFormatException {
	    this.resolution = ResolutionMode.valueOf(source);
      }

      private void setHardness(String source) {
	    this.hardness = HardnessLevel.valueOf(source);
      }

      private void setLuckyLevel(String source) {
	    this.luckyLevel = Float.parseFloat(source);
      }

      private void setSkin(String source) {
	    this.skinName = source;
      }
}

private float luckyLevel;
private HardnessLevel hardness;
private ResolutionMode resolution;
private String skinName;

}
