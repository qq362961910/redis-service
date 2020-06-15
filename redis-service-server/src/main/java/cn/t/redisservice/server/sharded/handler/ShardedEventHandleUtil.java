package cn.t.redisservice.server.sharded.handler;

import cn.t.redisservice.server.ShardedRedisServer;
import cn.t.redisservice.server.sharded.event.NodeAddedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author yj
 * @since 2020-06-11 20:33
 **/
public class ShardedEventHandleUtil {

    private static final Logger logger = LoggerFactory.getLogger(ShardedEventHandleUtil.class);

    public static void handleEvent(NodeAddedEvent nodeAddedEvent, ShardedRedisServer server) {
        ShardedRedisServer newShardedRedisServer = nodeAddedEvent.getSourceServer();
        logger.info("当前节点: {}, 处理【节点新增】事件, 新增节点: {}", server.getId(), newShardedRedisServer.getId());
        Map.Entry<Integer, Integer> entry = server.getHashRangeServerIdMap().higherEntry(newShardedRedisServer.getId());
        //处于同一片数据集，则进行数据拆分
        if(entry != null && server.getId() == entry.getValue()) {

        }
    }
}
