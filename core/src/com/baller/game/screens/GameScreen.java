package com.baller.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.baller.game.Globals;
import com.baller.game.UserInterface.UserClick;
import com.baller.game.common.DisplayObject;
import com.baller.game.uicomponents.MessageBox;
import com.baller.game.uicomponents.PopupMenu;
import com.baller.game.uicomponents.ScoreBar;
import com.baller.game.uicomponents.ScoreTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.baller.game.UserInterface.*;
import static com.baller.game.UserInterface.UserClick.Id;

public class GameScreen extends AbstractScreen {

private static final float EVENT_DELAY = 0.3f;
private Stage stage;
private ScoreBar bar;
private PopupMenu menu;
private SpriteBatch batch;
private MessageBox messageBox;
private ScoreTable scoreTable;
private float lastEventTime = 0;
@Override
public void showMessage(MessageInfo info) {
      messageBox.rebuild(info);
      messageBox.show(stage);
}
@Override
public void hideMessage(){
      messageBox.hide();
}

@Override
public void acceptClicks(Map<Id, UserClick> mapper) {
      super.acceptClicks(mapper);
      var scoreClick = mapper.get(Id.LBL_GAME_SCORE);
      var messageClick = mapper.get(Id.MSG_GAME_PROCESS);
      var tableClick = mapper.get(Id.BTN_STATISTICS);
      var restClick = mapper.get(Id.REST_COUNTER);
      var timeClick = mapper.get(Id.GAME_TIMER);
      assert scoreClick != null && messageClick != null && tableClick != null && restClick != null && timeClick != null;
      bar.onScoreChanged(scoreClick.rocks());
      bar.onRestChanged(restClick.rocks());
      bar.onTimeChanged(timeClick.rocks());
      scoreTable.onStatisticsChanged(tableClick.rocks());
      messageBox.onMessageInfo(messageClick.rocks());
}

public GameScreen(Skin skin, Viewport port) {
      super(skin, port);
      this.batch = new SpriteBatch();
      menu = new PopupMenu(skin);
      stage = new Stage(port, batch);
      bar = new ScoreBar(skin);
      bar.addPauseListener(this::onPauseButtonClicked);
      bar.addStatisticsListener(this::onStatisticsTableClicked);
      bar.show(stage);
      menu.addResumeListener(this::onResumeButtonClicked);
      menu.addSaveListener(this::onSaveButtonClicked);
      menu.addRestoreListener(this::onRestoreButtonClicked);
      menu.addSettingsListener(this::onSettingsClicked);
      menu.addExitListener(this::onProgramExit);
      menu.hide();
      messageBox = new MessageBox(skin);
      messageBox.addRestartListener(this::onRestartClicked);
      messageBox.hide();
      scoreTable = new ScoreTable(skin);
      scoreTable.hide();
}
private boolean onRestartClicked(Event event) {
      if (messageBox.isVisible()){
	    messageBox.hide();
	    bubblesClick(Id.MSG_GAME_PROCESS);
      }
      return true;
}
private boolean onProgramExit(Event event) {
      bubblesClick(Id.BTN_EXIT);
      return true;
}
private void restartSleeping(Event event) {
      scoreTable.hide();
      menu.hide();
      if (messageBox.isForced()) {
	    messageBox.show(stage);
      }
      onResumeButtonClicked(event);
}
private boolean onStatisticsTableClicked(Event event) {
      if (scoreTable.isVisible()) {
	    restartSleeping(event);
      } else {
	    if (!menu.isVisible()) {
		  scoreTable.show(stage);
		  bubblesClick(Id.BTN_GAME_PAUSE);
	    }
	    if (messageBox.isVisible()) {
		  messageBox.setState(DisplayObject.DisplayState.Forced);
		  messageBox.hide();
	    }
      }
      return true;
}
private boolean onPauseButtonClicked(Event event) {
      if (menu.isVisible())
	    restartSleeping(event);
      else {
	    if (!scoreTable.isVisible()) {
		  menu.show(stage);
		  bubblesClick(Id.BTN_GAME_PAUSE);
	    }
      }
      return true;
}

private boolean onResumeButtonClicked(Event event) {
      bubblesClick(Id.BTN_GAME_RESUME);
      menu.hide();
      return true;
}

private boolean onSaveButtonClicked(Event event) {
      bubblesClick(Id.BTN_GAME_SAVE);
      return true;
}

private boolean onRestoreButtonClicked(Event event) {
      bubblesClick(Id.BTN_GAME_RESTORE);
      return true;
}

private boolean onSettingsClicked(Event event) {
      bubblesClick(Id.BTN_SETTINGS_SCREEN);
      return true;
}

@Override
public List<UserClick> getAll() {
      final UserClick.Id[] keys = {Id.BTN_GAME_SAVE, Id.BTN_GAME_RESTORE, Id.BTN_EXIT,
	  Id.BTN_GAME_RESUME, Id.BTN_GAME_PAUSE, Id.LBL_GAME_SCORE, Id.MSG_GAME_PROCESS};
      List<UserClick> list = new ArrayList<>(keys.length);
      for (UserClick.Id id : keys) {
	    var click = clickOf(id);
	    assert click != null;
	    list.add(click);
      }
      return list;
}

@Override
public void show() {
      Gdx.input.setInputProcessor(stage);
}

@Override
public void render(float delta) {
      batch.begin();
      bar.draw(batch);
      menu.draw(batch);
      messageBox.draw(batch);
      scoreTable.draw(batch);
      batch.end();
      stage.act();
      stage.draw();
      if (!Globals.PLAYER_NAME.equalsIgnoreCase(bar.playerName.getText().toString())) {
	    bar.playerName.setText(Globals.PLAYER_NAME);
      }
      if (!Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
	    lastEventTime += delta;
      }
      if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE) && lastEventTime >= EVENT_DELAY) {
	    lastEventTime = 0;
	    var event = new InputEvent();
	    event.setButton(Input.Keys.ESCAPE);
	    if (menu.isVisible() || scoreTable.isVisible()) {
		  restartSleeping(event);
	    } else {
		  if (!menu.isVisible()) {
			onPauseButtonClicked(event);
		  }
	    }
      }
}


@Override
public void dispose() {
      bar.dispose();
      menu.dispose();
      stage.dispose();
      batch.dispose();
}
}
