package org.zgl.rooms.three_cards.three_cards_1.cmd;

import org.zgl.error.AppErrorCode;
import org.zgl.error.GenaryAppError;
import org.zgl.net.builder_clazz.NotifyCode;
import org.zgl.net.server.manage.OperateCommandAbstract;
import org.zgl.player.PlayerRoom;
import org.zgl.rooms.three_cards.three_cards_1.dto.CardsDto;
import org.zgl.rooms.three_cards.three_cards_1.dto.RoomPlayerAccountDto;
import org.zgl.rooms.three_cards.three_cards_1.manage.FirstPlayerRoom;
import org.zgl.rooms.three_cards.three_cards_1.manage.FirstRooms;
import org.zgl.rooms.three_cards.three_cards_1.manage.MyPlayerSet;
import org.zgl.rooms.three_cards.three_cards_1.model.HandCard;
import org.zgl.utils.builder_clazz.ann.Protocol;

import java.util.List;

/**
 * 玩家看牌
 */
@Protocol("1001")
public class FirstRoom_LookCard extends OperateCommandAbstract {
    private List<FirstPlayerRoom> players;
    private FirstRooms room;
    private String playerAccount;
    @Override
    public Object execute() {
        PlayerRoom player = (PlayerRoom) getSession().getAttachment();
        room = (FirstRooms) player.getRoom();

        playerAccount = player.getPlayer().getAccount();
        MyPlayerSet  playerSet = room.getPlayerSet();
        players = playerSet.getNotAccountPlayer(playerAccount);
        boolean isPlay = playerSet.isPlayForAccount(playerAccount);
        if(!isPlay)
            new GenaryAppError(AppErrorCode.SERVER_ERR);//这个玩家没有在玩
        FirstPlayerRoom playerRoom = playerSet.getPlayerForPosition(playerAccount);
        HandCard handCard = playerRoom.getHandCard();
        if(handCard == null)
            new GenaryAppError(AppErrorCode.SERVER_ERR);
        return new CardsDto().dto(handCard);
    }

    @Override
    public void broadcast() {
        //有人看牌
        room.broadcast(players,NotifyCode.ROOM_PLAYER_LOOK_CARD,new RoomPlayerAccountDto(playerAccount));
    }
}
