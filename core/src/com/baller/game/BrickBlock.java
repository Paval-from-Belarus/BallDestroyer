package com.baller.game;

import com.badlogic.gdx.graphics.Texture;

import java.util.function.Consumer;

public class BrickBlock extends FieldObject{
    BrickBlock(Texture texture) {
        super(texture);
        setSize(Globals.OBJECT_WIDTH, Globals.OBJECT_HEIGHT);
    }
}
