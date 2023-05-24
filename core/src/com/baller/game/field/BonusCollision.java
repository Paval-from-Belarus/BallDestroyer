package com.baller.game.field;

import com.badlogic.gdx.utils.Null;
import com.baller.game.common.SquareCollider;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class BonusCollision {
private final BrickBlock.Type type;
private final Function<Object, Object> callback;
private final Integer score;
BonusCollision(BrickBlock.Type type, Integer score, Function<Object, Object> callback) {
      this.callback = callback;
      this.type = type;
      this.score = score;
}
public BrickBlock.Type getType() {
      return type;
}
public @Nullable Object callback(Object value){
      return callback.apply(value);
}

public Integer getScore() {
      return score;
}
}
