package com.baller.game.uicomponents;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.baller.game.Game;
import com.baller.game.Globals;

import javax.swing.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.zip.GZIPOutputStream;

public class SettingsMenu extends UIComponent {
private static final int DEFAULT_WIDTH = (int) (Globals.FIELD_WIDTH);
private static final int DEFAULT_HEIGHT = (int) (Globals.FIELD_HEIGHT);
private static final String BTN_CANCEL_TEXT = "X";
private static final String BTN_ACCEPT_TEXT = "Apply";
private final SelectBox<String> selectResolution;
private final SelectBox<String> selectHardness;
private final Slider fieldRatioSlider;
private final Slider fortuneSlider;
private final Button btnDiscard;
private final Button btnAccept;
private final Skin skin;
private Texture background = new Texture("block.png");

public SettingsMenu(Skin skin) {
      super();
      setTexture(background);
      setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
      setPos(Globals.FIELD_WIDTH >> 1, Globals.FIELD_HEIGHT >> 1);
      selectResolution = new SelectBox<>(skin);
      selectHardness = new SelectBox<>(skin);
      fieldRatioSlider = new Slider(0.1f, 1.0f, 0.1f, false, skin);
      fortuneSlider = new Slider(0.0f, 1.0f, 0.33f, false, skin);
      btnAccept = new TextButton(BTN_ACCEPT_TEXT, skin);
      btnDiscard = new TextButton(BTN_CANCEL_TEXT, skin);
      this.skin = skin;
      setSelectResolution();
      setSelectHardness();
      setBtnAccept();
      setBtnDiscard();
      setFortuneSlider();
      setRatioSlider();
      alignItems(
	  List.of(selectHardness, selectResolution, fortuneSlider, fieldRatioSlider),
	  List.of("Ball Velocity", "Screen Resolution", "Fortune", "Field Ratio"));
}
private void alignItems(List<Widget> items, List<String> labels) {
      assert items.size() == labels.size();
      final int START_OFFSET_Y = (int) (getHeight() * 0.80f);
      final int OFFSET_X = (getWidth() >> 1) - 220;
      final int WIDGET_HEIGHT = 100;
      final int WIDGET_WIDTH = (int) (getWidth() * 0.7f);
      int index = 0;
      int yOffset = START_OFFSET_Y;
      for (var item : items) {
	  item.setPosition(
	      Globals.convertWidth(OFFSET_X),
	      Globals.convertHeight(yOffset)
	  );
	  Label label = new Label(labels.get(index++), skin);
	  label.setPosition(
	      Globals.convertWidth(OFFSET_X + WIDGET_WIDTH / 2) - label.getWidth() / 2.0f,
	      Globals.convertHeight(yOffset + WIDGET_HEIGHT / 2 + 10),
	      Align.center
	  );
	  label.setFontScale(2f);
	  addActor(label);
	  item.setSize(WIDGET_WIDTH, WIDGET_HEIGHT / 2.0f);
	  yOffset -= WIDGET_HEIGHT;
	  addActor(item);
      }
}

private void setSelectResolution() {
      Array<String> items = new Array<>();
      for (Game.ResolutionMode mode : Game.ResolutionMode.values())
	    items.add(mode.name() + " Screen");
      selectResolution.setItems(items);
      selectResolution.setSelectedIndex(Globals.CURR_SCREEN_INDEX);
      selectResolution.setAlignment(Align.center);
}
private void setSelectHardness() {
      Array<String> items = new Array<>();
      for (var level : Game.HardnessLevel.values()) {
	    items.add(level.name());
      }
      selectHardness.setItems(items);
      selectHardness.setSelectedIndex(Globals.CURR_MODE_INDEX);
      selectHardness.setAlignment(Align.center);
}
private void setBtnDiscard(){
      final int BTN_SIZE = 60;
      btnDiscard.setSize(
	  Globals.convertWidth(BTN_SIZE),
	  Globals.convertHeight(BTN_SIZE)
      );
      btnDiscard.setPosition(
	  Globals.convertWidth((int) (getWidth() * 0.03f)),
	  Globals.convertHeight((int) (getHeight() * 0.85))
      );
      addActor(btnDiscard);
}
private void setBtnAccept(){
      final int BTN_WIDTH = 180;
      final int BTN_HEIGHT = 60;
      btnAccept.setSize(
	  Globals.convertWidth(BTN_WIDTH),
	  Globals.convertHeight(BTN_HEIGHT)
      );
      btnAccept.setPosition(
	  Globals.convertWidth(getWidth() >> 1) - btnAccept.getWidth() / 2.0f,
	  Globals.convertHeight((int) (getHeight() * 0.03f))
      );
      addActor(btnAccept);
}
private void setRatioSlider(){
      fieldRatioSlider.setValue(Globals.FIELD_RATIO);
}
private void setFortuneSlider(){
      fortuneSlider.setValue(Globals.CURR_LUCKY_LEVEL);
}

/**
 * Consumer feed the index of item
 */
public void addResolutionListener(Consumer<Integer> listener) {
      selectResolution.addListener(new ChangeListener() {
	    @Override
	    public void changed(ChangeEvent event, Actor actor) {
		  listener.accept(selectResolution.getSelectedIndex());
	    }
      });
}
public void addHarnessListener(Consumer<Integer> listener) {
      selectHardness.addListener(new ChangeListener() {
	    @Override
	    public void changed(ChangeEvent event, Actor actor) {
		  listener.accept(selectHardness.getSelectedIndex());
	    }
      });
}
public void addFortuneListener(Consumer<Float> listener) {
      fortuneSlider.addListener(new ChangeListener(){
	    @Override
	    public void changed(ChangeEvent event, Actor actor) {
		  listener.accept(fortuneSlider.getValue());
	    }
      });
}
public void addRatioListener(Consumer<Float> listener) {
      fieldRatioSlider.addListener(new ChangeListener(){
	    @Override
	    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
		  listener.accept(fieldRatioSlider.getValue());
	    }
      });
}
public void addBtnDiscardListener(EventListener listener) {
      btnDiscard.addListener(new ClickListener(){
	    @Override
	    public void clicked(InputEvent event, float x, float y) {
		  listener.handle(event);
	    }
      });
}
public void addBtnAcceptListener(EventListener listener) {
      btnAccept.addListener(new ClickListener(){
	    @Override
	    public void clicked(InputEvent event, float x, float y) {
		  listener.handle(event);
	    }
      });
}
public void dispose(){
	background.dispose();
}
}
