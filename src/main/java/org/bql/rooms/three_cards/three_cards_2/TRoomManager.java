package org.bql.rooms.three_cards.three_cards_2;

import org.bql.error.AppErrorCode;
import org.bql.error.GenaryAppError;
import org.bql.rooms.RoomAbs;
import org.bql.rooms.three_cards.three_cards_1.manage.FirstPlayerRoom;
import org.bql.rooms.three_cards.three_cards_1.manage.FirstRooms;
import org.bql.rooms.three_cards.three_cards_1.manage.MyPlayerSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TRoomManager {
    private  final Object lock = new Object();
    private  Map<Integer,FirstRooms> roomsMap = new ConcurrentHashMap<>();
    private static TRoomManager instance;
    private AtomicInteger roomId;
    public static TRoomManager getInstance() {
        if(instance == null)
            instance = new TRoomManager();
        return instance;
    }

    private TRoomManager() {
        roomId = new AtomicInteger(0);
    }

    private  FirstRooms createRoom(FirstPlayerRoom player){
        int id = roomId.incrementAndGet();
        if(id > 999999999){
            roomId.set(0);
        }
        FirstRooms r = new FirstRooms(player.getPlayer().getScenesId(),id,player.getPlayer().getAccount());
        roomsMap.put(id,r);
        return r;
    }
    public  RoomAbs hasRoom(int roomId){
        return roomsMap.getOrDefault(roomId,null);
    }

    public  RoomAbs intoRoom(FirstPlayerRoom p){
        synchronized (lock) {
            FirstRooms r = enterRoom(p);
            if (r == null) {
                r = createRoom(p);
                r.enterRoom(p);
            }
            return r;
        }
    }
    private FirstRooms enterRoom(FirstPlayerRoom p){
        for (Map.Entry<Integer,FirstRooms> e:roomsMap.entrySet()){
            FirstRooms r = e.getValue();
            if(r.getPlayerNum() < MyPlayerSet.MAX_SIZE){
                r.enterRoom(p);
                return r;
            }
        }
        return null;
    }
    /**
     * 交换房间
     * @param playerRoom
     * @return
     */
    public RoomAbs changeRoom(FirstPlayerRoom playerRoom){
        int roomId = playerRoom.getPlayer().getRoomId();
        for(Map.Entry<Integer,FirstRooms> e:roomsMap.entrySet()){
            FirstRooms r = e.getValue();
            if(e.getKey() != roomId && r.getPlayerNum() < MyPlayerSet.MAX_SIZE){
                r.enterRoom(playerRoom);
                return r;
            }
        }
        return intoRoom(playerRoom);
    }
    public void remove(int roomId){
        roomsMap.remove(roomId);
    }
    public List<FirstRooms> getAllRoom(){
        return new ArrayList<>(roomsMap.values());
    }
}
