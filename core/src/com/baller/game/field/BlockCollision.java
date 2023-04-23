package com.baller.game.field;

import com.baller.game.common.SquareCollider;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class BlockCollision {
private final BrickBlock ref;
private final SquareCollider.SideType side;
private Function<Object, Object> callback;
BlockCollision(BrickBlock block, SquareCollider.SideType side) {
      this.side = side;
      this.ref = block;
      callback = (o) -> null;
}
void setCallback(Function<Object, Object> callback){
      this.callback = callback;
}

public BrickBlock getRef() {
      return ref;
}

public SquareCollider.SideType getSide() {
      return side;
}
public @Nullable Object callback(Object value){
      return callback.apply(value);
}
}
