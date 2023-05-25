package com.baller.game.common;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.awt.*;
import java.util.Optional;

public class SquareCollider implements Collider {


private final float width;
private final float height;
private Point center;
private float a1, b1;
private float a2, b2;

private SquareCollider(float width, float height) {
      this.width = width;
      this.height = height;
      center = new Point(0, 0);
      updateSides();
}

public SquareCollider(float width, float height, Point center) {
      this(width, height);
      move(center);
}

private Vector2 upperBound() {
      Point center = center();
      return new Vector2(center.x - width() / 2.0f, center.y + height() / 2.0f);
}

private Vector2 lowerBound() {
      Point center = center();
      return new Vector2(center.x + width() / 2.0f, center.y - height() / 2.0f);
}

@Override
public boolean collides(Collider collider) {
      boolean isCollides = false;
      if (collider instanceof SquareCollider other) {
	    Vector2 upperBound = new Vector2();
	    Vector2 lowerBound = new Vector2();
	    Vector2 bound1 = other.upperBound();
	    Vector2 bound2 = upperBound();
	    upperBound.y = Math.max(bound1.y, bound2.y);
	    upperBound.x = Math.min(bound1.x, bound2.x);
	    bound1 = other.lowerBound();
	    bound2 = lowerBound();
	    lowerBound.y = Math.min(bound1.y, bound2.y);
	    lowerBound.x = Math.max(bound1.x, bound2.x);
	    isCollides = upperBound.y - lowerBound.y <= other.height() + this.height() &&
			     lowerBound.x - upperBound.x <= other.width() + this.width();
      }
      return isCollides;
}


/**
 * Return the angle by which collider attack the square
 */
public Vector2 getAttackVector(Collider other) {
      Vector2 center = new Vector2(center().x, center().y);
      Vector2 point = new Vector2(other.center().x, other.center().y);
      Vector2 direction = point.sub(center);
      Vector2 result = new Vector2();
      if (Math.abs(direction.x / width() * height()) > Math.abs(direction.y)) {
	    if (direction.x > 0) {
		  result.x = 1.0f;
	    } else {
		  result.x = -1.0f;
	    }
      } else {
	    if (direction.y > 0) {
		  result.y = 1.0f;
	    } else {
		  result.y = -1.0f;
	    }
      }
      return result;
}

private float height() {
      return height;
}

private float width() {
      return width;
}

/**
 * Check collision with any mean defined by two points
 */
@Override
public boolean collides(Point start, Point end) {
      updateSides();
      float a = 0f, b = 0f, c = 0f;
      boolean wasAdded = false;
      if (start.x == end.x) {
	    a = 1;
	    b = 0;
	    c = -start.x;
	    wasAdded = true;
      }
      if (start.y == end.y) {
	    a = 0;
	    b = 1;
	    c = -start.y;
	    wasAdded = true;
      }
      if (!wasAdded) {
	    float diff = (float) (end.y - start.y) / (end.x - start.x);
	    a = diff;
	    b = -1;
	    c = start.y * (1 - diff);
      }
      var crossPoint = getCrossPoint(a, b, c, this.a1, -1, this.b2);
      boolean result = false;
      if (crossPoint.isPresent() && !(result = inside(crossPoint.get()))) {
	    crossPoint = getCrossPoint(a, b, c, this.a2, -1, this.b2);
	    result = crossPoint.isPresent() && inside(crossPoint.get());
      }
      return result;
}

private boolean inside(Vector2 dote) {
      float diffX = center.x - dote.x;
      float diffY = center.y - dote.y;
      return Math.abs(diffX) <= ((float) width / 2) && Math.abs(diffY) <= ((float) height / 2);
}

/**
 * return crossPoint if only one exists
 */
private Optional<Vector2> getCrossPoint(float a1, float b1, float c1,
					float a2, float b2, float c2) {
      Vector2 result = new Vector2();
      if (a2 != 0 & a1 != 0) {
	    result.y = (a2 / a1 * c1 - c2) / (b2 - a2 / a1 * b1);
	    result.x = -(b1 * result.y + c1) / a1;
      } else if (a2 != 0) {
	    result.y = -c1 / b1;
	    result.x = -(c2 + b2 * result.y) / a2;
      } else {
	    result.x = 0;
	    result.y = 0;
      }
      return Optional.of(result);
}

private void updateSides() {
      float[] buffer = getLine(center.x - (float) width / 2, center.y + (float) height / 2,
	  center.x + (float) width / 2, center.y - (float) height / 2);
      this.a1 = buffer[0];
      this.b1 = buffer[1];
      buffer = getLine(center.x - (float) width / 2, center.y - (float) height / 2,
	  center.x + (float) width / 2, center.y + (float) height / 2);
      this.a2 = buffer[0];
      this.b2 = buffer[1];
}

private float[] getLine(float x1, float y1, float x2, float y2) {
      float[] result = new float[2];
      result[0] = (y2 - y1) / (x2 - x1);
      result[1] = y1 - result[0] * x1;
      return result;
}

@Override
public boolean collides(Point point) {
      return false;
}

@Override
public Side getSide(Point center) {
      return null;
}

@Override
public void move(Point center) {
      this.center = center;
      updateSides();
}

public Point center() {
      return this.center;
}
}
