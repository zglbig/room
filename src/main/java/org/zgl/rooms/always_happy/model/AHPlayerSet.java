package org.zgl.rooms.always_happy.model;

import org.zgl.player.PlayerRoom;

import java.util.ArrayList;
import java.util.List;

public class AHPlayerSet {
    private List<PlayerRoom> players;
    private final AHRoom room;

    public AHPlayerSet(AHRoom room) {
        this.room = room;
        this.players = new ArrayList<>();
    }
    public void enter(PlayerRoom player){
        if(!players.contains(player)){
            players.add(player);
        }
    }
    public void exit(PlayerRoom player){
        if(players.contains(player))
            players.remove(player);
    }

    public List<PlayerRoom> getPlayers() {
        return players;
    }
    public List<PlayerRoom> getPlayersNotPlayer(PlayerRoom player){
        ArrayList<PlayerRoom> ahPlayers = new ArrayList<>(players);
        ahPlayers.remove(player);
        return ahPlayers;
    }
    public int playerNum(){
        return players.size();
    }
    public void end(){
        for (PlayerRoom p:players){
            if(!p.getSession().isConnected()){
                players.remove(p);
            }
        }
    }
}
