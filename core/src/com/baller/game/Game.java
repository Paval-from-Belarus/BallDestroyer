package com.baller.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.*;
import com.baller.game.GameController.Event;
import com.baller.game.field.GameField;

import java.awt.*;
import java.io.File;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.baller.game.players.Ball;
import com.baller.game.players.Player;
import com.baller.game.players.Players;
import com.baller.game.serializer.Serializer;

public class Game extends com.badlogic.gdx.Game {
public enum Stage {GameProcess, Settings, MainMenu, GamePause}
public enum HardnessLevel {Easy, Hard, Mad}
public static SpriteBatch batch;
Players players;
GameField field;
Settings settings;
GameController controller;
private Viewport viewport;
private Consumer<Float> renderHandler;

private AtomicBoolean isActive;
private AtomicBoolean isFieldCreated;

{
      isFieldCreated = new AtomicBoolean(false);
      isActive = new AtomicBoolean(true);
      renderHandler = this::defaultRenderHandler;
}

@Override
public void create() {
      viewport = new FitViewport(Globals.FIELD_WIDTH, Globals.FIELD_HEIGHT);
      batch = new SpriteBatch();
      initController();
      initSettings();
}
private void initSettings(){
      settings = new Settings(Globals.FIELD_WIDTH, Globals.FIELD_HEIGHT);
      settings.setResolution(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
      settings.setHardness(HardnessLevel.Easy.ordinal());
      settings.setSoundLevel(0.3f);
}
private void initField(float dt) {
      if(isFieldCreated.get()){
	    renderHandler = this::dispatchField;
	    return;
      }
      players = new Players();
      var list = new Array<Player.Id>();
      var id = players.add("John");
      list.add(id);
      list.add(players.add("Ivan"));
      list.add(players.add("Ignat"));
      field = new GameField(players.getAll());
      if(field.verify(id)){
	    players.release(id);
      }
      field.setRatio(0.8f);
      field.setTrampolineCnt(id, 3);
      players.get(id).ifPresent(player -> player.setTrampolineCnt(3));
      field.rebuild();
      settings.setSkin("White");
      isFieldCreated.set(true);
      renderHandler = this::dispatchField;
}

/**
 * @return not empty Serializer if it exist
 * At another way, return empty
 * This convention is applicable for save method
 * */
private Optional<Serializer> findSavedGame(){
      File jsonBack = Settings.getJsonBackPath().toFile();
      File txtBack = Settings.getTxtBackPath().toFile();
      Serializer serializer = new Serializer();
      if(txtBack.exists() && serializer.fromFile(txtBack.getAbsolutePath(), Serializer.SerializationMode.Text)){
	    return Optional.of(serializer);
      }
      if(jsonBack.exists() && serializer.fromFile(jsonBack.getAbsolutePath(), Serializer.SerializationMode.Json)){
	    return Optional.of(serializer);
      }
      return Optional.empty();
}
private void load(Object handle){
      var serializer = findSavedGame();
      serializer.ifPresentOrElse(s -> {
	    this.field = s.field.construct();
	    this.field.addPlayers(s.players);
	    this.players = new Players(s.players, s.nameMapper);
	    if(this.field.verify(s.players[0].getId())){
		  players.release(s.players[0].getId());
	    }
	    this.field.rebuild();
	    this.settings = s.settings.construct();
	    isFieldCreated.set(true);
      }, () -> System.out.println("NO CONFIG FILE"));
}
/**@param handle is any handle that will be skipped
 * */
private void save(Object handle) {
      Serializer serializer = new Serializer();
      Thread executor;
      try {
	    serializer
		.setPlayers(players)
		.setGameField(field)
		.setSettings(settings);
	    Runnable task = () -> {
		  serializer.toFile(Settings.getJsonBackPath().toString(), Serializer.SerializationMode.Json);
		  serializer.toFile(Settings.getTxtBackPath().toString(), Serializer.SerializationMode.Text);
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

private void defaultRenderHandler(float dt) {}

private void freezeField(float dt) {
      renderHandler = this::renderField;
      controller.addCallback(Event.onStageChange, this::pauseHandler);
      controller.addCallback(Event.OnProgressSave, this::save);
      controller.addCallback(Event.OnProgressRestore, this::load);
}

public Supplier<Boolean> guardRuler(Supplier<Boolean> handle) {
      return () -> {
	    if (!this.isActive.get())
		  return false;
	    return handle.get();
      };
}
private void pauseHandler(Object rawScreen) {
      if (controller.getStage() != Stage.GamePause) {
	    controller.removeCallback(Event.onStageChange, this::pauseHandler);
	    controller.removeCallback(Event.OnProgressSave, this::save);
	    controller.removeCallback(Event.OnProgressSave, this::load);
      }
}

private void initController() {
      controller = new GameController();
      controller.setViewport(viewport);
      controller.addCallback(Event.OnResolutionChange, this::changeResolution);
      controller.addCallback(Event.onStageChange, this::onChangeStage);
      controller.addCallback(Event.OnProgramExit, this::onApplicationExit);
      controller.init();
}

private void onApplicationExit(Object handle) {
      Gdx.app.exit();
}

private void onChangeStage(Object rawScreen) {
      if(super.screen != rawScreen)
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
      Point resolution = (Point) rawResolution;
      settings.setResolution(resolution.x, resolution.y);
}
private void dispatchPlayer(Player player) {
      Ball[] balls = player.getBalls();
      for (Ball ball : balls) {
	    GameField.Message msg = field.getMessage(ball);
	    player.dispatch(msg, ball);
      }
}
private void renderField(float dt){
      batch.begin();
      field.draw(batch);
      players.draw(batch);
      batch.end();
}
private void dispatchField(float dt) {
      final float MAX_DELTA = 0.15f;
      dt = Math.min(MAX_DELTA, dt);
      for (Player.Properties properties : players.getVerified()) {
	    Optional<Player> player = players.get(properties.getId());
	    player.ifPresent(this::dispatchPlayer);
      }
      players.update(dt);
      field.update(dt);
      renderField(dt);
}

@Override
public void render() {
      ScreenUtils.clear(0.3f, 0.4f, 0.7f, 1f);
      viewport.apply();
      batch.setProjectionMatrix(viewport.getCamera().combined);
      controller.dispatchInput();
      renderHandler.accept(Gdx.graphics.getDeltaTime());
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
