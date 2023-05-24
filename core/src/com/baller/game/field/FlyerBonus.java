package com.baller.game.field;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.baller.game.Globals;
import com.baller.game.common.AnimatedObject;
import com.baller.game.common.DisplayObject;
import com.baller.game.common.SquareCollider;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class FlyerBonus extends AnimatedObject {
private static final int DEFAULT_WIDTH = 80;
private static final int DEFAULT_HEIGHT = 40;
private BrickBlock.Type type;
private Label scoreBonus;
private int score;
private Function<Object, Object> callback;

private FlyerBonus(Texture texture) {
      super(texture);
      setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
      setCollider(new SquareCollider(DEFAULT_WIDTH, DEFAULT_HEIGHT, this.getPos()));
      velocity = new Vector2(0.0f, MathUtils.random(-150, -50));
}

public FlyerBonus(Texture texture, BrickBlock.Type type, Integer score, Function<Object, Object> callback) {
      this(texture);
      assert Globals.GAME_SKIN != null;
      this.type = type;
      this.callback = callback;
      this.score = score;
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

public Function<Object, Object> getCallback() {
      return callback;
}

public int getScore() {
      return score;
}
}
