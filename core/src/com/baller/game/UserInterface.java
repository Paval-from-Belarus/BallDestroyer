package com.baller.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.baller.game.screens.GameScreen;
import com.baller.game.screens.MainMenuScreen;
import com.baller.game.screens.SettingsScreen;

import java.util.List;

public class UserInterface {
private Skin skin;
private Queue<UserClick> messageQueue;
private Viewport viewport;

UserInterface(GameController controller) {
      this.controller = controller;
      this.skin = new Skin(Gdx.files.internal("buttonsPack/buttons_pack.json"));
      messageQueue = new Queue<>();
}

public void setViewport(Viewport viewport){
	this.viewport = viewport;
}
public static class UserClick {
      public enum Type {Move, Release, Pressed, Touched}

      public enum Id {BtnPause, BtnSettings, BtnMainMenu}

      public Id getId() {
	    return value;
      }

      public Type getType() {
	    return type;
      }

      public UserClick(Type type, Id id) {
	    this.type = type;
	    this.value = id;
      }

      private Type type;
      private Id value;
}

public static enum ScreenType {Game, Settings, MainMenu}

public static class MessageInfo {
      public String title;
      public Object info; //for different Messages ― different
}

public static class Message {
      public enum Type {Defeat, Victory, NetworkLost, Process}

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
}

/**
 * set specific attribute of Screen Component according UserClick
 */
public void setComponent(UserClick handle, Object value) {
}
public List<UserClick> getUserClick() {
      return null;
}

public Screen getScreen() {
      return mainScreen;
}

private Screen mainScreen;
private GameController controller;
}
