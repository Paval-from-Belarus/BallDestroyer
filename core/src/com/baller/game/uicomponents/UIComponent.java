package com.baller.game.uicomponents;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.baller.game.common.DisplayObject;

public abstract class UIComponent extends DisplayObject {
private Array<Actor> actors;
{
      actors = new Array<>();
}
protected UIComponent() {super();}
protected UIComponent(Texture texture){super(texture);}
protected void addActor(Actor actor){
      actors.add(actor);
}
public Array<Actor> getActors() {
      return actors;
}
public void hide(){
      super.setState(DisplayState.Hidden);
      for(Actor actor : getActors()){
            actor.remove();
      }
}
public void show(Stage stage){
      super.setState(DisplayState.Visible);
      for(Actor actor : getActors())
            stage.addActor(actor);
}
}
