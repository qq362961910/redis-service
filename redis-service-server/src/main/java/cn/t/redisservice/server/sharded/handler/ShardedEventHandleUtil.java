package cn.t.redisservice.server.sharded.handler;

import cn.t.redisservice.common.util.KeyUtil;
import cn.t.redisservice.server.ShardedRedisServer;
import cn.t.redisservice.server.sharded.event.NodeAddedEvent;
import cn.t.redisservice.server.sharded.event.NodeRemovedEvent;
import cn.t.util.common.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author yj
 * @since 2020-06-11 20:33
 **/
public class ShardedEventHandleUtil {

    private static final Logger logger = LoggerFactory.getLogger(ShardedEventHandleUtil.class);

    public static void handleEvent(NodeAddedEvent nodeAddedEvent, ShardedRedisServer server) {
        ShardedRedisServer newShardedRedisServer = nodeAddedEvent.getSourceServer();
        logger.info("当前节点: {}, 处理【节点新增】事件, 新增节点: {}", server.getId(), newShardedRedisServer.getId());
        TreeMap<Integer, Integer> hashRangeServerIdMap = server.getHashRangeServerIdMap();
        hashRangeServerIdMap.put(nodeAddedEvent.getHashEnd(), newShardedRedisServer.getId());
        Map.Entry<Integer, Integer> entry = hashRangeServerIdMap.higherEntry(newShardedRedisServer.getId());
        //处于同一片数据集，则进行数据拆分
        if(entry != null && server.getId() == entry.getValue()) {
            Set<String> keySet = server.allKeys();
            if(!CollectionUtil.isEmpty(keySet)) {
                logger.info("server: [{}]数据即将开始re-balance, target server: [{}], 数据总量: {}", server.getId(), newShardedRedisServer.getId(), keySet.size());
                for(String key: keySet) {
                    int hash = KeyUtil.hashKey(key);
                    if(hash < newShardedRedisServer.getId()) {
                        newShardedRedisServer.set(key, server.get(key));
                        server.remove(key);
                    }
                }
            }
        }

    }
    public static void handleEvent(NodeRemovedEvent nodeRemovedEvent, ShardedRedisServer server) {

    }
}
