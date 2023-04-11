package com.baller.game;

import com.badlogic.gdx.math.Vector2;

import java.awt.*;

public class SquareCollider implements Collider {
int width;
int height;
Point center;
//diags
float a1, b1;
float a2, b2;
private Point upper;
private Point lower;

public SquareCollider(int width, int height) {
      this.width = width;
      this.height = height;
      center = new Point(0, 0);
      updateSides();
}

public SquareCollider(int width, int height, Point center) {
      this(width, height);
      move(center);
}

@Override
public boolean collides(Collider other) {
      Side closest = other.getSide(center);
      return collides(closest.getStart(), closest.getEnd());
}

//@Override
//public boolean collides(Collider other) {
//      boolean isCollides = false;
//      Point[] points = other.getPoints();
//      for (int i = 0; i < points.length && !isCollides; i++) {
//	    isCollides = upper.x > points[i].x && lower.x < points[i].x &&
//			     upper.y > points[i].y && lower.y < points[i].y;
//
//      }
//      return isCollides;
//}

@Override
public boolean collides(Point start, Point end) {
      updateSides();
      float a = 0f, b = 0f, c = 0f;
      boolean result;
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
      Vector2 crossPoint = getCrossPoint(a, b, c, this.a1, -1, this.b2);
      result = inside(crossPoint);
      if (!result) {
	    crossPoint = getCrossPoint(a, b, c, this.a2, -1, this.b2);
	    result = inside(crossPoint);
      }
      return result;
}

private boolean inside(Vector2 dote) {
      float diffX = center.x - dote.x;
      float diffY = center.y - dote.y;
      return Math.abs(diffX) <= ((float) width / 2) && Math.abs(diffY) <= ((float) height / 2);
}

private Vector2 getCrossPoint(float a1, float b1, float c1,
			      float a2, float b2, float c2) {
      Vector2 result = new Vector2();
      if (a1 == 0) {
	    result.y = -c1 / b1;
	    result.x = 0f;
      } else {
	    result.y = (a2 / a1 * c1 - c2) / (b2 - a2 / a1 * b1);
	    result.x = -(b1 * result.y + c1) / a1;
      }
      return result;

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

public Point[] getPoints() {
      return null;
}

public Point center() {
      return this.center;
}
}
