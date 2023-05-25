package com.baller.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Globals {
    public static Skin GAME_SKIN;
    public enum Color {Red, Blue, Green}
    public static String[] SCREEN_RESOLUTIONS = {"Minimal", "Broad", "Full Screen"};
    public static int CURR_SCREEN_INDEX = 0;
    public static int CURR_MODE_INDEX = 0;
    public static String PLAYER_NAME = "John";
    public static float CURR_LUCKY_LEVEL = 0.5f;
    public static Vector2 DEFAULT_BALL_POS = new Vector2(280, 80f);
    public static Vector2 DEFAULT_BALL_VELOCITY = new Vector2(-80f, 140f);
    public static int BALL_SIZE = 40;
    public static Vector2 DEFAULT_TRAMPOLINE_VELOCITY = new Vector2(50f, 0f);
    public static Vector2 DEFAULT_TRAMPOLINE_POS= new Vector2(100f, 40f);
    public static int TRAMPOLINE_WIDTH = 120;
    public static int TRAMPOLINE_HEIGHT = 30;
    //object size is only dimension
    //used for field initialization
    public static int OBJECT_WIDTH = 100;
    public static int OBJECT_HEIGHT = 50;
    public static int FIELD_WIDTH = 640;
    public static int FIELD_HEIGHT = 480;
    public static float FIELD_RATIO = 0.3f; //what is the part of whole window field holds
    public static int TABLE_WIDTH = FIELD_WIDTH;
    public static int TABLE_HEIGHT = (int) (FIELD_HEIGHT * FIELD_RATIO);
    public static int RED_ZONE = FIELD_HEIGHT - TABLE_HEIGHT;
    public static float WINDOW_WIDTH = Gdx.graphics.getWidth();
    public static float WINDOW_HEIGHT = Gdx.graphics.getHeight();
    public static float convertHeight(int height){
        return (float) WINDOW_HEIGHT / FIELD_HEIGHT * height;
    }
    public static int convertHeight(float height){
        return (int) (height / WINDOW_HEIGHT * FIELD_HEIGHT);
    }
    public static float convertWidth(int width){
        return (float) WINDOW_WIDTH / FIELD_WIDTH * width;
    }
    static {
        System.out.println(Gdx.graphics.getWidth());
        System.out.println(Gdx.graphics.getHeight());
    }
    public static int convertWidth(float width){
        return (int) (width / WINDOW_WIDTH * FIELD_WIDTH);
    }
    public static String getTexturePath(Color color){
        return "badlogic.png";
    }


}
