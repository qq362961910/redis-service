package cn.t.redisservice.server;

import cn.t.redisservice.common.util.KeyUtil;
import cn.t.redisservice.server.sharded.event.ShardedEvent;

/**
 * @author yj
 * @since 2020-06-11 19:27
 **/
public abstract class ShardedRedisServer implements RedisServer {

    private final int index;
    private int totalServerCount;

    public abstract void onEvent(ShardedEvent shardedEvent);

    @Override
    public int getIndex() {
        return index;
    }

    protected boolean belongsToMe(String key) {
        return KeyUtil.calculateShardedServerIndex(key, totalServerCount) == index;
    }

    public void setTotalServerCount(int totalServerCount) {
        this.totalServerCount = totalServerCount;
    }

    public ShardedRedisServer(int index, int totalServerCount) {
        this.index = index;
        this.totalServerCount = totalServerCount;
    }
}
