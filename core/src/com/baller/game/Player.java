package com.baller.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.function.BiConsumer;

public class Player {
    enum State {Blocked, Alive, Defeated}
    public static class Id {
        int value;
        Id(int value){
            this.value = value;
        }
    }
    private ArrayList<Ball> balls;
    private Integer score;
    private Texture textBall;
    private Player.Id id;
    private Player.State state;
    {
        textBall = new Texture("badlogic.jpg");
        state = State.Alive;
    }
    Player(Player.Id id){
        this.id = id;
        score = 0;
        balls = new ArrayList<>(1);
        balls.add(new Ball(textBall));
    }
    public Player.Id getId(){
        return id;
    }
    public void setState(Player.State state){
        this.state = state;
        if(state != State.Blocked)
            return;
        for(Ball ball : balls)
            ball.setActive(false);
    }
    public void draw(SpriteBatch batch){
        if(state == State.Blocked)
            return;
        //score.draw();
        for(Ball ball : balls)
            ball.draw(batch);
        System.out.println(score);
    }
    public void update(float dt){
        if(state == State.Blocked)
            return;

        for(Ball ball : balls)
            ball.update(dt);
    }
    public void setScore(int value){
        this.score = 0;
    }
    public int getScore(){return this.score;}

    public void dispatch(GameField.Message msg, Ball ball){
        switch (msg.event) {
            case BlockCollision -> {
                BrickBlock block = (BrickBlock) msg.handle;
                if(block != null && block.isActive()){
                    score += 1;
                }
            }
            case SideCollision -> {
                Integer side = (Integer) msg.getValue();
                if(side == GameField.BottomSide){
                    if(balls.size() > 1){
                        balls.remove(balls.size() - 1);
                    }
                    else {
                        this.state = State.Defeated;
                        ball.freeze();
                    }
                }
            }
            case EmptyField -> ball.freeze();
        }
        GameField.DefaultDispatcher(msg, ball);
    }
    public Ball[] getBalls(){
        return balls.toArray(new Ball[0]);
    }
    public void dispose(){
        textBall.dispose();
    }
}
