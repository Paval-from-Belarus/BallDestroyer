package com.baller.game.players;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.baller.game.AnimatedObject;
import com.baller.game.CircleCollider;
import com.baller.game.Globals;

import java.awt.*;
import java.util.function.Consumer;


public class Ball extends AnimatedObject {
public static class Properties {
      private Point pos;
      private Vector2 velocity;
      private Properties(){}
      private Properties(Ball owner) {
	    this.pos = owner.virtualPos;
	    this.velocity = owner.velocity;
      }

      private void restore(Ball owner) {
	    owner.velocity = velocity;
	    owner.setPos(pos.x, pos.y);
      }
}

Consumer<Sprite> animation;

Ball(Texture texture) {
      super(texture);
      setSize(Globals.BALL_SIZE, Globals.BALL_SIZE);
      this.pos = Globals.DEFAULT_BALL_POS.cpy();
      this.velocity = Globals.DEFAULT_BALL_VELOCITY.cpy();
      this.setCollider(new CircleCollider(Math.max(width >> 1, height >> 1), virtualPos));
      update(0f);
}

void restore(Ball.Properties properties){
      properties.restore(this);
}
public void freeze() {
      this.velocity = new Vector2(0f, 0f);
}

public void playAnimation() {
      animation.accept(this.spriteBack);
}

public void setAnimation(Consumer<Sprite> action) {
      this.animation = action;
}

public Properties getProperties() {
      return new Properties(this);
}
}
