package com.baller.game.uicomponents;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.baller.game.Globals;
import com.baller.game.UserInterface;
import com.sun.source.tree.LambdaExpressionTree;

import java.util.function.Consumer;

import static com.baller.game.UserInterface.*;

public class MessageBox extends UIComponent implements Disposable {
private final int BTN_RESTART_SIZE = 80;
private final int DEFAULT_WIDTH = (int) (Globals.FIELD_WIDTH * 0.6f);
private final int DEFAULT_HEIGHT = (int) (DEFAULT_WIDTH / 1.5f);
private final Texture background = new Texture("message_background.png");
private final Texture textCircle = new Texture("retry.png");
private final Button btnRestart;
private final Label infoArea;
private final Label title;
private final Skin skin;
public MessageBox(Skin skin) {
      super();
      setTexture(background);
      setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
      setPos(Globals.FIELD_WIDTH >> 1, Globals.FIELD_HEIGHT >> 1);
      this.skin = skin;
      btnRestart = new ImageButton(new SpriteDrawable(new Sprite(textCircle)));
      title = new Label("TITLE", skin);
      title.setAlignment(Align.center);
      infoArea = new Label("fsdfjladfjla;kfja;fdja;fja;", skin);
      infoArea.setAlignment(Align.center);
      setBtnRestart();
      setTitle();
      setInfoArea();
}
private void setTitle(){
      title.setPosition(
          Globals.convertWidth(getPos().x - title.getWidth() / 2),
          Globals.convertHeight(getPos().y + (getHeight() / 2 - 40))
      );
      title.setFontScale(3f);
      addActor(title);
}
private void setInfoArea(){
      infoArea.setPosition(
          Globals.convertWidth(getPos().x + (int) (getWidth() / 3.5f) - 80 ),
          Globals.convertHeight(getPos().y)
      );
      infoArea.setFontScale(2f);
      addActor(infoArea);
}
private void setBtnRestart(){
      final int BTN_SIZE = BTN_RESTART_SIZE;
      btnRestart.setSize(
          Globals.convertWidth(BTN_SIZE),
          Globals.convertHeight(BTN_SIZE)
      );
      btnRestart.setPosition(
          Globals.convertWidth(getPos().x - (int) (getWidth() / 2.2f) ),
          Globals.convertHeight(getPos().y - BTN_SIZE / 2)
      );
      addActor(btnRestart);
}
public void rebuild(MessageInfo info) {
      title.setText(info.title);
      infoArea.setText(info.text);
}
public void onMessageInfo(RocksHandler rocks) {
      rocks.accept(ms -> rebuild((MessageInfo)ms));
}
public void addRestartListener(EventListener listener){
      btnRestart.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                  listener.handle(event);
            }
      });
}
@Override
public void draw(SpriteBatch batch){
      super.draw(batch);
      if (isVisible())
            btnRestart.draw(batch, 1);
}
@Override
public void dispose() {
      background.dispose();
      textCircle.dispose();
}
}
