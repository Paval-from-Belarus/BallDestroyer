package com.baller.game.serializer;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.SequenceInputStream;
import java.io.Serial;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class Proxy {
public Proxy(Serializer father) {
      this.father = father;
}
private final Serializer father;
public Serializer getSerializer(){
      return father;
}
public static final String DEFAULT_NAME = "game.back";
public enum SerializationMode {Json, Text}
public static Optional<Serializer> fromFile(@NotNull String sourceName, @NotNull SerializationMode mode) {
      Path path = Path.of(sourceName);
      Serializer father = null;
      try {
	    String content = Files.readString(path);
	    if (mode == SerializationMode.Json) {
		  father = new Gson().fromJson(content, Serializer.class);
	    } else {
		  father = new TextSerializer().fromText(content);
	    }
      } catch (Exception e) {
	    e.printStackTrace();
      }
      return Optional.ofNullable(father);
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
private void saveAsJson(Path destPath) throws IOException {
      Gson gson = new Gson();
      String text = gson.toJson(father);
      Files.writeString(destPath, text);
}

private void saveAsText(Path destPath) throws IOException {
      TextSerializer text = new TextSerializer();
      String content = text.toText(father);
      Files.writeString(destPath, content);
}
}
