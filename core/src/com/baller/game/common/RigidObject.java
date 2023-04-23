package com.baller.game.common;

import com.badlogic.gdx.graphics.Texture;
import com.baller.game.common.DisplayObject;

public abstract class RigidObject extends DisplayObject {
//    private Collider collider;
    RigidObject(Texture texture){
        super(texture);
    }
//    public void setCollider(Collider collider){this.collider = collider;}
//    public Collider collider(){return this.collider;}
}
