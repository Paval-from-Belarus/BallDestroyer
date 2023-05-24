package com.baller.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.baller.game.screens.ClickScreen;
import com.baller.game.screens.GameScreen;
import com.baller.game.screens.MainMenuScreen;
import com.baller.game.screens.SettingsScreen;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public class UserInterface {
private Skin skin;
private Viewport viewport;
private final Map<UserClick.Id, UserClick> clicksMapper;
private final Map<UserClick.Id, Consumer<Object>> rocksMapper;
private final HashSet<UserClick.Id> messages;

UserInterface() {
      this.skin = new Skin(Gdx.files.internal("buttonsPack/buttons_pack.json"));
      this.clicksMapper = new HashMap<>();
      this.messages = new HashSet<>(UserClick.Id.values().length);
      this.rocksMapper = new HashMap<>();
      initClicks();
      Globals.GAME_SKIN = this.skin;
}

private void initClicks() {
      for (var id : UserClick.Id.values()) {
	    var click = new UserClick(id);
	    click.setBubbles(this::appendMessage);
	    click.setRocks((handler) -> appendRocksHandler(click.getId(), handler));
	    clicksMapper.put(id, click);
      }
}

private void appendRocksHandler(UserClick.Id id, Consumer<Object> rocksHandler) {
      rocksMapper.put(id, rocksHandler);
}

private void appendMessage(UserClick.Id id) {
      messages.add(id);
}

public void setViewport(Viewport viewport) {
      this.viewport = viewport;
}

@FunctionalInterface
public interface RocksHandler {
      void accept(Consumer<Object> rocks);
}

public static class UserClick {
      public enum Id {
	    BTN_GAME_PAUSE, BTN_GAME_SAVE, BTN_GAME_RESUME, BTN_GAME_RESTORE, LBL_GAME_SCORE, UI_RESOLUTION,
	    BTN_SETTINGS_SCREEN, BTN_HARDNESS_LEVEL, BTN_FORTUNE_LEVEL, BTN_DISCARD_SETTINGS, BTN_FIELD_RATIO, BTN_LUCKY,
	    BTN_ACCEPT_SETTINGS, MSG_GAME_PROCESS, BTN_EXIT, BTN_STATISTICS,
	    BtnSettings, BtnMainMenu
      }

      public Id getId() {
	    return value;
      }

      public UserClick(Id id) {
	    this.value = id;
      }

      public UserClick setRocks(RocksHandler callback) {
	    this.callable = callback;
	    return this;
      }

      public UserClick setBubbles(Consumer<Id> task) {
	    this.task = task;
	    return this;
      }

      public void bubbles() {
	    task.accept(value);
      }

      public RocksHandler rocks() {
	    return callable;
      }

      private final Id value;
      private RocksHandler callable = o -> {
      };
      private Consumer<Id> task = (id) -> {
      };

      @Override
      public boolean equals(Object other) {
	    boolean result = false;
	    if (other instanceof UserClick)
		  result = ((UserClick) other).value.equals(this.value);
	    return result;
      }
}

public enum ScreenType {Game, Settings, MainMenu}

public static class MessageInfo {
      public MessageInfo(Message.Type type, @NotNull String title,@NotNull String text) {
	    this.type = type;
	    this.title = title;
	    this.text = text;
      }

      public Message.Type type;
      public String title;
      public String text; //for different Messages ― different
}

public static class Message {
      public enum Type {Defeat, Victory, NetworkLost, Process, Warning}
}

public void showMessage(MessageInfo info) {
	mainScreen.showMessage(info);
}

public void hideMessage() {
      mainScreen.hideMessage();
}

public void setScreen(ScreenType type) {
      switch (type) {
	    case Game -> mainScreen = new GameScreen(this.skin, this.viewport);
	    case Settings -> mainScreen = new SettingsScreen(this.skin, this.viewport);
	    case MainMenu -> mainScreen = new MainMenuScreen();
      }
      screenType = type;
      mainScreen.acceptClicks(clicksMapper);
}

/**
 * set specific attribute of Screen Component according UserClick
 */
public void setComponent(UserClick.Id id, Object value) {
      var handler = rocksMapper.get(id);
      if (handler != null) {
	    handler.accept(value);
      } else {
	    System.out.println("Handler on " + id.toString() + " is not set");
      }
}

public @NotNull List<UserClick.Id> getUserClick() {
      List<UserClick.Id> list = new ArrayList<>(messages);
      messages.clear();
      return list;
}

public ScreenType getScreenType() {
      return screenType;
}

public Screen getScreen() {
      return mainScreen;
}

private ScreenType screenType;
private ClickScreen mainScreen;
}
