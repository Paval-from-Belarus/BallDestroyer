package com.baller.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.baller.game.uicomponents.ScoreBar;
import com.baller.game.UserInterface.*;

public class GameScreen implements Screen {
private Stage stage;
private ScoreBar bar;
private SpriteBatch batch;
private Queue<UserClick> messages;
public Queue<UserClick> getMessage(){
      var result = this.messages;
      this.messages = new Queue<>();
      return result;
}

public GameScreen(Skin skin, Viewport port) {
      messages = new Queue<>();
      this.batch = new SpriteBatch();
      stage = new Stage(port, batch);
      bar = new ScoreBar(skin);
      for (Actor actor : bar.getActors())
	    stage.addActor(actor);
}

private void onPauseButtonClicked(ChangeListener.ChangeEvent event, Actor actor){
      messages.addLast(
          new UserClick(UserClick.Type.Release, UserClick.Id.BtnPause)
      );
      System.out.println("Added");
}

@Override
public void show() {
      Gdx.input.setInputProcessor(stage);
}

@Override
public void render(float delta) {
      batch.begin();
      bar.draw(batch);
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
      stage.dispose();
      batch.dispose();
}
}
