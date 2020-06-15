package cn.t.redisservice.server;

import cn.t.redisservice.server.sharded.event.NodeAddedEvent;
import cn.t.redisservice.server.sharded.event.ShardedEvent;
import cn.t.redisservice.server.sharded.handler.ShardedEventHandleUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author yj
 * @since 2020-06-11 19:12
 **/
public class SimpleShardedRedisServer extends ShardedRedisServer {

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
    public Set<String> allKeys() {
        return database.keySet();
    }

    @Override
    public Set<Map.Entry<String, String>> dump() {
        return database.entrySet();
    }

    @Override
    public void onEvent(ShardedEvent shardedEvent) {
        if(shardedEvent instanceof NodeAddedEvent) {
            ShardedEventHandleUtil.handleEvent((NodeAddedEvent)shardedEvent, this);
        } else {
            throw new RuntimeException("未处理的事件类型");
        }
    }

    public SimpleShardedRedisServer(int id, TreeMap<Integer, Integer> hashRangeServerIdMap) {
        super(id, hashRangeServerIdMap);
    }
}
