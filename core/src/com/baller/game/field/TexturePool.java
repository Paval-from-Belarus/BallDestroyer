package com.baller.game.field;

import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;
import java.util.Map;

public class TexturePool {
private Texture textTrampoline;
private Map<BrickBlock.Type, Texture> textBlocks;
private Texture textField;
private Texture textBorders;
TexturePool(){
      textTrampoline = new Texture("block.png");
      textField = new Texture("fieldBGD.png");
      initBlocks();
}
private void initBlocks(){
      textBlocks = new HashMap<>();
      for(var type : BrickBlock.Type.values()){
	    String path;
        switch (type){
	      case DamageBonus -> path = "poison.png";
	      case Killer -> path = "horseshoe.png";
	      case MultiTrampoline -> path = "bottle.png";
	      default -> path = "block.png";
	}
	textBlocks.put(type, new Texture(path));
      }
}
public Texture getTrampoline(){
	return textTrampoline;
}
public Texture getBlock(BrickBlock.Type type){
      return textBlocks.get(type);
}
public Texture getFieldBGD(){
      return textField;
}
public Texture getFieldBorders(){
      return textBorders;
}
public void dispose(){
	textBlocks.forEach((key, value)->value.dispose());
	textTrampoline.dispose();
}
}
