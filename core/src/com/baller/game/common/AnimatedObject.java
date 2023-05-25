package com.baller.game.common;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.baller.game.Globals;

public abstract class AnimatedObject extends RigidObject {
private static final float[] arrVelocity;

static {
      arrVelocity = new float[]{12f, 30f, 60f};
}

public enum Axis {Horizontal, Vertical}

public enum VelocityLevel {Low, Medium, High}

protected Vector2 pos;
protected Vector2 velocity;
protected float velocityPlain;

protected AnimatedObject(Texture texture) {
      super(texture);
      velocity = new Vector2(); //no velocity
      pos = new Vector2();
}

public void rotate(float angle) {
      velocity = new Vector2(velocityPlain, 0f);
      velocity.rotateRad(angle);
}

public void reflect(Vector2 direction) {
      if (Math.abs(direction.x) > Math.abs(direction.y)) {
            if (direction.x * velocity.x < 0.0f)
                  reflect(Axis.Vertical);
      } else {
            if (direction.y * velocity.y < 0.0f)
                  reflect(Axis.Horizontal);
      }
//      final float length = velocity.len();
//      Vector2 aux = velocity.cpy().scl(1.0f / length).scl(direction);
//      double cos = (aux.x + aux.y);//unit vector
//      double angle = MathUtils.PI - 2 * Math.acos(cos);
//      cos = MathUtils.cos((float) angle);

//      float auxLength = (float) Math.sqrt(length * length + length * length - cos * length * length);
//      this.velocity = direction.scl(auxLength).add(velocity);

//       Vector2 plain = velocity.cpy().scl(1.0f / length);
//      direction = direction.add(plain).scl(1.0f / direction.len());
//      velocity = direction.scl(length);
//      Vector2 plain = new Vector2(1, 1);
//      velocity = direction.scl(length);
//      if (Math.abs(direction.x) > Math.abs(direction.y)) {
//	    if (velocity.y * direction.y < 0)
//		  velocity.y *= -1.0f;
//      } else {
//	    if (velocity.x * direction.x < 0)
//		  velocity.x *= -1.0f;
//      }
//      direction = direction.add(velocity).scl(1.0f / direction.len());
//      velocity = direction.scl(length);
//      final float length = velocity.len();
//      velocity = direction.scl(length);
//      if (Math.abs(velocity.x) > Math.abs(velocity.y)) {
//            velocity.x *= -1.0f;
//      } else {
//            velocity.y *= -1.0f;
//      }
}

public void setVelocity(Vector2 velocity) {
      this.velocity.set(velocity);
}

public Vector2 getVelocity() {
      return velocity;
}

public void reflect(Axis axis) {
      switch (axis) {
	    case Horizontal -> this.velocity.y *= -1f;
	    case Vertical -> this.velocity.x *= -1f;
      }
}

@Override
public void setPos(int x, int y) {
      pos.x = Globals.convertWidth(x);
      pos.y = Globals.convertHeight(y);
      super.setPos(x, y);
}

public void update(float dt) {
      Vector2 diff = velocity.cpy();
      pos = pos.add(diff.scl(dt));

      spriteBack.setPosition(pos.x, pos.y);
      virtualPos.move(Globals.convertWidth(pos.x + spriteBack.getWidth() / 2f), Globals.convertHeight(pos.y + spriteBack.getHeight() / 2f));
}

}
