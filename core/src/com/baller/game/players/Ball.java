package com.baller.game.players;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.baller.game.common.AnimatedObject;
import com.baller.game.Globals;
import com.baller.game.common.SquareCollider;
import com.baller.game.serializer.AbstractSerializable;

import java.awt.Point;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Ball extends AnimatedObject {
public static class Properties extends AbstractSerializable<Ball> {
      private Point pos;
      private Vector2 velocity;
      private float radius;

      public Properties() {
	    List<String> patterns = List.of(
		"Pos=\\(([^)]+)\\),", "Velocity=\\(([^)]+)\\),", "Radius=([0-9\\.]+)"
	    );
	    List<Consumer<String>> handlers = List.of(
		this::setPos, this::setVelocity, this::setRadius
	    );
	    addHandlers(handlers);
	    addPatterns(patterns);
      }

      private Properties(Ball owner) {
	    this.pos = owner.virtualPos;
	    this.velocity = owner.velocity;
	    this.radius = owner.getHeight() / 2f;
      }

      @Override
      public String toString() {
	    return "{Pos=" +
		       "(x=" + this.pos.x + "," +
		       "y=" + this.pos.y + ")," +
		       "Velocity=" + this.velocity.toString() + "," +
		       "Radius=" + String.format("%.3f", this.radius) + "}";
      }

      @Override
      public String serialize() {
	    return this.toString();
      }

      public Ball construct(Texture texture) {
	    Ball ball = new Ball(texture);
	    ball.velocity = velocity;
	    ball.setPos(pos.x, pos.y);
	    int diameter = (int) (radius * 2);
	    ball.setSize(diameter, diameter);
	    return ball;
      }

      public Ball construct() {
	    throw new UnsupportedOperationException("Use method with texture");
      }

      @Override
      public String[] getFieldNames() {
	    return new String[]{"pos", "velocity", "radius"};
      }

      private void setPos(String source) {
	    Matcher matcher = Pattern.compile("x=(.+), *y=(.+) *").matcher(source);
	    if (!matcher.find())
		  throw new IllegalStateException("Incorrect source string");
	    int x = Integer.parseInt(matcher.group(1));
	    int y = Integer.parseInt(matcher.group(2));
	    this.pos = new Point(x, y);
      }

      private void setVelocity(String source) {
	    Matcher matcher = Pattern.compile("(.+), *(.+) *").matcher(source);
	    if (!matcher.find())
		  throw new IllegalStateException("Incorrect source string");
	    float x = Float.parseFloat(matcher.group(1));
	    float y = Float.parseFloat(matcher.group(2));
	    this.velocity = new Vector2(x, y);
      }

      private void setRadius(String source) {
	    this.radius = Float.parseFloat(source);
      }
}

private Consumer<Sprite> animation;

Ball(Texture texture) {
      super(texture);
      setSize(Globals.BALL_SIZE, Globals.BALL_SIZE);
      this.pos = Globals.DEFAULT_BALL_POS.cpy();
      this.velocity = Globals.DEFAULT_BALL_VELOCITY.cpy();
      this.setCollider(new SquareCollider(width, height, virtualPos));
//      this.setCollider(new CircleCollider(Math.max(width >> 1, height >> 1), virtualPos));
      update(0f);
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
