package cn.t.redisservice.server;

import cn.t.redisservice.common.util.KeyUtil;
import cn.t.redisservice.server.sharded.event.NodeAddedEvent;
import cn.t.redisservice.server.sharded.event.ShardedEvent;
import cn.t.redisservice.server.sharded.handler.EventHandleUtil;
import cn.t.redisservice.server.sharded.listener.ShardedServerEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yj
 * @since 2020-06-11 19:12
 **/
public class SimpleShardedRedisServer implements RedisServer, ShardedServerEventListener {

    private final Map<String, String> database = new HashMap<>();
    private final int index;
    private int totalServerCount;

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
    public int getIndex() {
        return index;
    }

    @Override
    public void onEvent(ShardedEvent shardedEvent) {
        if(shardedEvent instanceof NodeAddedEvent) {
            EventHandleUtil.handleEvent((NodeAddedEvent)shardedEvent, this);
        } else {
            throw new RuntimeException("未处理的事件类型");
        }
    }

    private boolean belongsToMe(String key) {
        return KeyUtil.calculateShardedServerIndex(key, totalServerCount) == index;
    }

    public void setTotalServerCount(int totalServerCount) {
        this.totalServerCount = totalServerCount;
    }

    public SimpleShardedRedisServer(int index, int totalServerCount) {
        this.index = index;
        this.totalServerCount = totalServerCount;
    }
}
