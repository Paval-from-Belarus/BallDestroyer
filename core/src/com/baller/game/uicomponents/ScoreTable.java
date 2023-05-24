package com.baller.game.uicomponents;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Disposable;
import com.baller.game.Globals;
import com.baller.game.UserInterface;
import org.javatuples.Pair;

import java.util.List;

import static com.baller.game.UserInterface.*;

public class ScoreTable extends UIComponent implements Disposable {
private static final int FIRST_COLUMN_WIDTH = 100;
private static final int SECOND_COLUMN_WIDTH = 200;
private final Skin skin;
private final Table table;
private final Label lblName;
private final Label lblScore;
private final Texture background = new Texture("block.png");
public ScoreTable(Skin skin) {
	super();
	this.skin = skin;
	setTexture(background);
	setSize(Globals.FIELD_WIDTH, Globals.FIELD_HEIGHT);
	setPos(Globals.FIELD_WIDTH / 2, Globals.FIELD_HEIGHT / 2);
	table  = new Table();
	lblName = new Label("Player", skin);
	lblScore = new Label("Score", skin);
	table.setPosition(
	    Globals.convertWidth(Globals.WINDOW_WIDTH / 2),
	    Globals.convertHeight(Globals.WINDOW_HEIGHT / 2)
	);
	setTableHeader();
	table.setDebug(true);
	addActor(table);
}
private void setTableHeader() {
      table.add(lblName).width(FIRST_COLUMN_WIDTH);
      table.add(lblScore).width(SECOND_COLUMN_WIDTH);
      table.row();
}
private void setTable(List<Pair<String, Integer>> statistics) {
      table.clear();
      setTableHeader();
      for (var single : statistics) {
	    TextField name = new TextField(single.getValue0(), skin);
	    TextField score = new TextField(String.valueOf(single.getValue1()), skin);
	    table.add(name).width(FIRST_COLUMN_WIDTH);
	    table.add(score).width(SECOND_COLUMN_WIDTH);
	    table.row();
      }
}
public void onStatisticsChanged(RocksHandler rocks) {
      rocks.accept(list -> setTable((List<Pair<String, Integer>>)list));
}

@Override
public void dispose() {
      background.dispose();
}

}
