package com.baller.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.baller.game.UserInterface.*;

import javax.security.auth.callback.TextInputCallback;

public class GameController {
/**<code>OnScreenChange</code> event corrupted when user change current menu. In this case, handle object
 * is <code>UserInterface.ScreenType</code>
 * */
public enum Event {OnResolutionChange, OnScreenChange, OnProgressSave, OnSkinChange, OnProgramExit}

private Game.Stage stage;
private UserInterface ui;
private Map<Event, List<Consumer<Object>>> callbackMap;

GameController() {
      callbackMap = new HashMap<>();
      ui = new UserInterface(this);
}

public void setViewport(Viewport port) {
      ui.setViewport(port);
}

public void dispatchInput() {
      List<UserClick> clicks = ui.getUserClick();
      for (UserClick click : clicks) {

      }
}

public void sendMessage(Message.Type type, MessageInfo info, Supplier<Boolean> ruleHandler) {
      ui.showMessage(type, info);
      Thread executor = new Thread(() -> loopHandler(ruleHandler, ui::hideMessage));
      executor.start();
}

private void loopHandler(Supplier<Boolean> ruler, Runnable lastTask) {
      while (ruler.get() && Gdx.app.getApplicationListener() != null) {
	    Thread.yield();//do nothing besides waiting
      }
      lastTask.run();
}

public void addCallback(Event event, Consumer<Object> callback) {
      var list = callbackMap.getOrDefault(event, new ArrayList<>());
      list.add(callback);
      callbackMap.put(event, list);
}

public void removeCallback(@NotNull Event event, @NotNull Consumer<Object> removable) {
      var list = callbackMap.get(event);
      if (list == null)
	    return;
      list.remove(removable);
}

private void setScreen(ScreenType type) {
      ui.setScreen(type);
      callbackMap.getOrDefault(Event.OnScreenChange, List.of())
	  .forEach(action -> action.accept(ui.getScreen()));
}

public void init() {
      stage = Game.Stage.GameProcess;
      setScreen(ScreenType.Game);
}

public Game.Stage getStage() {
      return stage;
}
}
