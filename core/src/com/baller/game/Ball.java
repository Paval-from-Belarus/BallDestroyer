package com.baller.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import java.awt.*;
import java.util.function.Consumer;


public class Ball extends Movable{
    Consumer<Sprite> animation;
    Ball(Texture texture){
        super(texture);
        setSize(Globals.BALL_SIZE, Globals.BALL_SIZE);
        this.pos = Globals.DEFAULT_BALL_POS;
        this.velocity = Globals.DEFAULT_BALL_VELOCITY;
        this.collider = new CircleCollider(Math.max(width >> 1, height >> 1), virtualPos);
        update(0f);
    }
    public void freeze(){
        this.velocity = new Vector2(0f, 0f);
    }
    public void playAnimation(){
        animation.accept(this.spriteBack);
    }
    public void setAnimation(Consumer<Sprite> action){
        this.animation = action;
    }
}
