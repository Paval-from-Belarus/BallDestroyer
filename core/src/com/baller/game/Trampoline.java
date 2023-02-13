package com.baller.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import java.awt.*;

public class Trampoline extends FieldObject{

    Trampoline(Texture texture) {
        super(texture);
        setSize(Globals.TRAMPOLINE_WIDTH, Globals.TRAMPOLINE_HEIGHT);
        collider = new SquareCollider(width, height, virtualPos);
    }
}
