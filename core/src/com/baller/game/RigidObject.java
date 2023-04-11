package com.baller.game;

import com.badlogic.gdx.graphics.Texture;

public abstract class RigidObject extends DisplayObject {
//    private Collider collider;
    RigidObject(Texture texture){
        super(texture);
    }
//    public void setCollider(Collider collider){this.collider = collider;}
//    public Collider collider(){return this.collider;}
}
