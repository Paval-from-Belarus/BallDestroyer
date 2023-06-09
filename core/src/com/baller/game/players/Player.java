package com.baller.game.players;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.baller.game.Game;
import com.baller.game.Globals;
import com.baller.game.common.AnimatedObject;
import com.baller.game.field.BonusCollision;
import com.baller.game.field.BrickBlock;
import com.baller.game.field.GameField;
import com.baller.game.common.DisplayObject.*;
import com.baller.game.serializer.AbstractSerializable;

import java.awt.Point;
import java.util.*;
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
		"TrampolineCnt=(.+)", "Balls=\\[(.+)\\]");
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
		       "Balls=" + Arrays.toString(balls) + "\n";
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

      @Override
      public String[] getFieldNames() {
	    return new String[]{"trampolineCnt", "id", "score", "balls"};
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
	    String[] patterns = source.split("},?");
	    if (patterns.length < 2)
		  System.out.println("Only single ball");
	    Ball.Properties[] balls = new Ball.Properties[patterns.length];
	    int index = 0;
	    for (String pattern : patterns) {
		  var props = new Ball.Properties();
		  props.deserialize(pattern + "}"); //last char was removed from pattern
		  if (!props.isEmpty()) {
			balls[index] = props;
			index += 1;
		  }
	    }
	    if (index == balls.length) {
		  this.balls = balls;
	    } else {
		  throw new IllegalStateException("Impossible to process balls");
	    }
      }
}

public static final int DEFAULT_TRAMPOLINE_CNT = 1;
public static final int DEFAULT_BALL_CNT = 1;
public static final int DEFAULT_SCORE = 0;

public enum State {Blocked, Alive, Defeated, Victory}

public static class Id {
      int value;

      Id(int value) {
	    this.value = value;
      }

      @Override
      public boolean equals(Object other) {
	    if (other instanceof Player.Id)
		  return ((Player.Id) other).value == this.value;
	    return false;
      }

      @Override
      public String toString() {
	    return String.valueOf(value);
      }
}

private ArrayList<Ball> balls;
private Integer score;
private Texture textBall;
private final Player.Id id;
private int trampolineCnt;
private Player.State state;

{
      textBall = new Texture("red_ball.png");
      state = State.Blocked;
      trampolineCnt = DEFAULT_TRAMPOLINE_CNT;
      score = DEFAULT_SCORE;
      balls = new ArrayList<>(1);
}

Player(Player.Id id) {
      this.id = id;
      balls.add(new Ball(textBall));
      boost(Game.HardnessLevel.values()[Globals.CURR_MODE_INDEX]);
//      balls.add(new Ball(textBall));
//      balls.get(1).reflect(AnimatedObject.Axis.Vertical);
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


public void draw(SpriteBatch batch) {
      if (state == State.Blocked)
	    return;
      //score.draw();
      for (Ball ball : balls) {
//	    System.out.println(ball.getPos());
	    ball.draw(batch);
      }
}

public void boost(Game.HardnessLevel level) {
      balls.forEach(ball -> ball.boost(level.ratio()));
}

public void update(float dt) {
      if (state == State.Blocked)
	    return;
      for (Ball ball : balls)
	    ball.update(dt);
}

private void dispatchBonusEvent(BonusCollision collision) {
      assert collision != null;
      int scoreSum = collision.getScore();
      switch (collision.getType()) {
	    case DamageBonus -> {}// the bonus damaged is automatically added
	    case Killer -> {
		  BrickBlock[] blocks = (BrickBlock[]) collision.callback(null);
		  if (blocks != null) {
			for (BrickBlock dummy : blocks) {
			      dummy.setType(BrickBlock.Type.Destroyed);
			}
		  }
//		  block.setType(BrickBlock.Type.Plain);
	    }
	    case MultiTrampoline -> {
		  int trampolineCnt = new Random().nextInt(1, 4);
		  if (this.trampolineCnt != trampolineCnt) {
			collision.callback(trampolineCnt);
			this.trampolineCnt = trampolineCnt;
		  }
	    }
	    case MultiBall -> {
		  Ball collided = (Ball) collision.callback(null);
		  assert collided != null;
		  Ball clone = new Ball(textBall);
		  if (collided.isVisible()) {
			clone.setPos(collided.getPos().x , collided.getPos().y);
			clone.setVelocity(collided.getVelocity());
			clone.reflect(AnimatedObject.Axis.Vertical);
		  }
		  balls.add(clone);
	    }
      }
      score += scoreSum;
}
public void dispatchAll(List<GameField.Message> messages, Ball ball) {
      for (var msg : messages) {
	    dispatch(msg, ball);
      }
}
public void dispatch(GameField.Message msg, Ball ball) {
      GameField.Event event = msg.getEvent();
      switch (event) {
	    case BlockCollision -> score += 0;
	    case BonusCollision ->  {
		  BonusCollision collision = (BonusCollision) msg.getValue();
		  assert collision != null;
		  dispatchBonusEvent(collision);
	    }
	    case SideCollision -> {
		  Integer side = (Integer) msg.getValue();
		  if (side == GameField.BottomSide) {
			if (balls.size() > 1) {
//			      ball.setState(DisplayState.Hidden);
			      balls.remove(ball);
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
public void commit() {
      if (isActive()) {
	    boolean isFrozen = balls.stream()
		.allMatch(Ball::isFrozen);
	    if (isFrozen) {
		  this.state = State.Victory;
	    }
      }
}
public Player.State getState(){
      return state;
}

public boolean isActive() {
      return this.state == State.Alive || this.state == State.Victory;
}

public Properties getProperties() {
      return new Properties(this);
}

public Ball[] getBalls() {
      return balls.toArray(new Ball[0]);
}

public void setTrampolineCnt(int trampolineCnt) {
      this.trampolineCnt = trampolineCnt;
}

public int getScore() {
      return score;
}

public void dispose() {
      textBall.dispose();
}
}
