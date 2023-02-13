package com.baller.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Null;

import java.awt.*;
import java.util.ArrayList;
import java.util.function.Consumer;


import static com.baller.game.Globals.*;

public class GameField {
    enum Event {BlockCollision, SideCollision, TrampolineCollision, Movement, EmptyField}
    public static final int LeftSide = 1;
    public static final int BottomSide = 2;
    public static final int RightSide = 3;
    public static final int TopSide = 4;
    public class Message {
        Object handle;
        Event event;
        Message(Event event){
            this.event = event;
        }
        Object getValue(){return this.handle;}
        private void setEvent(Event event){
            this.event = event;
        }
        private void setValue(Object value){
            this.handle = value;
        }
    }
    public static void DefaultDispatcher(Message msg, Ball player){
        switch(msg.event){
            case BlockCollision -> {
                FieldObject block = (FieldObject) msg.getValue();
                Collider body = block.collider();
                if(block.isActive()){
                    block.setActive(false);
                    player.reflect(Movable.Axis.Horizontal);
                }
            }
            case SideCollision -> {
                Integer side = (Integer) msg.getValue();
                if(side == TopSide || side == BottomSide){
                    player.reflect(Movable.Axis.Horizontal);
                }
                else {
                    player.reflect(Movable.Axis.Vertical);
                }
            }
            case TrampolineCollision -> {
                player.reflect(Movable.Axis.Horizontal);
            }
            case Movement -> {
                System.out.println("Hello ball!");
            }
        }
    }
    ArrayList<BrickBlock> blockList;
    int blockCnt;
    Trampoline trampoline;
    int columnCnt;
    int rowCnt;
    int rowGap;
    int columnGap;
    Object lastHandle;
    Texture textBlock;
    Texture textTrampoline;
    {
        textBlock = new Texture("block.png");
        textTrampoline = new Texture("block.png");
    }

    int ceilWidth;
    int ceilHeight;
    GameField(){
        setDimensions();
        trampoline = new Trampoline(textTrampoline);
        blockCnt = rowCnt * columnCnt;
        blockList = new ArrayList<>(rowCnt * columnCnt);
        Consumer<Object> callback = o-> blockCnt = blockCnt - 1;
        int START_OFFSET_X = (columnGap >> 1) + (OBJECT_WIDTH >> 1);
        int START_OFFSET_Y = FIELD_HEIGHT - ((rowGap >> 1) + (OBJECT_HEIGHT >> 1));
        Point currPos = new Point(START_OFFSET_X , START_OFFSET_Y);
        for(int i = 0; i < rowCnt; i++){
            for(int j = 0; j < columnCnt; j++){
                BrickBlock block = new BrickBlock(textBlock);
                block.setPos(currPos.x, currPos.y);
                block.setCallback(callback, this);
                blockList.add(block);
                currPos.translate(this.ceilWidth, 0);
            }
            currPos.x = START_OFFSET_X;
            currPos.translate(0, -this.ceilHeight);
        }
        trampoline.setPos(FIELD_WIDTH / 2, 30);
    }
    public void update(float dt){
        Collider body = trampoline.collider();
        if(!isOutside(trampoline.collider())){
            if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
                trampoline.setPos(body.center().x - 10, body.center().y);
            }
            if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
                trampoline.setPos(body.center().x + 10, body.center().y);
            }
        } else {
            Point pos = trampoline.getPos();
            Integer side = (Integer) lastHandle;
            if(side == LeftSide)
                trampoline.setPos(body.center().x + 10, body.center().y);
            else
                trampoline.setPos(body.center().x - 10, body.center().y);
        }

    }
    public void draw(SpriteBatch batch){
        trampoline.draw(batch);
        for(FieldObject object : blockList)
            object.draw(batch);
    }
    public void dispose(){
        if(textBlock != null)
            textBlock.dispose();
    }
    public Message getMessage(Ball ball){
        Message msg = new Message(Event.Movement);
        if(blockCnt == 0){
            msg.setEvent(Event.EmptyField);
            return msg;
        }
        if(isOutside(ball)){
            msg.setEvent(Event.SideCollision);
            msg.setValue(lastHandle);
            return msg;
        }
        if(isJumper(ball)){
            msg.setEvent(Event.TrampolineCollision);
            msg.setValue(lastHandle);
        }
        if(isDestroyer(ball)){
            msg.setEvent(Event.BlockCollision);
            msg.setValue(lastHandle);
        }
        return msg;
    }
    public void rebuild(){

    }
    public @Null BrickBlock[] getColumn(BrickBlock block){
        int index = blockList.indexOf(block);
        if(index == -1)
            return null;
        int column = index % this.rowCnt;
        BrickBlock[] result = new BrickBlock[this.rowCnt];
        index = column;
        for(int i = 0; i < result.length; i++){
            result[i] = blockList.get(index);
            index += columnCnt;
        }
        return result;
    }

    public @Null BrickBlock[] getRow(BrickBlock block){
        int index = blockList.indexOf(block);
        if(index == -1)
            return null;
        int row = index / this.rowCnt;
        BrickBlock[] result = new BrickBlock[this.columnCnt];
        index = row * this.columnCnt;
        for(int i = 0; i < result.length; i++){
            result[i] = blockList.get(index);
            index += 1;
        }
        return result;
    }
    public BrickBlock[] toArray(){
        return blockList.toArray(new BrickBlock[0]);
    }

    private boolean isJumper(Ball player){
        Collider ball = player.collider();
        Collider ramp = trampoline.collider();
        return (ball.center().y - player.getWidth() / 2  <= ramp.center().y) &&
                ((ball.center().x <= ramp.center().x + trampoline.getWidth() / 2) && (ball.center().x >= ramp.center().x - trampoline.getWidth() / 2));

    }
    private boolean isOutside(Ball player){
        return isOutside(player.collider());
    }
    private boolean isOutside(Collider body){
        final Point[] vertexes = new Point[]{
                new Point(0, FIELD_HEIGHT),
                new Point(0, 0),
                new Point(FIELD_WIDTH, 0),
                new Point(FIELD_WIDTH, FIELD_HEIGHT)
        };
        int iVertex = 0;
        int side;
        boolean isOutside = false;
        for(side = LeftSide; !isOutside && side <= TopSide; side++){
               isOutside = body.collides(vertexes[iVertex], vertexes[ (iVertex + 1) % vertexes.length]);
               iVertex++;
        }
        lastHandle = Integer.valueOf(side - 1);
        return isOutside;
    }
    private boolean isDestroyer (Ball player){
        Collider body = player.collider();
        if(body.center().y < RED_ZONE)
            return false;
        int column = body.center().x / this.ceilWidth;
        int row = (TABLE_HEIGHT - (body.center().y - RED_ZONE)) / this.ceilHeight;
        int index = row * this.columnCnt + column;
        lastHandle = blockList.get(index);
        return true;
    }
    private void setDimensions(){
        this.columnCnt = TABLE_WIDTH / OBJECT_WIDTH;
        this.rowGap = (TABLE_WIDTH % OBJECT_WIDTH) / this.columnCnt;
        this.rowCnt = (TABLE_HEIGHT / OBJECT_HEIGHT);
        this.columnGap = (TABLE_HEIGHT % OBJECT_HEIGHT) / this.rowCnt;
        this.ceilHeight = OBJECT_HEIGHT + (this.columnGap);
        this.ceilWidth = OBJECT_WIDTH + (this.rowGap);
    }

}
