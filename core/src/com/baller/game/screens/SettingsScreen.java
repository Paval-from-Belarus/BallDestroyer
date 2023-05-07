package com.baller.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.baller.game.Game;
import com.baller.game.Globals;
import com.baller.game.uicomponents.SettingsMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.baller.game.UserInterface.*;

public class SettingsScreen extends AbstractScreen {
private final SettingsMenu menu;
private final Stage stage;
private final SpriteBatch batch;
private final Integer INITIAL_RESOLUTION_INDEX;
private Integer modeIndex;
private Float luckyRatio;
private Float fieldRatio;

public SettingsScreen(Skin skin, Viewport port) {
      super(skin, port);
      batch = new SpriteBatch();
      stage = new Stage(port, batch);
      menu = new SettingsMenu(skin);
      menu.addResolutionListener(this::onResolutionChanged);
      menu.addHarnessListener(v -> modeIndex = v);
      menu.addFortuneListener(v -> luckyRatio = v);
      menu.addRatioListener(v -> fieldRatio = v);
      menu.addBtnAcceptListener(this::onChangeProps);
      menu.addBtnDiscardListener(this::onScreenReturn);
      INITIAL_RESOLUTION_INDEX = Globals.CURR_SCREEN_INDEX;
      modeIndex = Globals.CURR_MODE_INDEX;
      luckyRatio = Globals.CURR_LUCKY_LEVEL;
      fieldRatio = Globals.FIELD_RATIO;
      menu.show(stage);
}

@Override
public List<UserClick> getAll() {
      final UserClick.Id[] keys = {UserClick.Id.UI_RESOLUTION};
      List<UserClick> list = new ArrayList<>();
      for (var id : keys) {
	    list.add(clickOf(id));
      }
      return list;
}

@Override
public void acceptClicks(Map<UserClick.Id, UserClick> mapper) {
      super.acceptClicks(mapper);
      //do something with rocks
}
private void onResolutionChanged(Integer index) {
      bubblesClick(UserClick.Id.UI_RESOLUTION);
      Globals.CURR_SCREEN_INDEX = index;
}

private boolean onScreenReturn(Event event) {
      onResolutionChanged(INITIAL_RESOLUTION_INDEX);
      bubblesClick(UserClick.Id.BTN_DISCARD_SETTINGS);//nothing has been changed
      return true;
}

private boolean onChangeProps(Event event) {
      var props = List.of(INITIAL_RESOLUTION_INDEX, modeIndex, luckyRatio, fieldRatio);
      var realProps = List.of(Globals.CURR_SCREEN_INDEX, Globals.CURR_MODE_INDEX,
          Globals.CURR_LUCKY_LEVEL, Globals.FIELD_RATIO);
      boolean hasChanged = false;
      int index = 0;
      for (Number value : props) {
            hasChanged = (value.equals(realProps.get(index)));
            index += 1;
            if (hasChanged) {
                  break;
            }
      }
      if (!hasChanged) {
	    onScreenReturn(event);
      } else {
            onHardnessChanged(modeIndex);
            onRatioChanged(fieldRatio);
            onFortuneChanged(luckyRatio);
            bubblesClick((UserClick.Id.BTN_ACCEPT_SETTINGS));
      }
      return true;
}

private void onHardnessChanged(Integer index) {
      bubblesClick(UserClick.Id.BTN_HARDNESS_LEVEL);
      Globals.CURR_MODE_INDEX = index;
}

private void onRatioChanged(Float value) {
      bubblesClick(UserClick.Id.BTN_FIELD_RATIO);
      Globals.FIELD_RATIO = value;
}

private void onFortuneChanged(Float value) {
      bubblesClick(UserClick.Id.BTN_LUCKY);
      Globals.CURR_LUCKY_LEVEL = value;
}

@Override
public void show() {
      Gdx.input.setInputProcessor(stage);
}

@Override
public void render(float delta) {
      batch.begin();
      menu.draw(batch);
      batch.end();
      stage.act();
      stage.draw();
}

@Override
public void dispose() {
      menu.dispose();
      batch.dispose();
      stage.dispose();
}
}
