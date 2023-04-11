package com.baller.game.field;

import com.baller.game.players.Player;

public class PlayerTrampolines {
private final Player.Id id;
private int trampolineCnt;
PlayerTrampolines(Player.Id player, int trampolineCnt){
      this.id = player;
      this.trampolineCnt = trampolineCnt;
}
public int getTrampolineCnt() {
      return trampolineCnt;
}

public void setTrampolineCnt(int trampolineCnt) {
      this.trampolineCnt = trampolineCnt;
}
@Override
public boolean equals(Object other){
      if(other instanceof PlayerTrampolines)
	    return ((PlayerTrampolines) other).id.equals(this.id);
      return false;
}
@Override
public String toString(){
      return String.valueOf(trampolineCnt);
}
}
