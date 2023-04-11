package com.baller.game.players;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.baller.game.AnimatedObject;
import com.baller.game.field.BrickBlock;
import com.baller.game.field.GameField;
import com.baller.game.DisplayObject.*;
import com.baller.game.serializer.AbstractSerializable;
import com.baller.game.serializer.TextSerializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class Player {
public static class Properties extends AbstractSerializable<Player> {
      private int trampolineCnt;
      private Player.Id id;// each player cannot exists without id
      private int score;
      private Ball.Properties[] balls;//used for serialization

      //not set by default
      public Properties() {
	    List<String> patterns = List.of("Id=(.+)", "Score=(.+)",
		"TrampolineCnt=(.+)", "Balls=\\[.+\\]");
	    List<Consumer<String>> handlers = List.of(
		this::setId, this::setScore, this::setTrampolineCnt, this::setBalls
	    );
	    addHandlers(handlers);
	    addPatterns(patterns);
      }

      private Properties(Player owner) {
	    id = owner.id;
	    trampolineCnt = owner.trampolineCnt;
	    score = owner.score;
      }

      public int getScore() {
	    return score;
      }

      public int getTrampolineCnt() {
	    return trampolineCnt;
      }

      public Id getId() {
	    return id;
      }

      @Override
      public String serialize() {
	    return "Id=" + id.value + "\n" +
		       "Score=" + score + "\n" +
		       "TrampolineCnt=" + trampolineCnt + "\n" +
		       "Balls" + Arrays.toString(balls) + "\n";
      }

      @Override
      public Player construct() {
	    Player owner = new Player(id);
	    owner.balls.clear();//remove default balls from players
	    for (Ball.Properties ball : balls)
		  owner.balls.add(ball.construct(owner.textBall));
	    owner.score = score;
	    owner.trampolineCnt = trampolineCnt;
	    return owner;
      }

      private void setScore(String source) {
	    this.score = Integer.parseInt(source);
      }

      private void setTrampolineCnt(String source) {
	    this.trampolineCnt = Integer.parseInt(source);
      }

      private void setId(String source) {
	    int id = Integer.parseInt(source);
	    this.id = new Player.Id(id);
      }

      private void setBalls(String source) {
	    var items = TextSerializer.arrayFromString(source, raw -> {
		  raw = raw.substring(1, raw.length() - 1); //remove curly bracket
		  var props = new Ball.Properties();
		  props.deserialize(raw);
		  return props;
	    });
	    if (items == null)
		  throw new IllegalStateException("Impossible to process items");
	    this.balls = items.toArray(new Ball.Properties[0]);
      }
}

public static final int DEFAULT_TRAMPOLINE_CNT = 1;
public static final int DEFAULT_BALL_CNT = 1;
public static final int DEFAULT_SCORE = 0;

enum State {Blocked, Alive, Defeated}

public static class Id {
      private int value;

      Id(int value) {
	    this.value = value;
      }

      @Override
      public boolean equals(Object other) {
	    if (other instanceof Player.Id)
		  return ((Player.Id) other).value == this.value;
	    return false;
      }
}

private ArrayList<Ball> balls;
private Integer score;
private Texture textBall;
private final Player.Id id;
private int trampolineCnt;
private Player.State state;

{
      textBall = new Texture("badlogic.jpg");
      state = State.Blocked;
      trampolineCnt = DEFAULT_TRAMPOLINE_CNT;
      score = DEFAULT_SCORE;
      balls = new ArrayList<>(2);
}

Player(Player.Id id) {
      this.id = id;
      balls.add(new Ball(textBall));
      balls.add(new Ball(textBall));
      balls.get(1).reflect(AnimatedObject.Axis.Vertical);
}

public Player.Id getId() {
      return id;
}

public void setState(Player.State state) {
      this.state = state;
      if (state != State.Blocked)
	    return;
      for (Ball ball : balls)
	    ball.setState(DisplayState.Hidden);
}

public State getState() {
      return this.state;
}

public void draw(SpriteBatch batch) {
      if (state == State.Blocked)
	    return;
      //score.draw();
      for (Ball ball : balls) {
	    System.out.println(ball.getPos());
	    ball.draw(batch);
      }
}

public void update(float dt) {
      if (state == State.Blocked)
	    return;

      for (Ball ball : balls)
	    ball.update(dt);
}

public void dispatch(GameField.Message msg, Ball ball) {
      GameField.Event event = msg.getEvent();
      Object handle = msg.getValue();
      switch (event) {
	    case BlockCollision -> {
		  BrickBlock block = (BrickBlock) handle;
		  if (block != null && block.isVisible()) {
			score += 1;
		  }
	    }
	    case SideCollision -> {
		  Integer side = (Integer) msg.getValue();
		  if (side == GameField.BottomSide) {
			if (balls.size() > 1) {
			      balls.remove(balls.size() - 1);
			} else {
			      this.state = State.Defeated;
			      ball.freeze();
			}
		  }
	    }
	    case EmptyField -> ball.freeze();
      }
      GameField.DefaultDispatcher(msg, ball);
}

public boolean isActive() {
      return this.state == State.Alive;
}

public Properties getProperties() {
      return new Properties(this);
}

public Ball[] getBalls() {
      return balls.toArray(new Ball[0]);
}

public void dispose() {
      textBall.dispose();
}
}
