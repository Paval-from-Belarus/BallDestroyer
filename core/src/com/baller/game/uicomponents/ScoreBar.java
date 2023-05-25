package com.baller.game.uicomponents;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.baller.game.Globals;

import java.sql.Time;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.baller.game.UserInterface.*;

public class ScoreBar extends UIComponent {
private static final String TITLE_TEXT = "Score:XXX";
private static final String BRICKS_COUNTER = "Rest:XXX";
private static final String TIME_TITLE = "Time:0000.000";
private static final String BUTTON_STYLE = "PAUSE_BUTTON";
private static final int DEFAULT_WIDTH = 700;
private static final int DEFAULT_HEIGHT = (int) (Globals.FIELD_HEIGHT * Globals.FIELD_TOP_RATIO);
private static final float DEFAULT_ALPHA = 0.9f;
private final Label scoreTitle;
public final Label playerName;
//private final Label time;
private final Button pause;
private final Button statistics;
private final Skin skin;
private final Label brickCounter;
private final Label timeLabel;
private Texture background;

{
      background = new Texture("block.png");
}

public ScoreBar(Skin skin) {
      super();
      setTexture(background);
      setAlpha(DEFAULT_ALPHA);
      setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
      setPos(Globals.FIELD_WIDTH >> 1, Globals.FIELD_HEIGHT - (DEFAULT_HEIGHT >> 1));
      this.skin = skin;
      this.scoreTitle = new Label(ScoreBar.TITLE_TEXT, skin);
      playerName = new Label("John", skin);
      brickCounter = new Label(ScoreBar.BRICKS_COUNTER, skin);
      timeLabel = new Label(TIME_TITLE, skin);
      setLabels();
      this.pause = new TextButton("PAUSE", skin);//, BUTTON_STYLE);
      this.statistics = new TextButton("Statistics", skin);
      setButtons();
}



private void setLabels() {
      final int X_START = getWidth() / 50;
      final int Y_START = getPos().y - 5;
      int xOffset = X_START;
      int yOffset = Y_START;
      var labels = List.of(scoreTitle, brickCounter, timeLabel, playerName);
      for (var label : labels) {
	    label.setAlignment(Align.left);
	    label.setFontScale(1.5f, 1.5f);
	    label.setPosition(
		Globals.convertWidth(xOffset),
		Globals.convertHeight(yOffset)
	    );
	    xOffset += Globals.convertWidth(label.getWidth()) + 20;
	    addActor(label);
      }
}

private final static int BUTTON_WIDTH = 100;
private final static int BUTTON_HEIGHT = 50;

private void setButtons() {
      int xOffset = 5 * getWidth() / 8;
      final int yOffset = getPos().y - BUTTON_HEIGHT / 2;
      var buttons = List.of(pause, statistics);
      for (var button : buttons) {
	    button.setSize(
		Globals.convertWidth(BUTTON_WIDTH),
		Globals.convertHeight(BUTTON_HEIGHT)
	    );
	    button.setPosition(
		Globals.convertWidth(xOffset),
		Globals.convertHeight(yOffset)
	    );
	    xOffset += BUTTON_WIDTH;
	    addActor(button);
      }
}

public void addPauseListener(EventListener listener) {
      pause.addListener(new ClickListener() {
	    @Override
	    public void clicked(InputEvent event, float x, float y) {
		  listener.handle(event);
	    }
      });
}
public void addStatisticsListener(EventListener listener) {
      statistics.addListener(new ClickListener() {
	    @Override
	    public void clicked(InputEvent event, float x, float y) {
		  listener.handle(event);
	    }
      });
}
/**
 * rocks accept any value that will used to show current score. Current score will in form of Integer
 */
public void onScoreChanged(RocksHandler rocks) {
      rocks.accept(score -> scoreTitle.setText("Score:" + score));
}
public void onRestChanged(RocksHandler rocks) {
      rocks.accept(score -> brickCounter.setText("Rest:" + score));
}
public void onTimeChanged(RocksHandler rocks) {
      rocks.accept(time -> {
	    Long millis = (Long) time;
	    String formatted = String.format("%02d.%02d", TimeUnit.MILLISECONDS.toMinutes(millis), TimeUnit.MILLISECONDS.toSeconds(millis));
	    timeLabel.setText("Time:" + formatted);
      });
}
public void dispose() {
      background.dispose();
}
}
