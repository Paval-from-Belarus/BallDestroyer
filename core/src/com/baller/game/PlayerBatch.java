package com.baller.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

public class PlayerBatch {
    private ArrayList<Player> players;
    private int nextIndex;
    PlayerBatch(){
        players = new ArrayList<>();
        nextIndex = 0;
    }
    public Player.Id[] add(int playerCnt){
        Player.Id[] array = new Player.Id[playerCnt];
        for(int i = 0; i < array.length; i++){
            array[i] = add();
        }
        return array;
    }
    public Player.Id add(){
        Player.Id id = new Player.Id(nextIndex++);
        Player player = new Player(id);
        players.add(player);
        return id;
    }
    private boolean isCorrectId(Player.Id id){
        return (id != null && id.value < players.size() && players.get(id.value) != null);
    }
    public void block(Player.Id id){
        if(isCorrectId(id)){
            players.get(id.value).setState(Player.State.Blocked);
//            Player founded = null;
//            for(Player player : players){
//                if(player != null && player.getId().equals(id)){
//                    founded = player;
//                    break;
//                }
//            }
//            if(founded != null){
//                founded.setState(false);
//            }
        }
    }
    public void release(Player.Id id){
        if(isCorrectId(id))
            players.get(id.value).setState(Player.State.Alive);
    }
    public void remove(Player.Id id){
        if(isCorrectId(id)){
            Player player = players.get(id.value);
            player.setState(Player.State.Blocked);
            player.dispose();
            players.set(id.value, null);
        }
    }
    public Player[] toArray(){
        return players.toArray(new Player[0]);
    }
    public void removeAll(){
        for (Player player : players) {
            if (player != null)
                remove(player.getId());
        }
    }
    public void draw(SpriteBatch batch){
        for(Player player : players){
            if(player != null){
                player.draw(batch);
            }
        }
    }
    public void update(float dt){
        for (Player player : players){
            player.update(dt);
        }
    }
}
