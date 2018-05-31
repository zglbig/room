package org.bql.rooms.thousands_of.cmd;

import org.bql.net.server.manage.OperateCommandAbstract;
import org.bql.rooms.thousands_of.dto.HistoryDto;
import org.bql.rooms.thousands_of.dto.HistoryDtos;
import org.bql.rooms.thousands_of.model.TOPlayer;
import org.bql.rooms.thousands_of.model.TORoom;
import org.bql.utils.builder_clazz.ann.Protocol;

import java.util.List;


/**查看历史记录
 *
 */
@Protocol("1008")
public class TOHistoryOperation extends OperateCommandAbstract {
    @Override
    public Object execute() {
        TOPlayer player = (TOPlayer) getSession().getAttachment();
        TORoom room = (TORoom) player.getRoom();
        List<HistoryDto> dtos = room.getGamblingParty().historyDtos();
        return new HistoryDtos(dtos);
    }
}
