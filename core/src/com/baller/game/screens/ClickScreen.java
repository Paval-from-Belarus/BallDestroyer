package com.baller.game.screens;

import com.badlogic.gdx.Screen;
import com.baller.game.UserInterface;

import java.util.List;
import java.util.Map;

public interface ClickScreen extends Screen {
      List<UserInterface.UserClick> getAll();
      void acceptClicks (Map<UserInterface.UserClick.Id, UserInterface.UserClick> mapper);
}
