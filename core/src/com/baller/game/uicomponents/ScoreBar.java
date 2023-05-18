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

import java.util.List;

import static com.baller.game.UserInterface.*;

public class ScoreBar extends UIComponent {
private static final String TITLE_TEXT = "Score";
private static final String BUTTON_STYLE = "PAUSE_BUTTON";
private static final int DEFAULT_WIDTH = 700;
private static final int DEFAULT_HEIGHT = 50;
private static final float DEFAULT_ALPHA = 0.3f;
private final Label scoreTitle;
public final Label playerName;
//private final Label time;
private final Label gameName;
private final Button pause;
private Integer score;
private final Skin skin;
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
      gameName = new Label("Game Destroyer", skin);
      setLabels();
      this.pause = new TextButton("PAUSE", skin);//, BUTTON_STYLE);
      setPauseButton();
}


private void setLabels() {
      final int X_START_OFFSSET = getPos().x - getWidth() / 2 + getWidth() / 8;
      final int Y_START_OFFSET = getPos().y - 5;
      int xOffset = X_START_OFFSSET;
      int yOffset = Y_START_OFFSET;
      var labels = List.of(scoreTitle, playerName, gameName);
      for (var label : labels) {
	    label.setAlignment(Align.left);
	    label.setFontScale(2f, 2f);
	    label.setPosition(
		Globals.convertWidth(xOffset),
		Globals.convertHeight(yOffset)
	    );
	    xOffset += 150;
	    addActor(label);
      }
}

private final static int BUTTON_WIDTH = 50;
private final static int BUTTON_HEIGHT = 50;

private void setPauseButton() {
      final int xOffset = getPos().x + getWidth() / 2 - getWidth() / 5;
      final int yOffset = getPos().y - BUTTON_HEIGHT / 2;
      pause.setSize(
	  Globals.convertWidth(BUTTON_WIDTH),
	  Globals.convertHeight(BUTTON_HEIGHT)
      );
      pause.setPosition(
	  Globals.convertWidth(xOffset),
	  Globals.convertHeight(yOffset)
      );
      addActor(pause);
}

public void addPauseListener(EventListener listener) {
      pause.addListener(new ClickListener() {
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

public void dispose() {
      background.dispose();
}
}
