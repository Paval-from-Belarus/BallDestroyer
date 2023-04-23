package com.baller.game.field;

import com.baller.game.players.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerTrampolines {
private final Player.Id id;
private Trampoline[] trampolines;
private int nextIndex;
PlayerTrampolines(Player.Id player, int capacity){
      this.id = player;
      this.trampolines = new Trampoline[capacity];
}
public int capacity(){
      return trampolines.length;
}
public void setCapacity(int capacity){
      Trampoline[] buffer = new Trampoline[capacity];
      int copyLength = Math.min(capacity(), capacity);
      System.arraycopy(trampolines, 0, buffer, 0, copyLength);
      this.trampolines = buffer;
}
public @NotNull List<Trampoline> list(){
      return Arrays.asList(trampolines);
}
public void clear(){
      nextIndex = 0;
      trampolines = new Trampoline[capacity()];
}
public boolean add(@NotNull Trampoline trampoline){
      boolean isAdded = false;
      if(nextIndex < trampolines.length){
            trampolines[nextIndex++] = trampoline;
            isAdded = true;
      }
      return isAdded;
}
public boolean belongs(Player.Id id){
      return this.id.equals(id);
}
@Override
public boolean equals(Object other){
      if(other instanceof PlayerTrampolines)
	    return ((PlayerTrampolines) other).id.equals(this.id);
      if(other instanceof Player.Id)
            return other.equals(this.id);
      return false;
}
}
