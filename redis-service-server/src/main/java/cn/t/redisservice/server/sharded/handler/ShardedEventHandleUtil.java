package cn.t.redisservice.server.sharded.handler;

import cn.t.redisservice.server.ShardedRedisServer;
import cn.t.redisservice.server.sharded.event.NodeAddedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yj
 * @since 2020-06-11 20:33
 **/
public class ShardedEventHandleUtil {

    private static final Logger logger = LoggerFactory.getLogger(ShardedEventHandleUtil.class);

    public static void handleEvent(NodeAddedEvent nodeAddedEvent, ShardedRedisServer server) {
        ShardedRedisServer shardedRedisServer = nodeAddedEvent.getSourceServer();
        int totalServerCount = nodeAddedEvent.getTotalServerCount();
        logger.info("处理【节点新增】事件, server index: {}, total server count: {}", shardedRedisServer.getIndex(), totalServerCount);
        server.setTotalServerCount(nodeAddedEvent.getTotalServerCount());
        //处于同一片数据集，则进行数据拆分
        if(nodeAddedEvent.getNextServerIndex() == server.getIndex()) {

        }
    }
}
