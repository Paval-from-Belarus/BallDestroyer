package com.baller.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class Game extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	PlayerBatch players;
	GameField field;
	private OrthographicCamera camera;

	@Override
	public void create () {
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Globals.WINDOW_WIDTH, Globals.WINDOW_HEIGHT);
		batch = new SpriteBatch();
		players = new PlayerBatch();
		players.add();
		field = new GameField();
	}

	@Override
	public void render () {
		ScreenUtils.clear(1, 0, 0, 1);
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		players.update(0.1f);
		field.update(0.1f);
		for(Player player : players.toArray()){
			Ball[] balls = player.getBalls();
			for(Ball ball : balls){
				GameField.Message msg = field.getMessage(ball);
				player.dispatch(msg, ball);

			}
		}

		batch.begin();
		field.draw(batch);
		players.draw(batch);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		field.dispose();
		players.removeAll();
	}
}
