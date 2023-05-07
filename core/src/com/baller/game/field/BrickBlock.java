package com.baller.game.field;

import com.badlogic.gdx.graphics.Texture;
import com.baller.game.common.AnimatedObject;
import com.baller.game.common.SquareCollider;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class BrickBlock extends AnimatedObject {
/**
 * DamageBonus is used to destroy all
 * */
public enum Type {Plain, HealBonus, DamageBonus, Killer, MultiTrampoline, Destroyed}
public static int DEFAULT_WIDTH = 100;
public static int DEFAULT_HEIGHT = 50;
private Type type;
private BiConsumer<BrickBlock, Type> task;
private BrickBlock(Texture texture) {
      super(texture);
      setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
      setCollider(new SquareCollider(this.getWidth(), this.getHeight(), this.getPos()));
      task = (block, type) ->{};
}

BrickBlock(Texture texture, Type type) {
      this(texture);
      setType(type);
}
void onStateChanged(BiConsumer<BrickBlock, Type> task){
      this.task = task;
}
public Type getType() {
      return type;
}
public void setType(Type type){
      task.accept(this, type);
      this.type = type;
      if(type == Type.Destroyed)
            super.setState(DisplayState.Hidden);
      else
            super.setState(DisplayState.Visible);
}
}
