package cn.t.redisservice.server.sharded.handler;

import cn.t.redisservice.common.util.KeyUtil;
import cn.t.redisservice.server.ShardedRedisServer;
import cn.t.redisservice.server.sharded.event.NodeAddedEvent;
import cn.t.redisservice.server.sharded.event.NodeRemovedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author yj
 * @since 2020-06-11 20:33
 **/
public class ShardedEventHandleUtil {

    private static final Logger logger = LoggerFactory.getLogger(ShardedEventHandleUtil.class);

    public static void handleEvent(NodeAddedEvent nodeAddedEvent, ShardedRedisServer server) {
        ShardedRedisServer eventSourceServer = nodeAddedEvent.getSourceServer();
        logger.info("当前节点: {}, 处理【节点新增】事件, 新增节点: {}", server.getId(), eventSourceServer.getId());
        TreeMap<Integer, Integer> hashRangeServerIdMap = server.getHashRangeServerIdMap();
        hashRangeServerIdMap.put(nodeAddedEvent.getHashEnd(), eventSourceServer.getId());
        Map.Entry<Integer, Integer> entry = hashRangeServerIdMap.higherEntry(eventSourceServer.getId());
        //处于同一片数据集，则进行数据拆分
        if(entry != null && server.getId() == entry.getValue()) {
            if(server.size() > 0) {
                logger.info("新增节点, server: [{}]数据即将开始re-balance, target server: [{}], 数据总量: {}", server.getId(), eventSourceServer.getId(), server.size());
                for(Map.Entry<String, String> e: server.dump()) {
                    int hash = KeyUtil.hashKey(e.getKey());
                    if(hash < eventSourceServer.getId()) {
                        eventSourceServer.set(e.getKey(), e.getValue());
                        server.remove(e.getKey());
                    }
                }
            }
            eventSourceServer.getHashRangeServerIdMap().putAll(hashRangeServerIdMap);
        }
    }
    public static void handleEvent(NodeRemovedEvent nodeRemovedEvent, ShardedRedisServer server) {
        ShardedRedisServer eventSourceServer = nodeRemovedEvent.getSourceServer();
        logger.info("当前节点: {}, 处理【节点移除】事件, 移除节点: {}", server.getId(), eventSourceServer.getId());
        TreeMap<Integer, Integer> hashRangeServerIdMap = server.getHashRangeServerIdMap();
        hashRangeServerIdMap.remove(eventSourceServer.getHashEnd());
        if(eventSourceServer.size() > 0) {
            Map.Entry<Integer, Integer> entry = hashRangeServerIdMap.higherEntry(eventSourceServer.getId());
            //数据向后迁移
            if(entry != null) {
                logger.info("移除节点, server: [{}]数据即将开始向后迁移, target server: [{}], 数据总量: {}", eventSourceServer.getId(), server.getId(), eventSourceServer.size());
                server.addAll(eventSourceServer.dump());
            }
        }
    }
}
