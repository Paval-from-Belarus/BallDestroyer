package com.baller.game;

import java.awt.*;

public interface Collider {
    class Side {
        private final Point start;
        private final Point end;
        Side(Point start, Point end){
            this.start = start;
            this.end = end;
        }
        Point getStart(){return start;}
        Point getEnd(){return end;}
        public static Side valueOf(int xStart, int yStart,  int xEnd, int yEnd){
            return new Side(
                    new Point(xStart, yStart),
                    new Point(xEnd, yEnd)
            );
        }
    }
    boolean collides(Collider other);
    boolean collides(Point start, Point end);
    boolean collides(Point point);
    Side getSide(Point center);
    void move(Point center);
    Point center();

}
