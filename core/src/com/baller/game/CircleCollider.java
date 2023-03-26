package com.baller.game;

import com.badlogic.gdx.math.Vector2;

import java.awt.*;
import java.util.NavigableMap;
import java.util.SortedSet;
import java.util.TreeSet;

public class CircleCollider implements Collider {

Point center;
float radius;
float doubleRad;
Side[] sides;

public CircleCollider(float radius) {
      this.radius = radius;
      this.doubleRad = radius * radius;
      this.center = new Point(0, 0);
}

public CircleCollider(float radius, Point center) {
      this(radius);
      move(center);
}

@Override
public void move(Point point) {
      this.center = point;
}

@Override
public boolean collides(Collider other) {
      Side closest = other.getSide(this.center);
      return this.collides(closest.getStart(), closest.getEnd());
}

@Override
public boolean collides(Point point) {
      float dest = Vector2.dst2(center.x, center.y, point.x, point.y);
      return dest <= doubleRad;
}

@Override
public boolean collides(Point start, Point end) {
      float a, b, c;
      if (start.x == end.x || start.y == end.y) {
	    if (start.x == end.x) {
		  a = 1;
		  b = 0;
		  c = -start.x;
	    } else {
		  a = 0;
		  b = 1;
		  c = -start.y;
	    }
      } else {
	    float diff = (float) (end.y - start.y) / (end.x - start.x);
	    a = diff;
	    b = -1;
	    c = start.y * (1 - diff);
      }
      double dest = Math.abs(a * center.x + b * center.y + c) / Math.sqrt(a * a + b * b);
      return dest <= radius;
}

/***
 *return Side closest to provided point
 * */
@Override
public Side getSide(Point center) {
      int diffX = this.center.x - center.x;
      int diffY = this.center.y - center.y;
      Side[] buffer = new Side[4];
      Side[] restBuffer = new Side[2];
      System.arraycopy(this.sides, 0, buffer, 0, 4);
      if (diffX > 0)
	    buffer[0] = null;
      else
	    buffer[2] = null;
      if (diffY > 0)
	    buffer[1] = null;
      else
	    buffer[3] = null;
      int offset = 0;
      for (Side side : buffer)
	    if (side != null)
		  restBuffer[offset++] = side;
      return null;
}

public Point center() {
      return this.center;
}

private void initSides() {
      this.sides = new Side[4];
      sides[0] = Side.valueOf((int) (center.x - radius), (int) (center.y + radius),
	  (int) (center.x - radius), (int) (center.y - radius));

      sides[1] = Side.valueOf((int) (center.x - radius), (int) (center.y - radius),
	  (int) (center.x + radius), (int) (center.y - radius));

      sides[2] = Side.valueOf((int) (center.x + radius), (int) (center.y - radius),
	  (int) (center.x + radius), (int) (center.y + radius));

      sides[3] = Side.valueOf((int) (center.x + radius), (int) (center.y + radius),
	  (int) (center.x - radius), (int) (center.y + radius));
}
}
