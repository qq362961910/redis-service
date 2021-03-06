package cn.t.redisservice.server;

import cn.t.redisservice.common.util.KeyUtil;
import cn.t.redisservice.server.sharded.event.NodeAddedEvent;
import cn.t.redisservice.server.sharded.event.NodeRemovedEvent;
import cn.t.redisservice.server.sharded.event.ShardedEvent;
import cn.t.redisservice.server.sharded.handler.ShardedEventHandleUtil;
import cn.t.util.common.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author yj
 * @since 2020-06-11 19:12
 **/
public class SimpleShardedRedisServer extends ShardedRedisServer {

    private static final Logger logger = LoggerFactory.getLogger(SimpleShardedRedisServer.class);

    private final Map<String, String> database = new HashMap<>();

    @Override
    public String get(String key) {
        if(belongsToMe(key)) {
            return database.get(key);
        }
        return null;
    }

    @Override
    public void set(String key, String value) {
        if(belongsToMe(key)) {
//            logger.info("set {}, keyHash: {}, serverId: {}, hashEnd: {}", key, KeyUtil.hashKey(key), getId(), hashEnd);
            database.put(key, value);
        }
    }

    @Override
    public void remove(String key) {
        if(belongsToMe(key)) {
            database.remove(key);
        }
    }

    @Override
    public int size() {
        return database.size();
    }

    @Override
    public Set<String> allKeys() {
        return database.keySet();
    }

    @Override
    public Set<Map.Entry<String, String>> dump() {
        return database.entrySet();
    }

    @Override
    public void addAll(Set<Map.Entry<String, String>> set) {
        if(!CollectionUtil.isEmpty(set)) {
            for(Map.Entry<String, String> entry: set) {
                database.put(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public void onEvent(ShardedEvent shardedEvent) {
        if(shardedEvent instanceof NodeAddedEvent) {
            ShardedEventHandleUtil.handleEvent((NodeAddedEvent)shardedEvent, this);
        } else if(shardedEvent instanceof NodeRemovedEvent) {
            ShardedEventHandleUtil.handleEvent((NodeRemovedEvent)shardedEvent, this);
        } else {
            throw new RuntimeException("未处理的事件类型");
        }
    }

    public SimpleShardedRedisServer(int id, int hashEnd) {
        super(id, hashEnd, null);
    }

    public SimpleShardedRedisServer(int id, int hashEnd, TreeMap<Integer, ShardedRedisServer> hashRangeServerMap) {
        super(id, hashEnd, hashRangeServerMap);
    }
}
