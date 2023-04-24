package com.baller.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.baller.game.UserInterface.*;

import static com.baller.game.Game.Stage.GamePause;
import static com.baller.game.Game.Stage.GameProcess;

public class GameController {
/**<code>OnScreenChange</code> event corrupted when user change current menu. In this case, handle object
 * is <code>UserInterface.ScreenType</code>
 * */
public enum Event {OnResolutionChange, onStageChange, OnProgressSave, OnProgressRestore, OnSkinChange, OnProgramExit}

private Game.Stage stage;
private UserInterface ui;
private Map<Event, List<Consumer<Object>>> callbackMap;
private Object lastHandle;

GameController() {
      callbackMap = new HashMap<>();
      ui = new UserInterface();
}

public void setViewport(Viewport port) {
      ui.setViewport(port);
}
public void setScore(Integer score){
      ui.setComponent(UserClick.Id.LBL_GAME_SCORE, score);
}
public void dispatchInput() {
      List<UserClick.Id> clicks = ui.getUserClick();
      for (UserClick.Id id : clicks) {
            var event = convertClick(id);
            event.ifPresent(self -> throughCallback(self, null));
      }
}
private Optional<Event> convertClick(UserClick.Id id){
      Optional<Event> result = Optional.empty();
      switch(id){
            case BTN_GAME_PAUSE -> setStage(GamePause);
            case BTN_GAME_RESUME -> setStage(GameProcess);
            case BTN_GAME_SAVE -> result = Optional.of(Event.OnProgressSave);
            case BTN_GAME_RESTORE -> result = Optional.of(Event.OnProgressRestore);
      }
      return result;
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
private void throughCallback(Event event, Object handle){
      var list = callbackMap.get(event);
      if(list == null)
            return;
      for(var callback : list)
            callback.accept(handle);
}
private void setStage(Game.Stage stage){
      this.stage = stage;
      callbackMap.getOrDefault(Event.onStageChange, List.of())
          .forEach(action -> action.accept(ui.getScreen()));
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
      switch(type){
            case Game -> setStage(GameProcess);
            case Settings -> setStage(Game.Stage.Settings);
            case MainMenu -> setStage(Game.Stage.MainMenu);
      }
}
public void init() {
      stage = GameProcess;
      setScreen(ScreenType.Game);
}

public Game.Stage getStage() {
      return stage;
}
}
