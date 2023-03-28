package com.baller.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.*;
import com.baller.game.field.GameField;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.baller.game.GameController.*;
import com.baller.game.players.Ball;
import com.baller.game.players.Player;
import com.baller.game.players.Players;
import com.baller.game.serializer.Serializer;

public class Game extends com.badlogic.gdx.Game {
public enum Stage {GameProcess, Settings, MainMenu, GamePause}

public static SpriteBatch batch;
Players players;
GameField field;
Settings settings;
GameController controller;
private Viewport viewport;
private Consumer<Float> renderHandler;

private AtomicBoolean isActive;

{
      isActive = new AtomicBoolean(true);
      renderHandler = this::defaultRenderHandler;
}

@Override
public void create() {
      viewport = new FitViewport(Globals.FIELD_WIDTH, Globals.FIELD_HEIGHT);
      batch = new SpriteBatch();
      initController();
}
private void load(){

}
private void save(){

}
private void initField(float dt) {
      players = new Players();
      var list = new Array<Player.Id>();
      var id = players.add("John");
      list.add(id);
      list.add(players.add("Ivan"));
      list.add(players.add("Ignat"));
      field = new GameField(players.getAll());
      //field.verifyAll(list.toArray(Player.Id.class), players::release);
      new Serializer().fromFile("text.back", Serializer.SerializationMode.Text);
      field.verify(id, players::release);
      field.setRatio(0.7f);
      field.setTrampolineCnt(id, 3);
      field.rebuild();
      saveProgress(null);
      renderHandler = this::dispatchField;
}

private void defaultRenderHandler(float dt) {
}

private void freezeField(float dt) {
      renderHandler = this::defaultRenderHandler;
      controller.addCallback(Event.OnScreenChange, this::pauseHandler);
      controller.addCallback(Event.OnProgressSave, this::saveProgress);
}

public Supplier<Boolean> guardRuler(Supplier<Boolean> handle) {
      return () -> {
	    if (!this.isActive.get())
		  return false;
	    return handle.get();
      };
}

private void saveProgress(Object handle) {
      Serializer serializer = new Serializer();
      Thread executor;
      try {
	    serializer
		.setPlayers(players)
		.setGameField(field);
	    Runnable task = () -> {
		  serializer.toFile("json.back", Serializer.SerializationMode.Json);
		  serializer.toFile("text.back", Serializer.SerializationMode.Text);
	    };
	    executor = new Thread(task);
      } catch (Exception e) {
	    System.out.println(e.getMessage());
	    return;
      }
      executor.start();
      controller
	  .sendMessage(
	      UserInterface.Message.Type.Process,
	      null,
	      guardRuler(executor::isAlive)
	  );
}

private void pauseHandler(Object rawScreen) {
      if (controller.getStage() != Stage.GamePause) {
	    controller.removeCallback(Event.OnScreenChange, this::pauseHandler);
	    controller.removeCallback(Event.OnProgressSave, this::saveProgress);
      }
}

private void initController() {
      controller = new GameController();
      controller.setViewport(viewport);
      controller.addCallback(Event.OnResolutionChange, this::changeResolution);
      controller.addCallback(Event.OnScreenChange, this::changeScreen);
      controller.addCallback(Event.OnProgramExit, this::onApplicationExit);
      controller.init();
}

private void onApplicationExit(Object handle) {
      Gdx.app.exit();
}

private void changeScreen(Object rawScreen) {
      super.setScreen(((Screen) rawScreen));
      switch (controller.getStage()) {
	    case GameProcess -> renderHandler = this::initField;
	    case GamePause -> renderHandler = this::freezeField;
	    case Settings -> {
	    }
	    case MainMenu -> {
	    }
      }
}

private void changeResolution(Object rawResolution) {
      Vector2 resolution = (Vector2) rawResolution;
      settings.setResolution(resolution.x, resolution.y);
}
private void dispatchPlayer(Player player) {
      Ball[] balls = player.getBalls();
      for (Ball ball : balls) {
	    GameField.Message msg = field.getMessage(ball);
	    player.dispatch(msg, ball);
      }
}

private void dispatchField(float dt) {
      for (Player.Properties properties : players.getVerified()) {
	    Optional<Player> player = players.get(properties.getId());
	    player.ifPresent(this::dispatchPlayer);
      }
      players.update(dt);
      field.update(dt);
}

@Override
public void render() {
      ScreenUtils.clear(0.3f, 0.4f, 0.7f, 1f);
      viewport.apply();
      batch.setProjectionMatrix(viewport.getCamera().combined);
      renderHandler.accept(Gdx.graphics.getDeltaTime());
      batch.begin();
      field.draw(batch);
      players.draw(batch);
      batch.end();
      super.render();
}

@Override
public void dispose() {
      batch.dispose();
      field.dispose();
      players.removeAll();
      isActive.set(false);
      Gdx.app.exit();
}
}
