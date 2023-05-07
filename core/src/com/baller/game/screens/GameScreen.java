package com.baller.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.baller.game.Globals;
import com.baller.game.uicomponents.PopupMenu;
import com.baller.game.uicomponents.ScoreBar;
import com.baller.game.UserInterface.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.baller.game.UserInterface.UserClick.*;

public class GameScreen implements ClickScreen {
private Stage stage;
private ScoreBar bar;
private PopupMenu menu;
private SpriteBatch batch;
private Map<UserClick.Id, UserClick> mapper;

@Override
public void acceptClicks(Map<Id, UserClick> mapper) {
      this.mapper = mapper;
      var click = mapper.get(Id.LBL_GAME_SCORE);
      assert click != null;
      bar.onScoreChanged(click.rocks());
}

public GameScreen(Skin skin, Viewport port) {
      this.batch = new SpriteBatch();
      menu = new PopupMenu(skin);
      stage = new Stage(port, batch);
      bar = new ScoreBar(skin);
      bar.addPauseListener(this::onPauseButtonClicked);
      bar.show(stage);
      menu.hide();
      menu.addResumeListener(this::onResumeButtonClicked);
      menu.addSaveListener(this::onSaveButtonClicked);
      menu.addRestoreListener(this::onRestoreButtonClicked);
      menu.addResolutionListener(this::onResolutionChanged);
}

private void bubblesClick(UserClick.Id id){
      var click = mapper.get(id);
      click.bubbles();
}
private boolean onPauseButtonClicked(Event event) {
      menu.show(stage);
      bubblesClick(Id.BTN_GAME_PAUSE);
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
private void onResolutionChanged(Integer index){
      bubblesClick(Id.UI_RESOLUTION);
      Globals.CURR_SCREEN_INDEX = index;
      System.out.println(index);
}

@Override
public List<UserClick> getAll() {
      final UserClick.Id[] keys = {Id.BTN_GAME_SAVE, Id.BTN_GAME_RESTORE,
	  Id.BTN_GAME_RESUME, Id.BTN_GAME_PAUSE, Id.LBL_GAME_SCORE};
      List<UserClick> list = new ArrayList<>(keys.length);
      for(UserClick.Id id : keys){
            var click = mapper.get(id);
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
      batch.end();
      stage.act();
      stage.draw();
}

@Override
public void resize(int width, int height) {
      this.stage.getViewport().update(width, height);
}

@Override
public void pause() {

}

@Override
public void resume() {

}

@Override
public void hide() {
      dispose();
}

@Override
public void dispose() {
      bar.dispose();
      menu.dispose();
      stage.dispose();
      batch.dispose();
}
}
