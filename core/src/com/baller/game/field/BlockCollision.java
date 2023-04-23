package com.baller.game.field;

import com.baller.game.Collider;
import com.baller.game.SquareCollider;

public class BlockCollision {
private final BrickBlock ref;
private final SquareCollider.SideType side;

BlockCollision(BrickBlock block, SquareCollider.SideType side) {
      this.side = side;
      this.ref = block;
}

public BrickBlock getRef() {
      return ref;
}

public SquareCollider.SideType getSide() {
      return side;
}
}
