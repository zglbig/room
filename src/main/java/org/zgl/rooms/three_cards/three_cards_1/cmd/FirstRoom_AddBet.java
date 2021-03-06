package org.zgl.rooms.three_cards.three_cards_1.cmd;

import org.zgl.error.AppErrorCode;
import org.zgl.error.GenaryAppError;
import org.zgl.net.builder_clazz.NotifyCode;
import org.zgl.net.message.ServerResponse;
import org.zgl.net.server.manage.OperateCommandAbstract;
import org.zgl.player.PlayerInfoDto;
import org.zgl.rooms.three_cards.three_cards_1.dto.RoomAddBetDto;
import org.zgl.rooms.three_cards.three_cards_1.dto.SettleModelDto;
import org.zgl.rooms.three_cards.three_cards_1.manage.FirstGamblingParty;
import org.zgl.rooms.three_cards.three_cards_1.manage.FirstPlayerRoom;
import org.zgl.rooms.three_cards.three_cards_1.manage.FirstRooms;
import org.zgl.rooms.three_cards.three_cards_1.manage.MyPlayerSet;
import org.zgl.rooms.type.ScenesType;
import org.zgl.utils.builder_clazz.ann.Protocol;

import java.util.concurrent.atomic.AtomicInteger;

@Protocol("1009")
public class FirstRoom_AddBet extends OperateCommandAbstract {
    /**加注筹码位置*/
    private final int addBetChipPosition;

    public FirstRoom_AddBet(int addBetChipPosition) {
        this.addBetChipPosition = addBetChipPosition;
    }
    private String account;
    private String nextAccount;
    private FirstRooms room;
    private long moneyChange;
    private PlayerInfoDto p;
    @Override
    public Object execute() {
        FirstPlayerRoom player = (FirstPlayerRoom) getSession().getAttachment();
        p = player.getPlayer();
        room = (FirstRooms) player.getRoom();
        MyPlayerSet playerSet = room.getPlayerSet();
        account = p.getAccount();
        boolean isPlay = playerSet.isPlayForAccount(account);
        if(!isPlay)
            new GenaryAppError(AppErrorCode.DATA_ERR);//这个玩家没有在玩
        FirstGamblingParty gamblingParty = room.getGamblingParty();
        int nowBottomChip = gamblingParty.getNowBottomChip().get();
        if(nowBottomChip > addBetChipPosition)
            new GenaryAppError(AppErrorCode.BET_MIN_ERR);
        if(gamblingParty.isBottomTimeOut())
            new GenaryAppError(AppErrorCode.BET_TIME_OUT);//下注超时
        if(gamblingParty.getNowBottomPos().get() != p.getRoomPosition()) {
            new GenaryAppError(AppErrorCode.NOT_IS_YOU_BET);//不是该玩家下注
        }
        if(gamblingParty.getBetState()){
            new GenaryAppError(AppErrorCode.HASH_PLAYER_BET_ALL);
        }
        int scenesId = p.getScenesId();
        int[] bottom = ScenesType.get(scenesId).chip();
        if(bottom == null)
            new GenaryAppError(AppErrorCode.DATA_ERR);
        if(addBetChipPosition >= bottom.length)
            new GenaryAppError(AppErrorCode.DATA_ERR);
        moneyChange = bottom[addBetChipPosition];
        //看牌后倍数*2
        if(player.isHasLookCard())
            moneyChange *= gamblingParty.MULTIPLE;
        if(!p.reduceGold(moneyChange))
            new GenaryAppError(AppErrorCode.GOLD_NOT);//金币不足
        gamblingParty.setStartTime(System.currentTimeMillis());
        gamblingParty.setNowBottomPos(new AtomicInteger(addBetChipPosition));//设置当前牌局底注
        room.bottom(account,moneyChange);//更新房间金币
        //下一玩家下注位置
        nextAccount = playerSet.getNextPositionAccount(p.getRoomPosition());
        gamblingParty.addOparetionCount();
        return new SettleModelDto(account,p.getGold(),room.getAllMoneyNum());
    }

    /**
     * 通知所有玩家改用户财富变更
     */
    @Override
    public void broadcast() {
        FirstPlayerRoom nextPlayer = room.getPlayerSet().getPlayerForPosition(nextAccount);
        room.getGamblingParty().setNowBottomPos(new AtomicInteger(nextPlayer.getPlayer().getRoomPosition()));
        //通知下一个下注玩家
        nextPlayer.getSession().write(new ServerResponse(NotifyCode.ROOM_PLAYER_BOTTOM,null));
        RoomAddBetDto dto = new RoomAddBetDto(account,nextAccount,moneyChange,addBetChipPosition,p.getGold(),room.getAllMoneyNum());
        //通知所有玩家财富变更，下一玩家下注
        room.broadcast(room.getPlayerSet().getNotAccountPlayer(account),NotifyCode.ROOM_ADD_BET,dto);
    }
}
