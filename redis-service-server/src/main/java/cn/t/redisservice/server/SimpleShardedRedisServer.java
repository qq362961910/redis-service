package cn.t.redisservice.server;

import cn.t.redisservice.server.sharded.event.NodeAddedEvent;
import cn.t.redisservice.server.sharded.event.ShardedEvent;
import cn.t.redisservice.server.sharded.handler.EventHandleUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yj
 * @since 2020-06-11 19:12
 **/
public class SimpleShardedRedisServer extends ShardedRedisServer {

    private final Map<String, String> database = new HashMap<>();

    @Override
    public String get(String key) {
        if(!belongsToMe(key)) {
            return null;
        }
        return database.get(key);
    }

    @Override
    public void set(String key, String value) {
        if(!belongsToMe(key)) {
            return;
        }
        database.put(key, value);
    }

    @Override
    public void remove(String key) {
        if(!belongsToMe(key)) {
            return;
        }
        database.remove(key);
    }

    @Override
    public void onEvent(ShardedEvent shardedEvent) {
        if(shardedEvent instanceof NodeAddedEvent) {
            EventHandleUtil.handleEvent((NodeAddedEvent)shardedEvent, this);
        } else {
            throw new RuntimeException("未处理的事件类型");
        }
    }

    public SimpleShardedRedisServer(int index, int totalServerCount) {
        super(index, totalServerCount);
    }
}
