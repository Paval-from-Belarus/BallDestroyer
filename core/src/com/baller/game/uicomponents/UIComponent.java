package com.baller.game.uicomponents;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.baller.game.DisplayObject;
import org.w3c.dom.Text;

import java.util.List;

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
}
