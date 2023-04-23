package com.baller.game.field;

import com.badlogic.gdx.graphics.Texture;
import com.baller.game.common.AnimatedObject;
import com.baller.game.common.SquareCollider;

public class Trampoline extends AnimatedObject {
public static final int DEFAULT_WIDTH = 120;
public static final int DEFAULT_HEIGHT = 30;
Trampoline(Texture texture) {
      super(texture);
      setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
      setCollider(new SquareCollider(width, height, virtualPos));
}

public float getJumping() {
      return 0f;
}

public void setJumping(float value) {
      return;
}
}
