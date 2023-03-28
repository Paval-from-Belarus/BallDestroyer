package com.baller.game.field;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.baller.game.*;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

import com.baller.game.DisplayObject.*;
import com.baller.game.players.Ball;
import com.baller.game.players.Player;
import org.jetbrains.annotations.NotNull;

import static com.baller.game.Globals.*;

public class GameField {
public static class Properties {
      private float ratio;
      private int playerCnt;
      private int trampolineCnt;
      //used for all players
      private BrickBlock.Type[] blocks;
      private Properties(){}
      private Properties(GameField field) {
	    ratio = FIELD_RATIO;
	    playerCnt = field.playerCnt;
	    trampolineCnt = field.trampolines.size();
	    blocks = field.blockList.stream()
			 .map(BrickBlock::getType)
			 .toArray(BrickBlock.Type[]::new);
      }

      private void setField(GameField field) {
	    field.setRatio(ratio);
	    field.blockTypes = blocks;
	    field.playerCnt = playerCnt;
	    field.initTrampolines(trampolineCnt);
      }
}

public enum Event {BlockCollision, BonusCollision, SideCollision, TrampolineCollision, Movement, EmptyField}

public static final int DEFAULT_TRAMPOLINE_CNT = 1;
public static final int LeftSide = 1;
public static final int BottomSide = 2;
public static final int RightSide = 3;
public static final int TopSide = 4;

public static class Message {
      private Object handle;
      private Event event;
      private Consumer<Object> callback;

      private Message(Event event) {
	    this.event = event;
      }

      public Object getValue() {
	    return this.handle;
      }

      public Event getEvent() {
	    return this.event;
      }

      public boolean callback(Object object) {
	    if (callback != null) {
		  callback.accept(object);
		  return true;
	    }
	    return false;
      }

      private void setCallback(Consumer<Object> callback) {
	    this.callback = callback;
      }

      private void setEvent(Event event) {
	    this.event = event;
      }

      private void setValue(Object value) {
	    this.handle = value;
      }
}

public static void DefaultDispatcher(Message msg, Ball player) {
      switch (msg.event) {
	    case BlockCollision -> {
		  BrickBlock block = (BrickBlock) msg.getValue();
		  Collider body = block.collider();
		  if (block.isVisible()) {
			block.setState(DisplayState.Hidden);
			block.callback();
			player.reflect(AnimatedObject.Axis.Horizontal);
		  }
	    }
	    case SideCollision -> {
		  Integer side = (Integer) msg.getValue();
		  if (side == TopSide || side == BottomSide) {
			player.reflect(AnimatedObject.Axis.Horizontal);
		  } else {
			player.reflect(AnimatedObject.Axis.Vertical);
		  }
	    }
	    case TrampolineCollision -> {
		  player.reflect(AnimatedObject.Axis.Horizontal);
	    }
	    case Movement -> {
	//	  System.out.println("Hello ball!");
	    }
      }
}

public static BrickBlock.Type[] getRandomBricks(int brickCnt) {
      BrickBlock.Type[] brickTypes = new BrickBlock.Type[brickCnt];
      Random random = new Random();
      for (int i = 0; i < brickTypes.length; i++) {
	    int typeIndex = random.nextInt(BrickBlock.Type.values().length);
	    brickTypes[i] = BrickBlock.Type.values()[typeIndex];
      }
      return brickTypes;
}

private int restBlockCnt;//it's used to calculate the rest of block on the field
private int columnCnt;
private int rowCnt;
private int rowGap;
private int columnGap;
private Object lastHandle;
private int ceilWidth;
private int ceilHeight;
private List<BrickBlock> blockList;
private BrickBlock.Type[] blockTypes;
private List<Trampoline> trampolines;
private int playerCnt;
private final Texture textBlock;
private final Texture textTrampoline;

{
      textBlock = new Texture("block.png");
      textTrampoline = new Texture("block.png");
}

public GameField(Player.Properties[] properties) {
      if (properties.length == 0)
	    throw new IllegalStateException("This function is not implemented");
      initTrampolines(properties[0].getTrampolineCnt());
      playerCnt = properties.length;
      rebuild();
}
public void verifyAll(@NotNull Player.Id[] players, @NotNull Consumer<Player.Id> callback){
      if(players.length == 0)
	    return;
      Player.Id id = players[0];
      callback.accept(id);
}
public void verify(@NotNull Player.Id player, @NotNull Consumer<Player.Id> callback){
      callback.accept(player);
}

private GameField(GameField.Properties properties) {
      properties.setField(this);
      rebuild();
}

public void update(float dt) {
      boolean isBlocked = false;
      int index;
      for (index = 0; index < trampolines.size() && !isBlocked; index++) {
	    isBlocked = isOutside(trampolines.get(index).collider());
      }
      if (isBlocked){
	    index -= 1;
	    updateTrampoline(trampolines.get(index));
	    isBlocked = isOutside(trampolines.get(index).collider());
      }
      if(isBlocked)
	    return;
      for (int i = 0; i < trampolines.size(); i++) {
	    if (i != index) {
		  updateTrampoline(trampolines.get(i));
	    }
      }

}

private void updateTrampoline(Trampoline trampoline) {
      Collider body = trampoline.collider();
      Integer side = LeftSide;
      boolean isBlocked = false;
      if (isOutside(body)) {
	    side = (Integer) lastHandle;
	    isBlocked = true;
      }
      if (!isBlocked || side == LeftSide) {
	    if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
		  trampoline.setPos(body.center().x + 10, body.center().y);
	    }
      }
      if (!isBlocked || side == RightSide) {
	    if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
		  trampoline.setPos(body.center().x - 10, body.center().y);
	    }
      }
}

public void draw(SpriteBatch batch) {
      trampolines.forEach(trampoline -> trampoline.draw(batch));
      blockList.forEach(brick -> brick.draw(batch));
}

public void setTrampolineCnt(Player.Id id, int trampolineCnt) {
      initTrampolines(trampolineCnt);
}
public Message getMessage(Ball ball) {
      Message msg = new Message(Event.Movement);
      if (restBlockCnt == 0) {
	    msg.setEvent(Event.EmptyField);
	    return msg;
      }
      if (isOutside(ball)) {
	    msg.setEvent(Event.SideCollision);
	    msg.setValue(lastHandle);
	    return msg;
      }
      if (isJumper(ball)) {
	    msg.setEvent(Event.TrampolineCollision);
	    msg.setValue(lastHandle);
      }
      if (isDestroyer(ball)) {
	    msg.setEvent(Event.BlockCollision);
	    msg.setValue(lastHandle);
      }
      return msg;
}
public static final int TRAMPOLINE_MIN_GAP = FIELD_WIDTH / 15;
private void initTrampolines(int trampolineCnt) {
      final int maxCnt = FIELD_WIDTH / (Trampoline.DEFAULT_WIDTH + 2 * TRAMPOLINE_MIN_GAP);
      if (maxCnt < trampolineCnt)
	    trampolineCnt = maxCnt;
      this.trampolines = new ArrayList<>(trampolineCnt);
      for (int i = 0; i < trampolineCnt; i++)
	    trampolines.add(new Trampoline(textTrampoline));
}

private void updateTrampolines() {
      if (this.trampolines == null)
	    initTrampolines(DEFAULT_TRAMPOLINE_CNT);
      assert trampolines.size() > 0;
      final int TR_HEIGHT = 30;
      final int pieceSize = FIELD_WIDTH / trampolines.size();
      final int gap = Math.max(pieceSize >> 1, TRAMPOLINE_MIN_GAP);
      int xOffset = 0;
      for (Trampoline trampoline : trampolines) {
	    trampoline.setPos(xOffset + gap, TR_HEIGHT);
	    xOffset += pieceSize;
      }
}

private BrickBlock.Type nextBrick(int index) {
      int realIndex = index % blockTypes.length;
      return blockTypes[realIndex];
}

public void rebuild() {
      updateParams();
      if (blockList != null)
	    blockList.clear();
      else
	    blockList = new ArrayList<>(rowCnt * columnCnt);
      int brickIndex = 0;
      int START_OFFSET_X = (columnGap >> 1) + (BrickBlock.DEFAULT_WIDTH >> 1);
      int START_OFFSET_Y = FIELD_HEIGHT - ((rowGap >> 1) + (BrickBlock.DEFAULT_HEIGHT >> 1));
      Point currPos = new Point(START_OFFSET_X, START_OFFSET_Y);
      for (int i = 0; i < rowCnt; i++) {
	    for (int j = 0; j < columnCnt; j++) {
		  BrickBlock block = new BrickBlock(textBlock, nextBrick(brickIndex));
		  block.setPos(currPos.x, currPos.y);
		  block.setCallback(this::blockCallback, null);
		  blockList.add(block);
		  currPos.translate(this.ceilWidth, 0);
	    }
	    currPos.x = START_OFFSET_X;
	    currPos.translate(0, -this.ceilHeight);
	    brickIndex += 1;
      }
      updateTrampolines();
}

public void setRatio(float ratio) {
      if (ratio > 1.0f)
	    ratio = 1f;
      if (ratio < 0.2f)
	    ratio = 0.2f;
      FIELD_RATIO = ratio;
      TABLE_HEIGHT = (int) (FIELD_HEIGHT * FIELD_RATIO);
      RED_ZONE = FIELD_HEIGHT - TABLE_HEIGHT;
}

public Optional<BrickBlock[]> getColumn(BrickBlock block) {
      int index = blockList.indexOf(block);
      if (index == -1)
	    return Optional.empty();
      int column = index % this.rowCnt;
      BrickBlock[] result = new BrickBlock[this.rowCnt];
      index = column;
      for (int i = 0; i < result.length; i++) {
	    result[i] = blockList.get(index);
	    index += columnCnt;
      }
      return Optional.of(result);
}

public Optional<BrickBlock[]> getRow(BrickBlock block) {
      int index = blockList.indexOf(block);
      if (index == -1)
	    return Optional.empty();
      int row = index / this.rowCnt;
      BrickBlock[] result = new BrickBlock[this.columnCnt];
      index = row * this.columnCnt;
      for (int i = 0; i < result.length; i++) {
	    result[i] = blockList.get(index);
	    index += 1;
      }
      return Optional.of(result);
}

public BrickBlock[] toArray() {
      return blockList.toArray(new BrickBlock[0]);
}

public Properties getProperties() {
      return new Properties(this);
}

public void dispose() {
      if (textBlock != null)
	    textBlock.dispose();
}

private void updateParams() {
      this.columnCnt = TABLE_WIDTH / BrickBlock.DEFAULT_WIDTH;
      this.rowGap = (TABLE_WIDTH % BrickBlock.DEFAULT_WIDTH) / this.columnCnt;
      this.rowCnt = (TABLE_HEIGHT / BrickBlock.DEFAULT_HEIGHT);
      this.columnGap = (TABLE_HEIGHT % BrickBlock.DEFAULT_HEIGHT) / this.rowCnt;
      this.ceilHeight = BrickBlock.DEFAULT_HEIGHT + (this.columnGap);
      this.ceilWidth = BrickBlock.DEFAULT_WIDTH + (this.rowGap);
      this.restBlockCnt = rowCnt * columnCnt;
      if (this.blockTypes == null) //default initializer
	    this.blockTypes = getRandomBricks(this.restBlockCnt);
}

private void blockCallback(Object nullable) {
      this.restBlockCnt -= 1;
}

private boolean isJumper(Ball player) {
      Collider ball = player.collider();
      boolean hasCollision = false;
      for (int i = 0; i < trampolines.size() && !hasCollision; i++) {
	    Collider ramp = trampolines.get(i).collider();
	    Trampoline trampoline = trampolines.get(i);
	    hasCollision = (ball.center().y - player.getWidth() / 2 <= ramp.center().y) &&
			       ((ball.center().x <= ramp.center().x + trampoline.getWidth() / 2) && (ball.center().x >= ramp.center().x - trampoline.getWidth() / 2));
      }
      return hasCollision;

}

private boolean isOutside(Ball player) {
      return isOutside(player.collider());
}

private boolean isOutside(Collider body) {
      final Point[] vertexes = new Point[]{
	  new Point(0, FIELD_HEIGHT),
	  new Point(0, 0),
	  new Point(FIELD_WIDTH, 0),
	  new Point(FIELD_WIDTH, FIELD_HEIGHT)
      };
      int iVertex = 0;
      int side;
      boolean isOutside = false;
      for (side = LeftSide; !isOutside && side <= TopSide; side++) {
	    isOutside = body.collides(vertexes[iVertex], vertexes[(iVertex + 1) % vertexes.length]);
	    iVertex++;
      }
      lastHandle = Integer.valueOf(side - 1);
      return isOutside;
}

private boolean isDestroyer(Ball player) {
      Collider body = player.collider();
      if (body.center().y < RED_ZONE)
	    return false;
      int column = body.center().x / this.ceilWidth;
      int row = (TABLE_HEIGHT - (body.center().y - RED_ZONE)) / this.ceilHeight;
      if (row >= rowCnt)
	    row -= 1;
      int index = row * this.columnCnt + column;
      lastHandle = blockList.get(index);
      return true;
}


}
