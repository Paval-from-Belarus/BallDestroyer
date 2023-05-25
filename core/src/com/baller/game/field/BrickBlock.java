package com.baller.game.field;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.baller.game.common.AnimatedObject;
import com.baller.game.common.SquareCollider;
import com.baller.game.serializer.Serializer;

import java.util.Arrays;
import java.util.Random;
import java.util.function.BiConsumer;

public class BrickBlock extends AnimatedObject {
/**
 * DamageBonus is used to destroy all
 * */
public enum Type {
      Plain, Invincible, DamageBonus, Killer, MultiTrampoline, MultiBall, Destroyed;
      private int weight;
      private static int SUM_WEIGHT;
      public static float ratio;
      static {
            Plain.weight = 7;
            Invincible.weight = 5;
            DamageBonus.weight = 3;
            Killer.weight = 4;
            MultiBall.weight = 5;//2;
            MultiTrampoline.weight = 2;
            SUM_WEIGHT = Arrays.stream(values()).map(v -> v.weight).reduce(0, Integer::sum);
      }
      public static void setRatio(float ratio) {
            Type.ratio = ratio;
            SUM_WEIGHT = Arrays.stream(values()).map(Type::weight).reduce(0, Integer::sum);
      }
      public int weight() {
            int result = weight;
            if (this != Plain && this != Destroyed) {
                  result = (int) (result * ratio);
            }
            return result;
      }
      public static int totalWeight() {
            return SUM_WEIGHT;
      }
}
public static int DEFAULT_WIDTH = 85;
public static int DEFAULT_HEIGHT = 30;
private Type type;
private BiConsumer<BrickBlock, Type> task;
private BrickBlock(Texture texture) {
      super(texture);
      setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
      setCollider(new SquareCollider(this.getWidth() + 4, this.getHeight() + 4, this.getPos()));
      task = (block, type) ->{};
}

BrickBlock(Texture texture, Type type) {
      this(texture);
      setType(type);
}
public boolean isDestroyable() {
      return type != Type.Invincible && type != Type.Destroyed;
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
