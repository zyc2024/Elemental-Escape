package com.elements.game;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.elements.game.controller.GDXRoot;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("Elemental Escape");
		config.setWindowedMode(800, 450);
		config.setResizable(true);
		if (arg.length > 0) {
			new Lwjgl3Application(new GDXRoot(arg[0]), config);
		}
		else {
			// standard, use this branch for shipping
			new Lwjgl3Application(new GDXRoot(), config);
		}
	}
}
