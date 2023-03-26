package com.baller.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
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

private void initField(float dt) {
      players = new Players();
      var id = players.add("John");
      field = new GameField(players.getAll());
      field.setRatio(0.7f);
      field.setTrampolineCnt(id, 1);
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
      GameField.Properties field = this.field.getProperties();
      Player.Properties[] properties = players.getAll();
      Serializer serializer = new Serializer();
      try {
	    serializer.setPlayers(players);
      } catch (NoSuchFieldException | IllegalAccessException e) {
	    System.out.println(e.getMessage());
	    return;
      }
      Runnable task = () -> {
	    serializer.start("json.back", Serializer.SerializationMode.Json);
	    serializer.start("text.back", Serializer.SerializationMode.Text);
      };
      Thread runnable = new Thread(task);
      runnable.start();
      controller.sendMessage(UserInterface.Message.Type.Process, null,
	  guardRuler(runnable::isAlive)
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
      for (Player.Properties properties : players.getAll()) {
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
      try {
	    renderHandler.accept(Gdx.graphics.getDeltaTime());
      }
      catch (Exception e){
	    dispose();
      }
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
}
}
