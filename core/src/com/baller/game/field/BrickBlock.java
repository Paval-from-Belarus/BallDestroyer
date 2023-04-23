package com.baller.game.field;

import com.badlogic.gdx.graphics.Texture;
import com.baller.game.AnimatedObject;
import com.baller.game.SquareCollider;

import java.util.function.Consumer;

public class BrickBlock extends AnimatedObject {
/**
 * DamageBonus is used to destroy all
 * */
public enum Type {Plain, HealBonus, DamageBonus, Killer, MultiTrampoline, Destroyed}
public static int DEFAULT_WIDTH = 100;
public static int DEFAULT_HEIGHT = 50;
private Type type;

private BrickBlock(Texture texture) {
      super(texture);
      setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
      setCollider(new SquareCollider(this.getWidth(), this.getHeight(), this.getPos()));
}

BrickBlock(Texture texture, Type type) {
      this(texture);
      setType(type);
}
public Type getType() {
      return type;
}
public void setType(Type type){
      this.type = type;
      if(type == Type.Destroyed)
            super.setState(DisplayState.Hidden);
}
}
