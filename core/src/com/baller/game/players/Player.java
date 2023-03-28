package com.baller.game.players;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.baller.game.AnimatedObject;
import com.baller.game.field.BrickBlock;
import com.baller.game.field.GameField;
import com.baller.game.DisplayObject.*;
import java.util.ArrayList;

public class Player {
public static class Properties {
      private int trampolineCnt;
      private Player.Id id;// each player cannot exists without id
      private int score;
      private Ball.Properties[] balls;//used for serialization
      				      //not set by default
      private Properties(){}
      private Properties(Player owner) {
	    id = owner.id;
	    trampolineCnt = owner.trampolineCnt;
	    score = owner.score;
      }
      private void complete(Player owner){
	    for(int i = owner.balls.size(); i < balls.length; i++)
		  owner.balls.add(new Ball(owner.textBall));
	    int index = 0;
	    for(var ball : owner.balls){
		  ball.restore(this.balls[index]);
	    }
	    owner.score = score;
	    owner.trampolineCnt = trampolineCnt;
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
      public boolean equals(Object other){
	    if(other instanceof Player.Id)
		  return ((Player.Id)other).value == this.value;
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
}
Player(Player.Properties properties){
      this(properties.id);
      properties.complete(this);
}
Player(Player.Id id) {
      this.id = id;
      balls = new ArrayList<>(2);
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
      for (Ball ball : balls){
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
public boolean isActive(){
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
