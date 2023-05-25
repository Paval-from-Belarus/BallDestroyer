package com.baller.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
	      config.setTitle("ball-destroyer");
	      config.setResizable(false);
	      config.setDecorated(true);
	      config.setWindowSizeLimits(640, 480, 1280, 962);
	      new Lwjgl3Application(new Game(), config);

	}

}
