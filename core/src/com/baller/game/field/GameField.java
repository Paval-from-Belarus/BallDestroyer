package com.baller.game.field;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.badlogic.gdx.math.MathUtils;
import com.baller.game.common.AnimatedObject;
import com.baller.game.common.Collider;
import com.baller.game.common.DisplayObject;
import com.baller.game.common.SquareCollider;
import com.baller.game.players.Ball;
import com.baller.game.players.Player;
import com.baller.game.serializer.AbstractSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.baller.game.Globals.*;

public class GameField {
public static class Properties extends AbstractSerializable<GameField> {
      private float ratio;
      private BrickBlock.Type[] blocks;

      public Properties() {
	    var patterns = List.of("Ratio=(.+)", "Blocks=\\[(.+)\\]");
	    List<Consumer<String>> handlers = List.of(this::setRatio, this::setBlocks);
	    addPatterns(patterns);
	    addHandlers(handlers);
	    setEmptyState(true);
      }

      private Properties(GameField field) {
	    this();
	    ratio = FIELD_RATIO;
	    blocks = field.blockList.stream()
			 .map(BrickBlock::getType)
			 .toArray(BrickBlock.Type[]::new);
	    setEmptyState(false);
      }

      @Override
      public String serialize() {
	    return "Ratio=" + String.format("%.3f", ratio) + "\n" +
		       "Blocks=" + Arrays.toString(blocks) + "\n";
      }

      @Override
      public GameField construct() {
	    GameField field = new GameField();
	    field.setRatio(ratio);
	    field.blockTypes = blocks;
	    return field;
      }

      private void setRatio(String source) {
	    this.ratio = Float.parseFloat(source);
      }

      private void setBlocks(String source) {
	    String[] items = source.split(", *");
	    this.blocks = Arrays.stream(items)
			      .map(BrickBlock.Type::valueOf)
			      .toArray(BrickBlock.Type[]::new);
      }

      @Override
      public String[] getFieldNames() {
	    return new String[]{"ratio", "blocks"};
      }
}

public enum Event {BlockCollision, BonusCollision, SideCollision, TrampolineCollision, Movement, EmptyField}

public static final int LeftSide = 1;
public static final int BottomSide = 2;
public static final int RightSide = 3;
public static final int TopSide = 4;

public static class Message implements Serializable {
      private Object handle;
      private Event event;

      private Message(Event event) {
	    this.event = event;
      }

      public Object getValue() {
	    return this.handle;
      }

      public Event getEvent() {
	    return this.event;
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
		  List<BrickCollision> collisions = (List<BrickCollision>) msg.getValue();
		  collisions.forEach(collision -> {
			var brick = collision.brick();
			if (collision.type() != SquareCollider.SideType.None) {
			      AnimatedObject.Axis axis = (collision.type() == SquareCollider.SideType.Left || collision.type() == SquareCollider.SideType.Right) ?
							     AnimatedObject.Axis.Vertical : AnimatedObject.Axis.Horizontal;
			      player.reflect(axis);
			}
			if (brick.isVisible() && brick.getType() != BrickBlock.Type.Invincible) {
			      brick.setType(BrickBlock.Type.Destroyed);
			}
		  });

	    }
	    case BonusCollision -> {
		  //do nothing

	    }
	    case SideCollision -> {
		  Integer side = (Integer) msg.getValue();
		  if (side == TopSide && player.getVelocity().y > 0) {
			player.reflect(AnimatedObject.Axis.Horizontal);
		  }
		  if (side == LeftSide || side == RightSide) {
			player.reflect(AnimatedObject.Axis.Vertical);
			player.update(0.01f);
		  }
	    }
	    case TrampolineCollision -> {
		  if (player.getVelocity().y < 0)
			player.reflect(AnimatedObject.Axis.Horizontal);
	    }
	    case Movement, EmptyField -> {
		  //	  System.out.println("Hello ball!");
	    }
      }
}

public static BrickBlock.Type[] getRandomBricks(int brickCnt) {
      BrickBlock.Type[] brickTypes = new BrickBlock.Type[brickCnt];
      final int[] lPivots = new int[BrickBlock.Type.values().length];
      int pivot = 0;
      for (int i = 0; i < BrickBlock.Type.values().length; i++) {
	    lPivots[i] = pivot;
	    pivot += BrickBlock.Type.values()[i].weight();
      }
      for (int i = 0; i < brickTypes.length; i++) {
	    int weight = MathUtils.random(0, BrickBlock.Type.totalWeight() - 1);
	    int typeIndex = 0;
	    while (weight >= lPivots[typeIndex]) {
		  typeIndex += 1;
	    }
	    brickTypes[i] = BrickBlock.Type.values()[typeIndex - 1];
	    if (brickTypes[i] == BrickBlock.Type.Destroyed) {
		  System.out.println("Destroyed");
	    }
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
private List<PlayerTrampolines> playerTrampolines;
private List<Player.Id> players;
private final List<FlyerBonus> bonuses;
private List<Message> msgQueue = new ArrayList<>();
private TexturePool textures;
private Background background;

private GameField() {
      bonuses = new ArrayList<>();
      players = new ArrayList<>();
      textures = new TexturePool();
      background = new Background(textures.getFieldBGD());
}

public GameField(Player.Properties[] properties) {
      this();
      if (properties.length == 0)
	    throw new IllegalStateException("This function is not implemented");
      addPlayers(properties);
}

public void addPlayers(Player.Properties[] properties) {
      playerTrampolines = Arrays.stream(properties)
			      .map(player -> new PlayerTrampolines(player.getId(), player.getTrampolineCnt()))
			      .collect(Collectors.toList());
}

/**
 * @return verify as much players as possible
 */
public int verifyAll(@NotNull Player.Id[] players) {
      int verifiedCnt = 0;
      for (Player.Id id : players) {
	    if (verify(id))
		  verifiedCnt += 1;
	    else
		  break;
      }
      return verifiedCnt;
}

public boolean verify(@NotNull Player.Id player) {
      boolean isAdded = false;
      if (nextTrampolineRow < MAX_TRAMPOLINES_ROW_CNT) {
	    players.add(player);
	    isAdded = true;
      }
      return isAdded;
}

/**
 * @return if player exists return true<br>
 * else return false
 */
public boolean remove(@NotNull Player.Id player) {
      boolean isRemoved = players.remove(player);
      if (isRemoved) {
	    playerTrampolines = playerTrampolines.stream()
				    .filter(t -> !t.belongs(player))
				    .collect(Collectors.toList());
      }
      return isRemoved;
}

private final List<FlyerBonus> removedBonuses = new ArrayList<>();

public void update(float dt) {
      boolean isBlocked = false;
      int index;
      for (index = 0; index < trampolines.size() && !isBlocked; index++) {
	    isBlocked = isOutside(trampolines.get(index).collider());
      }
      if (isBlocked) {
	    index -= 1;
	    updateTrampoline(trampolines.get(index));
	    isBlocked = isOutside(trampolines.get(index).collider());
      }
      for (var bonus : bonuses) {
	    var body = bonus.collider();
	    bonus.update(dt);
	    if (isOutside(body)) {
		  removedBonuses.add(bonus);
	    }
      }
      bonuses.removeAll(removedBonuses);
      removedBonuses.clear();
      if (isBlocked)
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

public void draw(final SpriteBatch batch) {
      background.draw(batch);
      drawables.forEach(item -> item.draw(batch));
      assert bonuses != null;
      bonuses.forEach(bonus -> bonus.draw(batch));
}

public void setTrampolineCnt(Player.Id id, int trampolineCnt) {
      playerTrampolines.stream()
	  .filter(t -> t.belongs(id))
	  .findFirst()
	  .ifPresent(batch -> batch.setCapacity(trampolineCnt));
}

private List<DisplayObject> drawables;

private void absorbAll() {
      drawables = new ArrayList<>();
      drawables.addAll(blockList);
      drawables.addAll(trampolines);
}

public void checkCollides(Ball ball) {
      boolean isCollides = false;
      int index = 0;
      while (!isCollides && index < drawables.size()) {
	    var item = drawables.get(index);
	    if (!item.isStatic() && (isCollides = item.collides(ball)))
		  lastHandle = item;
	    index += 1;
      }

}

private Player.Id currentPlayer;

public void mark(Player.Id id) {
      currentPlayer = id;
}

public void release() {
      currentPlayer = null;
}


private List<Message> collectBonuses(Ball ball) {
      List<Message> messages = new ArrayList<>();
      Player.Id player = getCurrentPlayer();
      if (player == null)
	    return List.of();
      var trampolines = playerTrampolines.stream().filter(trs -> trs.belongs(player)).findFirst();
      if (trampolines.isEmpty()) {
	    return List.of();
      }
      //todo: doesn't work for mutliple players
      trampolines.get().list().forEach(trampoline -> {
	    for (var bonus : bonuses) {
		  if (bonus.collider().collides(trampoline.collider())) {
			var msg = new Message(Event.BonusCollision);
			Function<Object, Object> callback = bonus.getCallback();
			Integer score = bonus.getScore();
			var collision = new BonusCollision(bonus.getType(), score, callback);
			msg.setValue(collision);
			messages.add(msg);
			removedBonuses.add(bonus);
		  }
	    }
      });
      bonuses.removeAll(removedBonuses);
      removedBonuses.clear();
      return messages;
}

public List<Message> getMessage(Ball ball) {
      List<Message> messages = new ArrayList<>(collectBonuses(ball));
      Message msg = new Message(Event.Movement);
      if (restBlockCnt == 0) {
	    msg.setEvent(Event.EmptyField);
      }
      if (isOutside(ball)) {
	    msg.setEvent(Event.SideCollision);
	    msg.setValue(lastHandle);
	    messages.add(msg);
	    return messages;
      }
      //checkCollides(ball);
      if (isJumper(ball)) {
	    msg.setEvent(Event.TrampolineCollision);
	    msg.setValue(lastHandle);
	    messages.add(msg);
	    return messages;
      }
      if (isDestroyer(ball)) {
	    msg.setEvent(Event.BlockCollision);
	    msg.setValue(lastHandle);
      }
      messages.add(msg);
      return messages;
}


public static final int TRAMPOLINE_MIN_GAP = FIELD_WIDTH / 15;
public static final int RED_ZONE_BORDER = 40;
private static int MAX_TRAMPOLINES_ROW_CNT = 1;
private int nextTrampolineRow = 0;//index

private void resetTrampolineHeight() {
      nextTrampolineRow = 0;
}

private int nextTrampolineHeight() {
      int height = 0;
      if (nextTrampolineRow < MAX_TRAMPOLINES_ROW_CNT) {
	    height = (int) ((float) nextTrampolineRow / MAX_TRAMPOLINES_ROW_CNT * RED_ZONE + RED_ZONE_BORDER);
	    nextTrampolineRow += 1;
      }
      return height;
}

private void initAllTrampolines() {
      trampolines = new ArrayList<>();
      resetTrampolineHeight();
      for (Player.Id id : players) {
	    var list = initTrampolines(id);
	    if (list.size() > 0) {
		  trampolines.addAll(list);
	    }
      }
}

private @NotNull List<Trampoline> initTrampolines(Player.Id id) {
      if (nextTrampolineRow >= MAX_TRAMPOLINES_ROW_CNT)
	    return List.of();
      var dummyBatch = playerTrampolines.stream()
			   .filter(b -> b.belongs(id))
			   .findFirst();
      List<Trampoline> list = List.of();
      if (dummyBatch.isPresent()) {
	    var batch = dummyBatch.get();
	    int count = batch.capacity();
	    final int maxCnt = FIELD_WIDTH / (Trampoline.DEFAULT_WIDTH + 2 * TRAMPOLINE_MIN_GAP);
	    if (maxCnt < count)
		  count = maxCnt;
	    batch.clear();
	    for (int i = 0; i < count; i++) {
		  batch.add(new Trampoline(textures.getTrampoline()));
	    }
	    alignTrampolines(batch.list(), nextTrampolineHeight());
	    list = batch.list();
      }
      return list;
}

private void alignTrampolines(List<Trampoline> trampolines, final int height) {
      if (this.playerTrampolines == null)
	    return;
      assert trampolines.size() > 0 && height > 0;
      final int pieceSize = FIELD_WIDTH / trampolines.size();
      final int gap = Math.max(pieceSize >> 1, TRAMPOLINE_MIN_GAP);
      int xOffset = 0;
      for (Trampoline trampoline : trampolines) {
	    trampoline.setPos(xOffset + gap, height);
	    xOffset += pieceSize;
      }
}

private void onBlockStateChanged(BrickBlock block, BrickBlock.Type type) {
      if (block.getType() != BrickBlock.Type.Destroyed && type == BrickBlock.Type.Destroyed)
	    restBlockCnt -= 1;
      if (block.getType() == BrickBlock.Type.Destroyed && type != BrickBlock.Type.Destroyed)
	    restBlockCnt += 1;
}

private BrickBlock nextBlock(int index) {
      int realIndex = index % blockTypes.length;
      BrickBlock.Type type = blockTypes[realIndex];
      BrickBlock block = new BrickBlock(textures.getBlock(), type);
      block.onStateChanged(this::onBlockStateChanged);
      return block;
}

public void rebuild() {
      updateParams();
      if (blockList != null)
	    blockList.clear();
      else
	    blockList = new ArrayList<>(rowCnt * columnCnt);
      this.restBlockCnt = 0;
      int brickIndex = 0;
//      int START_OFFSET_X = (FIELD_WIDTH - TABLE_WIDTH) / 2 + (BrickBlock.DEFAULT_WIDTH >> 1);
      int START_OFFSET_X = (columnGap >> 1) + (BrickBlock.DEFAULT_WIDTH >> 1);
      int START_OFFSET_Y = FIELD_HEIGHT - ((rowGap >> 1) + (BrickBlock.DEFAULT_HEIGHT >> 1));
      Point currPos = new Point(START_OFFSET_X, START_OFFSET_Y);
      for (int i = 0; i < rowCnt; i++) {
	    for (int j = 0; j < columnCnt; j++) {
		  BrickBlock block = nextBlock(brickIndex);
		  if (block.isVisible() && block.isDestroyable())
			restBlockCnt += 1;
		  block.setPos(currPos.x, currPos.y);
		  blockList.add(block);
		  currPos.translate(this.ceilWidth, 0);
		  brickIndex += 1;
	    }
	    currPos.x = START_OFFSET_X;
	    currPos.translate(0, -this.ceilHeight);
      }
      initAllTrampolines();
      absorbAll();
}

public void setRatio(float ratio) {
      if (ratio > 0.7f)
	    ratio = 0.7f;
      if (ratio < 0.2f)
	    ratio = 0.2f;
      FIELD_RATIO = ratio;
      MAX_TRAMPOLINES_ROW_CNT = RED_ZONE / TRAMPOLINE_HEIGHT; //todo: replace with more beautiful formula
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

public @NotNull BrickBlock[] getRow(BrickBlock block) {
      int index = blockList.indexOf(block);
      BrickBlock[] result = new BrickBlock[0];
      if (index != -1) {
	    int row = index / this.columnCnt;
	    result = new BrickBlock[this.columnCnt];
	    index = row * this.columnCnt;
	    for (int i = 0; i < result.length; i++) {
		  result[i] = blockList.get(index);
		  index += 1;
	    }
      }
      return result;
}

public BrickBlock[] toArray() {
      return blockList.toArray(new BrickBlock[0]);
}

public Properties serialize() {
      return new Properties(this);
}

public void dispose() {
      textures.dispose();
}

private void updateParams() {
      this.columnCnt = TABLE_WIDTH / BrickBlock.DEFAULT_WIDTH;
      this.rowGap = (TABLE_WIDTH % BrickBlock.DEFAULT_WIDTH) / this.columnCnt;
      this.rowCnt = (TABLE_HEIGHT / BrickBlock.DEFAULT_HEIGHT);
      this.columnGap = (TABLE_HEIGHT % BrickBlock.DEFAULT_HEIGHT) / this.rowCnt;
      this.ceilHeight = BrickBlock.DEFAULT_HEIGHT + (this.columnGap);
      this.ceilWidth = BrickBlock.DEFAULT_WIDTH + (this.rowGap);
      if (this.blockTypes == null) //default initializer
	    this.blockTypes = getRandomBricks(rowCnt * columnCnt);
}

private boolean isJumper(Ball player) {
      Collider ball = player.collider();
      boolean hasCollision = false;
      for (int i = 0; i < trampolines.size() && !hasCollision; i++) {
	    Collider ramp = trampolines.get(i).collider();
	    hasCollision = ball.collides(ramp);
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
      lastHandle = side - 1; //the value of lastHandle is undefined in common case
      return isOutside;
}

private boolean isDestroyer(Ball player) {
      Collider body = player.collider();
//      if (body.center().y + < RED_ZONE)
//	    return false;
      boolean isDestroyer = false;
      int index;
      List<BrickCollision> bricks = new ArrayList<>();
      for (index = 0; !isDestroyer && index < blockList.size(); index++) {
	    var brick = blockList.get(index);
	    isDestroyer = brick.isVisible() && brick.collider().collides(body);
	    if (isDestroyer) {
		  bricks.add(new BrickCollision(brick, ((SquareCollider) brick.collider()).getCollisionSide()));
//		  var collision = new BonusCollision(brick, ((SquareCollider)body).getCollisionSide());
//		  System.out.println(brick.getType());
		  raiseBonus(brick, player);
//		  collisions.add(collision);
	    }
      }
//      System.out.println(body.center());
//      System.out.println(blockList.get(6).collider().center());
//      if (isDestroyer) {
//	    var brick = blockList.get(index - 1);
//	    if (body.collides(brick.collider())) {
//		  var side = ((SquareCollider) brick.collider()).getCollisionSide();
//		  BlockCollision collision = new BlockCollision(brick, side);
//		  setBlockCallback(collision, brick.getType());
//		  lastHandle = collision;
//	    }
//      }
      lastHandle = bricks;

      return bricks.size() != 0;
//      int column = body.center().x / this.ceilWidth;
//      int row = (TABLE_HEIGHT - (body.center().y - RED_ZONE)) / this.ceilHeight;
//      if (row >= rowCnt)
//	    row -= 1;
//      int index = row * this.columnCnt + column;
//      boolean response = false;
//      if (index < blockList.size()) {
//	    var brick = blockList.get(index);
//	    if (brick.isVisible() && body.collides(blockList.get(index).collider())) {
//		  var side = ((SquareCollider) brick.collider()).getCollisionSide();
//		  System.out.println(side);
//		  BlockCollision collision = new BlockCollision(brick, side);
//		  setBlockCallback(collision, brick.getType());
//		  lastHandle = collision;
//		  response = true;
//	    }
//      }
//      return response;
}

private @Nullable Player.Id getCurrentPlayer() {
      return currentPlayer;
}

private void raiseBonus(@NotNull BrickBlock brick, @NotNull Ball ball) {
      if (brick.getType() != BrickBlock.Type.Invincible) {
	    Function<Object, Object> callback = switch (brick.getType()) {
		  case Killer -> (o) -> getRow(brick);
		  case MultiTrampoline -> (Object count) -> {
			this.setTrampolineCnt(getCurrentPlayer(), (Integer) count);
			initAllTrampolines();
			absorbAll();
			return null;
		  };
		  case DamageBonus, Invincible -> (o) -> null;
		  case MultiBall -> (o) -> ball;
		  default -> null;
	    };
	    int score = switch (brick.getType()) {
		  case Plain -> MathUtils.random(1, 4);
		  case DamageBonus -> MathUtils.random(-5, -3);
		  case Killer -> 3;
		  default -> 1;
	    };
	    FlyerBonus bonus = new FlyerBonus(textures.getFlyerBonus(brick.getType()), brick.getType(), score, callback);
	    assert bonuses != null;
	    var point = brick.getPos();
	    bonus.setPos(point.x, point.y);
	    bonuses.add(bonus);
      }
}
}
