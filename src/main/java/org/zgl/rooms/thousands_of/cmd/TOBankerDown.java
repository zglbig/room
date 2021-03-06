package org.zgl.rooms.thousands_of.cmd;

import org.zgl.net.server.manage.OperateCommandAbstract;
import org.zgl.rooms.thousands_of.model.TOPlayer;
import org.zgl.rooms.thousands_of.model.TORoom;
import org.zgl.utils.builder_clazz.ann.Protocol;

/**
 * @作者： big
 * @创建时间： 2018/5/23
 * @文件描述：
 */
@Protocol("1016")
public class TOBankerDown extends OperateCommandAbstract {
    @Override
    public Object execute() {
        TOPlayer player = (TOPlayer) getSession().getAttachment();
        TORoom room = (TORoom) player.getRoom();
        room.getPlayerSet().bankerExit(player.getPlayer().getAccount());
        return null;
    }
}
