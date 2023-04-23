package com.baller.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import javax.swing.*;

public abstract class AnimatedObject extends RigidObject {
private static final float[] arrVelocity;

static {
      arrVelocity = new float[]{12f, 30f, 60f};
}

public enum Axis {Horizontal, Vertical}

public enum VelocityLevel {Low, Medium, High}

protected Vector2 pos;
protected Vector2 velocity;
protected float velocityPlain;

protected AnimatedObject(Texture texture) {
      super(texture);
      velocity = new Vector2(); //no velocity
      pos = new Vector2();
}

public void rotate(float angle) {
      velocity = new Vector2(velocityPlain, 0f);
      velocity.rotateRad(angle);
}

public void setVelocity(VelocityLevel level) {
      float updPlain = 0f;
      switch (level) {
	    case Low -> updPlain = arrVelocity[0];
	    case Medium -> updPlain = arrVelocity[1];
	    case High -> updPlain = arrVelocity[2];
      }
      float diff = this.velocityPlain / updPlain;
      this.velocityPlain = updPlain;
      this.velocity.scl(diff);
}
public Vector2 getVelocity(){
      return velocity;
}
public void reflect(Axis axis) {
      switch (axis) {
	    case Horizontal -> this.velocity.y *= -1f;
	    case Vertical -> this.velocity.x *= -1f;
      }
}
@Override
public void setPos(int x, int y){
      pos.x = Globals.convertWidth(x);
      pos.y = Globals.convertHeight(y);
      super.setPos(x, y);
}
public void update(float dt) {
      Vector2 diff = velocity.cpy();
      pos = pos.add(diff.scl(dt));
      spriteBack.setPosition(pos.x, pos.y);
      virtualPos.move(Globals.convertWidth(pos.x + spriteBack.getWidth() / 2f), Globals.convertHeight(pos.y + spriteBack.getHeight() / 2f));
}

}
