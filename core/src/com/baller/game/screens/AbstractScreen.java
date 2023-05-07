package com.baller.game.screens;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.baller.game.UserInterface;
import org.jetbrains.annotations.Nullable;

import javax.swing.text.View;
import java.util.List;
import java.util.Map;

import static com.baller.game.UserInterface.*;

public class AbstractScreen implements ClickScreen{
private final Skin skin;
private final Viewport viewport;
private Map<UserClick.Id, UserClick> mapper;
public AbstractScreen(Skin skin, Viewport viewport){
      this.skin = skin;
      this.viewport = viewport;
}
@Override
public List<UserClick> getAll() {
      return null;
}

@Override
public void acceptClicks(Map<UserClick.Id, UserClick> mapper) {
	this.mapper = mapper;
}

@Override
public void show() {

}

@Override
public void render(float delta) {

}

@Override
public void resize(int width, int height) {
      viewport.update(width, height);
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
}
protected void bubblesClick(UserClick.Id id) {
      var click = mapper.get(id);
      if (click != null)
      	click.bubbles();
}
protected @Nullable UserClick clickOf(UserClick.Id id) {
      return mapper.get(id);
}
protected  Skin getSkin(){
      return skin;
}
}
