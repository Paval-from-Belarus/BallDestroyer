package com.baller.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.awt.Point;
import java.util.function.Consumer;

public class FieldObject {
    Consumer<Object> action;
    Object callable;
    Sprite spriteBack;
    int width;
    int height;
    Point virtualPos;
    Collider collider;
    boolean activated;
    {
        activated = true;
        action = o -> {};
    }
    FieldObject(Texture texture){
        spriteBack = new Sprite(texture);
        spriteBack.setOriginCenter();
        virtualPos = new Point(0, 0);
        collider = new SquareCollider(getWidth(), getHeight(), virtualPos);
    }
    public void setCollider(Collider collider){this.collider = collider;}
    public void draw(SpriteBatch batch){
        if(isActive())
            spriteBack.draw(batch);
    }
    public void setTexture(Texture texture){
        if(spriteBack != null){
            spriteBack.setTexture(texture);
        }
        else {
            spriteBack = new Sprite(texture);
        }
        spriteBack.setOriginCenter();
    }
    public void setActive(boolean state){
        this.activated = state;
        this.action.accept(callable);
    }
    public boolean isActive(){
        return activated;
    }
    public void setScale(float scale){
        if(spriteBack != null)
            spriteBack.setScale(scale);
    }
    private void adjust(int width, int height){
        if(spriteBack == null)
            return;
        spriteBack.setSize(
                Globals.convertWidth(width),
                Globals.convertHeight(height)
        );
        spriteBack.setScale(1f);
    }
    public void setSize(int width, int height){
        this.width = width;
        this.height = height;
        this.adjust(width, height);
    }
    public void setPos(int x, int y){
        this.virtualPos.move(x, y);
        float realX = Globals.convertWidth(x - (this.width >> 1) );
        float realY = Globals.convertHeight(y - (this.height >> 1) );
        this.spriteBack.setPosition(realX, realY);

    }
    public Point getPos(){return virtualPos;}
    public int getWidth(){return width;}
    public int getHeight(){return height;}
    public Collider collider(){return this.collider;}

    //callback is invoked when activity status is changed
    public void setCallback(Consumer<Object> action, Object callable){
        this.action = action;
        this.callable = callable;
    }
}
