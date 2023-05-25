package com.baller.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.*;
import com.baller.game.GameController.Event;
import com.baller.game.field.GameField;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import com.baller.game.players.Ball;
import com.baller.game.players.Player;
import com.baller.game.players.Players;
import com.baller.game.serializer.Proxy;
import com.baller.game.serializer.Serializer;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.baller.game.UserInterface.*;

public class Game extends com.badlogic.gdx.Game {
public enum Stage {
      GameProcess, Settings, MainMenu, GamePause;
      private Stage last;

      static {
	    GamePause.last = GameProcess;
	    Settings.last = GameProcess;
	    GameProcess.last = GameProcess;
	    MainMenu.last = GameProcess;
      }

      public Stage getLast() {
	    return last;
      }
}
private List<Pair<String, Integer>> statistics;
public enum SavedMode {User, Internal}

public enum HardnessLevel {
      Noobie, Veteran, Master, Crazy, Mad;
      private float ratio = 1.0f;

      static {
	    Noobie.ratio = 0.7f;
	    Veteran.ratio = 1.3f;
	    Master.ratio = 2.0f;
	    Crazy.ratio = 3.0f;
	    Mad.ratio = 4.0f;
      }

      public float ratio() {
	    return ratio;
      }
}

public enum ResolutionMode {
      Tiny, Small, Broad, Large, Full;
      private Point mode;

      static {
	    Tiny.mode = new Point(640, 480);
	    Small.mode = new Point(851, 640);
	    Broad.mode = new Point(1024, 768);
	    Large.mode = new Point(1280, 962);
	    Full.mode = Large.mode; //prevent memory leaks
      }

      public int width() {
	    return mode.x;
      }

      public int height() {
	    return mode.y;
      }
}

public static SpriteBatch batch;
private Players players;
private GameField field;
private Settings settings;
private GameController controller;
private Viewport viewport;
private Consumer<Float> renderHandler;
private AtomicBoolean isActive;
private AtomicBoolean isFieldCreated;
private AtomicBoolean isGameFinished;
private Long elapsedTime;
private Player.Id currentPlayer;

{
      isFieldCreated = new AtomicBoolean(false);
      isActive = new AtomicBoolean(true);
      isGameFinished = new AtomicBoolean(false);
      renderHandler = this::defaultRenderHandler;
      elapsedTime = 0L;
}

@Override
public void create() {
      viewport = new FitViewport(Globals.FIELD_WIDTH, Globals.FIELD_HEIGHT);
      batch = new SpriteBatch();
      statistics = new ArrayList<>();
      initController();
      initSettings();
}

private void initSettings() {
      settings = new Settings(ResolutionMode.Tiny);
      settings.setHardness(HardnessLevel.Noobie);
      settings.setLuckyLevel(Globals.CURR_LUCKY_LEVEL);
      changeResolution(ResolutionMode.Full);
}

private void initField(float dt) {
      if (isFieldCreated.get()) {
	    renderHandler = this::dispatchField;
	    return;
      }
      elapsedTime = 0L;
      players = new Players();
      var list = new Array<Player.Id>();
      var id = players.add("John");
      list.add(id);
      list.add(players.add("Ivan"));
      list.add(players.add("Ignat"));
      field = new GameField(players.getAll());
      if (field.verify(id)) {
	    players.release(id);
	    setPlayerName("John");
	    currentPlayer = id;
      }
      field.setRatio(Globals.FIELD_RATIO);
      field.rebuild();
      settings.setSkin("White");
      isFieldCreated.set(true);
      renderHandler = this::dispatchField;
}

Pair<File, File> getBackFiles(SavedMode mode) {
      Pair<File, File> pair;
      if (mode == SavedMode.User) {
	    pair = new Pair<>(Settings.getJsonBackPath().toFile(), Settings.getTxtBackPath().toFile());
      } else {
	    pair = new Pair<>(Settings.getJsonAutoBackPath().toFile(), Settings.getTxtAutoBackPath().toFile());
      }
      return pair;
}

/**
 * @return not empty Serializer if it exists
 * At another way, return empty
 * This convention is applicable for save method
 */
private Optional<Serializer> findSavedGame(SavedMode mode) {
      File jsonBack;
      File txtBack;
      Pair<File, File> filePair = getBackFiles(mode);
      jsonBack = filePair.getValue0();
      txtBack = filePair.getValue1();
      Optional<Serializer> serializer = Optional.empty();
      if (txtBack.exists()) {
	    serializer = Proxy.fromFile(txtBack.getAbsolutePath(), Proxy.SerializationMode.Text);
      }
      if (serializer.isEmpty() && jsonBack.exists()) {
	    serializer = Proxy.fromFile(jsonBack.getAbsolutePath(), Proxy.SerializationMode.Json);
      }
      return serializer;
}

private void load(@Nullable Object rawMode) {
      SavedMode mode = SavedMode.User;
      if (rawMode != null)
	    mode = (SavedMode) rawMode;
      var serializer = findSavedGame(mode);
      serializer.ifPresentOrElse(s -> {
	    this.field = s.field.construct();
	    this.statistics = s.getStatistics();
	    this.elapsedTime = s.getElapsedTime();
	    controller.setScoreTable(statistics);
	    this.field.addPlayers(s.players);
	    this.players = new Players(s.players, s.nameMapper);
	    var id = s.players[0].getId();
	    if (this.field.verify(id)) {
		  players.release(id);
		  currentPlayer = id;
		  setPlayerName(players.getName(id));
	    }
	    this.field.rebuild();
	    this.settings = s.settings.construct();
	    if (this.settings != null) {
		  changeLuckyLevel(settings.getLuckyLevel());
		  changeResolution(settings.getResolution());
		  changeGameMode(settings.getHardness());
	    }
	    controller.hideMessage();
	    isFieldCreated.set(true);
	    controller.removeCallback(Event.OnProgressSave, this::load);
      }, () -> System.out.println("NO CONFIG FILE"));
}
private void debugRender(float delta) {
      if (Gdx.input.isButtonPressed(Input.Keys.LEFT)) {

      }
}
/**
 * @param rawMode is any handle that will be skipped
 */
private void save(Object rawMode) {
      SavedMode mode = SavedMode.User;
      if (rawMode != null)
	    mode = (SavedMode) rawMode;
      Pair<File, File> filePair = getBackFiles(mode);
      Serializer serializer = new Serializer();
      Thread executor;
      try {
	    serializer
		.setPlayers(players)
		.setGameField(field)
		.setSettings(settings)
		.setStatistics(statistics)
		.setElapsedTime(elapsedTime);
	    Proxy proxy = new Proxy(serializer);
	    Runnable task = () -> {
		  proxy.toFile(filePair.getValue0().toString(), Proxy.SerializationMode.Json);
		  proxy.toFile(filePair.getValue1().toString(), Proxy.SerializationMode.Text);
	    };
	    executor = new Thread(task);
      } catch (Exception e) {
	    System.out.println(e.getMessage());
	    return;
      }
      executor.start();
}

private void setPlayerName(@NotNull String name) {
      Globals.PLAYER_NAME = name;
}

private void defaultRenderHandler(float dt) {
}

private void freezeField(float dt) {
      renderHandler = this::renderField;
      controller.addCallback(Event.onStageChange, this::pauseHandler);
      controller.addCallback(Event.OnProgressSave, this::save);
      controller.addCallback(Event.OnProgressRestore, this::load);
}

private void pauseHandler(Object rawScreen) {
      if (controller.getStage() != Stage.GamePause) {
	    controller.removeCallback(Event.onStageChange, this::pauseHandler);
	    controller.removeCallback(Event.OnProgressSave, this::save);
	    controller.removeCallback(Event.OnProgressSave, this::load);
      }
}

private void initController() {
      controller = new GameController(Stage.GameProcess);
      controller.setViewport(viewport);
      controller.addCallback(Event.OnResolutionChange, this::changeResolution);
      controller.addCallback(Event.onStageChange, this::onChangeStage);
      controller.addCallback(Event.OnProgramExit, this::onApplicationExit);
      controller.addCallback(Event.onGameRebuild, this::rebuildGame);
      controller.init();
}

private void onApplicationExit(Object handle) {
      Gdx.app.exit();
}

private void rebuildGame(@Nullable Object handle) {
      changeGameMode(handle);
      changeFieldRatio(handle);
      changeResolution(handle);
      changeLuckyLevel(handle);
      isFieldCreated.set(false);
      renderHandler = this::initField;
}

private void onChangeStage(Object rawScreen) {
      if (super.screen != rawScreen)
	    super.setScreen(((Screen) rawScreen));
      switch (controller.getStage()) {
	    case GameProcess -> renderHandler = this::initField;
	    case GamePause -> renderHandler = this::freezeField;
	    case Settings -> renderHandler = this::freezeField;
	    case MainMenu -> {
	    }
      }
}


private void changeGameMode(@Nullable Object rawMode) {
      HardnessLevel mode;
      if (rawMode != null) {
	    mode = (HardnessLevel) rawMode;
	    Globals.CURR_MODE_INDEX = mode.ordinal();
      } else {
	    mode = HardnessLevel.values()[Globals.CURR_MODE_INDEX];
	    players.forEach(Player.State.Alive, pl -> pl.boost(mode));
      }
      settings.setHardness(mode);
}

private void changeFieldRatio(@Nullable Object value) {
      if (value != null) {
	    Float ratio = (Float) value;
	    field.setRatio(ratio);
	    Globals.FIELD_RATIO = ratio;
      } else {
	    field.setRatio(Globals.FIELD_RATIO);
      }
      field.rebuild();
}

private void changeResolution(@Nullable Object rawMode) {
      ResolutionMode mode;
      if (rawMode != null) {
	    mode = (ResolutionMode) rawMode;
	    Globals.CURR_SCREEN_INDEX = mode.ordinal();
      } else {
	    mode = ResolutionMode.values()[Globals.CURR_SCREEN_INDEX];
      }
      if (mode != ResolutionMode.Full) {
	    Gdx.graphics.setWindowedMode(mode.width(), mode.height());
      } else {
	    Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
      }
      settings.setResolution(mode);
}

private void changeLuckyLevel(@Nullable Object value) {
      if (value != null) {
	    Globals.CURR_LUCKY_LEVEL = (Float) value;
      }
      settings.setLuckyLevel(Globals.CURR_LUCKY_LEVEL);
}

private void dispatchPlayer(Player player) {
      Ball[] balls = player.getBalls();
      field.mark(player.getId());
      for (Ball ball : balls) {
	    List<GameField.Message> messages = field.getMessage(ball);
	    player.dispatchAll(messages, ball);
      }
      player.commit();
      controller.setScore(player.getScore());
      field.release();
}

private void renderField(float dt) {
      batch.begin();
      field.draw(batch);
      players.draw(batch);
      batch.end();
}

private void restartHandler(@Nullable Object handle) {
      if (isFieldCreated.get()) {
	    isFieldCreated.set(false);
	    renderHandler = this::initField;
	    controller.hideMessage();
	    controller.removeCallback(Event.OnGameRestart, this::restartHandler);
      }
}

private void finishHandler(Players.GameResult result) {
      if (isGameFinished.get()) {
	    return;
      }
      isGameFinished.set(true);
      renderHandler = this::renderField;
      controller.addCallback(Event.OnGameRestart, this::restartHandler);
      if (result == Players.GameResult.Victory) {
	    assert currentPlayer != null;
	    Optional<Player> player = players.get(currentPlayer);
	    player.ifPresent(p -> {
		  String name = players.getName(currentPlayer);
		  Integer score = p.getScore();
		  boolean wasFound = false;
		  for (var pair : statistics) {
			wasFound = pair.getValue0().equals(name) & pair.getValue1().equals(score);
			if (wasFound)
			      break;
		  }
		  if (!wasFound) {
			statistics.add(new Pair<>(name, score));
		  }
	    });
	    controller.setScoreTable(statistics);
	    controller.sendMessage(Message.Type.Victory, "Victory", "Play again!");
      } else {
	    controller.sendMessage(Message.Type.Defeat, "Defeat", "Try again...");
      }
}

private void dispatchField(float dt) {
      final float MAX_DELTA = 0.15f;
      dt = Math.min(MAX_DELTA, dt);
      for (Player.Properties properties : players.getVerified()) {
	    Optional<Player> player = players.get(properties.getId());
	    player.ifPresent(this::dispatchPlayer);
      }
      var result = players.getGameResult();
      if (result != Players.GameResult.InProgress) {
	    isGameFinished.set(false);
	    finishHandler(result);
      }
      elapsedTime += (long) (dt * 1000); //convert seconds to millis
      controller.setBrickCounter(field.getRestCounter());
      controller.setElapsedTime(elapsedTime);
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
