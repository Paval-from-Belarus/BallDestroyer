package com.baller.game.uicomponents;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.baller.game.Globals;
import com.baller.game.UserInterface;

import java.util.function.BiConsumer;

public class ScoreBar extends UIComponent {
private static final String TITLE_TEXT = "Score";
private static final String BUTTON_STYLE = "PAUSE_BUTTON";
private static final int DEFAULT_WIDTH = 700;
private static final int DEFAULT_HEIGHT = 50;
private static final float DEFAULT_ALPHA = 0.3f;
private final Label title;
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
      setPos(Globals.FIELD_WIDTH >> 1, Globals.FIELD_HEIGHT - (height >> 1));
      this.skin = skin;
      this.title = new Label(ScoreBar.TITLE_TEXT, skin);
      setTitle();
      this.pause = new TextButton("PAUSE", skin);//, BUTTON_STYLE);
      setPauseButton();
}

private void setTitle() {
      final int xOffset = getPos().x - getWidth() / 2 + getWidth() / 8;
      final int yOffset = getPos().y - 5;
      title.setAlignment(Align.left);
      title.setFontScale(2f, 2f);
      title.setPosition(
          Globals.convertWidth(xOffset),
          Globals.convertHeight(yOffset)
      );
      addActor(title);
}

private final static int BUTTON_WIDTH = 50;
private final static int BUTTON_HEIGHT = 50;
private void setPauseButton(){
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
      pause.addListener(event -> {
            System.out.println(event.getTarget());
            return false;
      });
//      pause.addListener(new ClickListener(){
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                  System.out.println("ADDED");
//            }
//
//      });
      addActor(pause);
}
public void addPauseListener(ClickListener listener){
      pause.addListener(listener);
}
private ChangeListener getListener(BiConsumer<ChangeListener.ChangeEvent, Actor> handler){
      return new ChangeListener() {@Override
            public void changed(ChangeEvent event, Actor actor) {
                  handler.accept(event, actor);
            }
      };
}
public void dispose(){
      background.dispose();
}
}
