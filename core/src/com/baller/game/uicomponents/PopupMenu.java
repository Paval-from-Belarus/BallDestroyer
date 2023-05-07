package com.baller.game.uicomponents;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.baller.game.Globals;

import java.util.List;
import java.util.function.Consumer;

public class PopupMenu extends UIComponent{
private static final String TITLE_TEXT = "Game on pause...";
private static final String BTN_SAVE_TEXT = "Save game";
private static final String BTN_RESUME_TEXT = "Resume";
private static final String BTN_RESTORE_TEXT = "Restore game";
private static final String BUTTON_STYLE = "BUTTON_MENU";
private static int DEFAULT_WIDTH = (int) (Globals.FIELD_WIDTH * 0.7f);
private static int DEFAULT_HEIGHT = (int) (Globals.FIELD_HEIGHT * 0.7f);
private static final int BUTTON_WIDTH = (int) (DEFAULT_WIDTH * 0.8f);
private static final int BUTTON_HEIGHT = 80;

private static final int SELECT_WIDTH = (int) (DEFAULT_WIDTH * 0.8);
private static final int SELECT_HEIGHT = 80;
private static final int BUTTON_GAP = 80;
private Texture background;
private Skin skin;
private final Label title;
private final Button btnSave;
private final Button btnResume;
private final Button btnRestore;
private final SelectBox<String> selectResolution;
{
      background = new Texture("block.png");
}
public PopupMenu(Skin skin){
      super();
      this.skin = skin;
      setTexture(background);
      setAlpha(0.0f);
      setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
      setPos(Globals.FIELD_WIDTH >> 1, Globals.FIELD_HEIGHT >> 1);
      this.title = new Label(PopupMenu.TITLE_TEXT, skin);
      this.btnSave = new TextButton(PopupMenu.BTN_SAVE_TEXT, skin);
      this.btnResume = new TextButton(PopupMenu.BTN_RESUME_TEXT, skin);
      this.btnRestore = new TextButton(PopupMenu.BTN_RESTORE_TEXT, skin);
      this.selectResolution = new SelectBox<>(skin);
//      setTitle();
      setButtons();
      setSelectResolution();
}
private void setSelectResolution(){
      final int yOffset = (int)(getHeight() * 0.6);
      selectResolution.setSize(
          Globals.convertWidth(SELECT_WIDTH),
          Globals.convertHeight(SELECT_HEIGHT)
      );
      selectResolution.setPosition(
          Globals.convertWidth((getPos().x >> 1) - 10),
          Globals.convertHeight(getPos().y - yOffset)
      );
      Array<String> items = new Array();
      for(String item : Globals.SCREEN_RESOLUTIONS)
            items.add(item);
      selectResolution.setItems(items);
      selectResolution.setSelectedIndex(Globals.CURR_SCREEN_INDEX);
      addActor(selectResolution);
}
private void setTitle(){
      final int xOffset = (getPos().x >> 1) + 100;
      final int yOffset = getPos().y + (getHeight() >> 1);
      title.setAlignment(Align.center);
      title.setFontScale(3f, 3f);
      title.setPosition(
          Globals.convertWidth(xOffset),
          Globals.convertHeight(yOffset)
      );
      addActor(title);
}
private void setButtons(){
      final int xOffset = (getPos().x >> 1) - 10;
      int yOffset = getPos().y + (getHeight() >> 1) - (getHeight() >> 2) - 40;
      List<Button> buttons = List.of(btnSave, btnResume, btnRestore);
      for(Button button : buttons){
            button.setSize(
                Globals.convertWidth(BUTTON_WIDTH),
                Globals.convertHeight(BUTTON_HEIGHT)
            );
            button.setPosition(
                Globals.convertWidth(xOffset),
                Globals.convertHeight(yOffset)
            );
            yOffset -= BUTTON_GAP;
            addActor(button);
      }
}
public void addSaveListener(EventListener listener){
      btnSave.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                  listener.handle(event);
            }
      });
}
public void addRestoreListener(EventListener listener){
      btnRestore.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                  listener.handle(event);
            }
      });
}
public void addResumeListener(EventListener listener){
      btnResume.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                  listener.handle(event);
            }
      });
}
/**
 * Consumer feed the index of item
 * */
public void addResolutionListener(Consumer<Integer> listener){
      selectResolution.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                  listener.accept(selectResolution.getSelectedIndex());
            }
      });

}
public void dispose(){
      background.dispose();
}
}
