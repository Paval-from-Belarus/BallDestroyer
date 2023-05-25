package com.baller.game.field;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.baller.game.Globals;
import com.baller.game.common.AnimatedObject;
import com.baller.game.common.SquareCollider;
import com.baller.game.serializer.AbstractSerializable;
import com.baller.game.serializer.Serializable;

import java.awt.*;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlyerBonus extends AnimatedObject {
private static final int DEFAULT_WIDTH = 60;
private static final int DEFAULT_HEIGHT = 30;
private BrickBlock.Type type;
private Label scoreBonus;
private int score;
private Object params;

private FlyerBonus(Texture texture) {
      super(texture);
      setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
      setCollider(new SquareCollider(DEFAULT_WIDTH, DEFAULT_HEIGHT, this.getPos()));
      velocity = new Vector2(0.0f, MathUtils.random(-150, -50));
}

public FlyerBonus(Texture texture, BrickBlock.Type type, Integer score, Object params) {
      this(texture);
      assert Globals.GAME_SKIN != null;
      this.type = type;
      this.score = score;
      this.params = params;
      this.scoreBonus = new Label(String.valueOf(score), Globals.GAME_SKIN);
      this.scoreBonus.setScale(2.0f);
}

@Override
public void setPos(int x, int y) {
      scoreBonus.setPosition(
	  Globals.convertWidth(x),
	  Globals.convertHeight(y)
      );
      super.setPos(x, y);
}

@Override
public void update(float dt) {
      super.update(dt);
      scoreBonus.setPosition(
	  spriteBack.getX(),
	  spriteBack.getY()
      );
}

@Override
public void draw(SpriteBatch batch) {
      super.draw(batch);
      scoreBonus.draw(batch, 1.0f);
}

public BrickBlock.Type getType() {
      return type;
}

public Object getParams() {
      return params;
}

public int getScore() {
      return score;
}
public Properties getProperties() {
      return new Properties(type, score, (Integer) params).setVelocity(velocity).setPos(virtualPos);
}
public static class Properties extends AbstractSerializable<FlyerBonus> {
      public BrickBlock.Type type;
      public Integer score;
      public Integer params;
      public Vector2 velocity;
      public Point pos;
      private Properties(BrickBlock.Type type, Integer score, Integer params) {
            this.type = type;
            this.score = score;
            this.params = params;
      }
      private Properties setVelocity(Vector2 velocity) {
            this.velocity = velocity;
            return this;
      }
      private Properties setPos(Point pos) {
            this.pos = pos;
            return this;
      }
      public Properties() {
            List<String> patterns = List.of(
                "Type=(.+)", "Score=(.+)","Params=(.+)","Pos=\\(([^)]+)\\),", "Velocity=\\(([^)]+)\\),"
            );
            List<Consumer<String>> handlers = List.of(
                  this::setPos, this::setScore, this::setParams, this::setPos, this::setVelocity
            );
            addHandlers(handlers);
            addPatterns(patterns);
      }
      private void setType(String source) {
            type = BrickBlock.Type.valueOf(source);
      }
      private void setScore(String source) {
            score = Integer.parseInt(source);
      }
      private void setParams(String source) {
            params = Integer.parseInt(source);
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
      public String serialize() {
            return "{Type=" + type + "," +
                       "Score=" + score + "," +
                       "Params=" + params + "," +
                       "Pos=(" + "x=" + pos.x + "," +"y=" + pos.y + ")" +
                       "Velocity=" + velocity.toString() +
                       "}";
      }

      @Override
      public FlyerBonus construct() {
            return null;
      }
      public FlyerBonus construct(Texture texture) {
            FlyerBonus bonus = new FlyerBonus(texture, type, score, params);
            bonus.setVelocity(velocity);
            bonus.setPos(pos.x, pos.y);
            return bonus;
      }

      @Override
      public String[] getFieldNames() {
            return new String[0];
      }
}
}
