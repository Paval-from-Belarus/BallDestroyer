package com.baller.game.field;

import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;
import java.util.Map;

public class TexturePool {
private Texture textTrampoline;
private Map<BrickBlock.Type, Texture> textBonuses;
private Texture textBlock;
private Texture textField;
private Texture textBorders;
TexturePool(){
      textTrampoline = new Texture("block.png");
      textField = new Texture("fieldBGD.png");
      textBlock = new Texture("block.png");
      initBlocks();
}
private void initBlocks(){
      textBonuses = new HashMap<>();

      for(var type : BrickBlock.Type.values()){
	    String path;
        switch (type){
	      case DamageBonus -> path = "poison.png";
	      case Killer -> path = "horseshoe.png";
	      case MultiTrampoline -> path = "bottle.png";
	      case MultiBall -> path = "voody.png";
	      case Invincible -> path = "barrier.png";
	      default -> path = "block.png";
	}
	textBonuses.put(type, new Texture(path));
      }
}
public Texture getFlyerBonus(BrickBlock.Type type) {
      return textBonuses.get(type);
}
public Texture getBrick(BrickBlock.Type type) {
      return switch (type) {
	    case Invincible, Plain -> textBonuses.get(type);
	    default -> textBlock;
      };
}
public Texture getTrampoline(){
	return textTrampoline;
}
public Texture getFieldBGD(){
      return textField;
}
public Texture getFieldBorders(){
      return textBorders;
}
public void dispose(){
	textBonuses.forEach((key, value)->value.dispose());
	textTrampoline.dispose();
}
}
