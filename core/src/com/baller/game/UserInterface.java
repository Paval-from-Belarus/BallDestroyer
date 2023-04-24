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
import java.util.function.BiConsumer;
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
}
private void initClicks(){
	for(var id : UserClick.Id.values()){
	      var click = new UserClick(id);
	      click.setBubbles(this::appendMessage);
	      click.setRocks((handler)->appendRocksHandler(click.getId(), handler));
	      clicksMapper.put(id, click);
	}
}
private void appendRocksHandler(UserClick.Id id, Consumer<Object> rocksHandler){
      rocksMapper.put(id, rocksHandler);
}
private void appendMessage(UserClick.Id id){
      messages.add(id);
}
public void setViewport(Viewport viewport){
	this.viewport = viewport;
}
public static class UserClick {
      public enum Id {BTN_GAME_PAUSE, BTN_GAME_SAVE, BTN_GAME_RESUME, BTN_GAME_RESTORE, LBL_GAME_SCORE, BtnSettings, BtnMainMenu}

      public Id getId() {
	    return value;
      }
      public UserClick(Id id) {
	    this.value = id;
      }
      public UserClick setRocks(Consumer<Consumer<Object>> callback){
	    this.callable = callback;
	    return this;
      }
      public UserClick setBubbles(Consumer<Id> task){
	    this.task = task;
	    return this;
      }

      public void bubbles(){task.accept(value);}
      public Consumer<Consumer<Object>> rocks(){
	    return callable;
      }
      private final Id value;
      private Consumer<Consumer<Object>> callable = o->{};
      private Consumer<Id> task = (id)->{};
      @Override
      public boolean equals(Object other){
	    boolean result = false;
	    if(other instanceof UserClick)
		  result = ((UserClick) other).value.equals(this.value);
	    return result;
      }
}

public static enum ScreenType {Game, Settings, MainMenu}

public static class MessageInfo {
      public String title;
      public Object info; //for different Messages ― different
}

public static class Message {
      public enum Type {Defeat, Victory, NetworkLost, Process, Warning}

      private Texture texture;
      private Button button;
      private Dialog dialog;

      public void draw(SpriteBatch batch) {

      }
}

public void showMessage(Message.Type type, MessageInfo info) {

}

public void hideMessage() {

}

public void setScreen(ScreenType type) {
      switch (type) {
	    case Game -> mainScreen = new GameScreen(this.skin, this.viewport);
	    case Settings -> mainScreen = new SettingsScreen();
	    case MainMenu -> mainScreen = new MainMenuScreen();
      }
      mainScreen.acceptClicks(clicksMapper);
}

/**
 * set specific attribute of Screen Component according UserClick
 */
public void setComponent(UserClick.Id id, Object value) {
      var handler = rocksMapper.get(id);
      if(handler != null){
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

public Screen getScreen() {
      return mainScreen;
}

private ClickScreen mainScreen;
}
