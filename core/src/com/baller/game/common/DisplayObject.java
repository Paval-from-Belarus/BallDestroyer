package com.baller.game.common;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.baller.game.Globals;

import java.awt.*;

public abstract class DisplayObject {
public enum DisplayState {Visible, Hidden, Disabled, Static}
protected Sprite spriteBack;
protected int width;
protected int height;
protected Point virtualPos;
protected DisplayState state;
protected  DisplayObject(){
      state = DisplayState.Visible;
      virtualPos = new Point(0, 0);
}
protected DisplayObject(Texture texture) {
      this();
      spriteBack = new Sprite(texture);
      spriteBack.setOriginCenter();
}

public void draw(SpriteBatch batch) {
      if (isVisible())
	    spriteBack.draw(batch);
}

public void setTexture(Texture texture) {
      if (spriteBack != null) {
	    spriteBack.setTexture(texture);
      } else {
	    spriteBack = new Sprite(texture);
      }
      spriteBack.setOriginCenter();
}
public void setState(DisplayState state) {
      this.state = state;
}

public boolean isActive(){
      return state != DisplayState.Disabled;
}
public boolean isVisible() {
      return state == DisplayState.Visible;
}
public boolean isStatic() {
      return state == DisplayState.Static;
}
public void setScale(float scale) {
      if (spriteBack != null)
	    spriteBack.setScale(scale);
}
public void setAlpha(float alpha){
      if(spriteBack != null)
            spriteBack.setAlpha(alpha);
}

private void adjust(int width, int height) {
      if (spriteBack == null)
	    return;
      spriteBack.setSize(
	  Globals.convertWidth(width),
	  Globals.convertHeight(height)
      );
      spriteBack.setScale(1f);
}

public void setSize(int width, int height) {
      this.width = width;
      this.height = height;
      this.adjust(width, height);
}
public void setPos(int x, int y) {
      this.virtualPos.move(x, y);
      float realX = Globals.convertWidth(x - (this.width >> 1));
      float realY = Globals.convertHeight(y - (this.height >> 1));
      this.spriteBack.setPosition(realX, realY);
}
public final Point getPos(){
      return virtualPos;
}


private Collider collider;
public void setCollider(Collider collider){
      this.collider = collider;
}
public Collider collider(){
      return this.collider;
}
public boolean collides(DisplayObject other) {
      return this.collider().collides(other.collider());
}
public int getWidth() {
      return width;
}
public int getHeight() {
      return height;
}
}
