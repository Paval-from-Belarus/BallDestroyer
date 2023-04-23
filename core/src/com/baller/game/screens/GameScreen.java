package com.baller.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.baller.game.uicomponents.PopupMenu;
import com.baller.game.uicomponents.ScoreBar;
import com.baller.game.UserInterface.*;

public class GameScreen implements Screen {
private Stage stage;
private ScoreBar bar;
private PopupMenu menu;
private SpriteBatch batch;
private Queue<UserClick> messages;
private UserClick lastClick;
public UserClick getMessage(){
//      var result = this.messages;
//      this.messages = new Queue<>();
//      return result;
      var click = lastClick;
      this.lastClick = null;
      return click;
}

public GameScreen(Skin skin, Viewport port) {
      messages = new Queue<>();
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
}

private boolean onPauseButtonClicked(Event event){
      menu.show(stage);
      lastClick = (new UserClick(UserClick.Id.BTN_GAME_PAUSE));
      return true;
}
private boolean onResumeButtonClicked(Event event){
      lastClick = (new UserClick(UserClick.Id.BTN_GAME_RESUME));
      menu.hide();
      return true;
}
private boolean onSaveButtonClicked(Event event){
      lastClick = (new UserClick(UserClick.Id.BTN_GAME_SAVE));
      return true;
}
private boolean onRestoreButtonClicked(Event event){
      lastClick = new UserClick(UserClick.Id.BTN_GAME_RESTORE);
      return true;
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
