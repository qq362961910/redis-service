package cn.t.redisservice.server.sharded.event;

import cn.t.redisservice.server.ShardedRedisServer;

/**
 * @author yj
 * @since 2020-06-11 19:29
 **/
public class NodeAddedEvent implements ShardedEvent {

    private final int totalServerCount;
    private final ShardedRedisServer shardedRedisServer;

    @Override
    public ShardedRedisServer getSourceServer() {
        return shardedRedisServer;
    }

    public int getTotalServerCount() {
        return totalServerCount;
    }

    public NodeAddedEvent(int totalServerCount, ShardedRedisServer shardedRedisServer) {
        this.totalServerCount = totalServerCount;
        this.shardedRedisServer = shardedRedisServer;
    }
}
