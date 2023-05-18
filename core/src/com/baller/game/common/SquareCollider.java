package com.baller.game.common;

import com.badlogic.gdx.math.Vector2;

import java.awt.*;
import java.util.Optional;

public class SquareCollider implements Collider {
/**
 * This enum is used to check the result of collision of side
 */
public enum SideType {Top, Right, Bottom, Left, None}

private final float width;
private final float height;
private Point center;
private float a1, b1;
private float a2, b2;
private SideType lastSide;
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

public SideType getCollisionSide() {
      SideType side = SideType.None;
      if (lastSide != null)
	    side = lastSide;
      return side;
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
public boolean collides(Collider square) {
      if (!(square instanceof SquareCollider))
	    return false;
      SquareCollider other = (SquareCollider) square;
      boolean isCollides = false;
      Vector2[] points = getPoints();
      Vector2 upper = other.upperBound();
      Vector2 lower = other.lowerBound();
      int i;
      for (i = 0; i < points.length && !isCollides; i++) {
	    isCollides = points[i].x >= upper.x && points[i].x <= lower.x &&
			     points[i].y <= upper.y && points[i].y >= lower.y;
      }
      if (isCollides) {
	    other.setLastCollision(points[i - 1]);
      }
      return isCollides;
}
private void setLastCollision(Vector2 touched){
      lastSide = getSideType(touched);
}

/**
 * this method is only used to set side of collision by external object
 * @param point is supposed to be inside
 * */
private SideType getSideType(Vector2 point) {
      final Point center = center();
      float xOffset = center.x - point.x;
      float yOffset = center.y - point.y;
      float xRatio = xOffset / width();
      float yRatio = yOffset / height();
      SideType[] sides;
      int index;
      if (Math.abs(xRatio) < Math.abs(yRatio)) {
	    sides = new SideType[]{SideType.Left, SideType.Right};
	    index = xRatio > 0 ? 1 : 0;
      } else {
	    sides = new SideType[]{SideType.Bottom, SideType.Top};
	    index = yRatio > 0 ? 1 : 0;
      }
      return sides[index];
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

public Vector2[] getPoints() {
      int diff = 0;
      float[] xOffsets = {0 + diff, width + diff, 0 + diff, -width + diff };
      float[] yOffsets = {0, 0, -height, -height};
      var vectors = new Vector2[5];
      Vector2 upper = upperBound();
      for (int i = 0; i < vectors.length - 1; i++) {
	    vectors[i] = new Vector2(upper.x + xOffsets[i], upper.y + yOffsets[i]);
      }
      vectors[4] = new Vector2(center().x, center().y);
      return vectors;
}

public Point center() {
      return this.center;
}
}
