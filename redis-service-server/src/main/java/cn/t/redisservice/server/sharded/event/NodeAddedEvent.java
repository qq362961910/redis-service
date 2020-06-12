package cn.t.redisservice.server.sharded.event;

import cn.t.redisservice.server.ShardedRedisServer;

/**
 * @author yj
 * @since 2020-06-11 19:29
 **/
public class NodeAddedEvent implements ShardedEvent {

    private final int nextServerIndex;
    private final int totalServerCount;
    private final ShardedRedisServer shardedRedisServer;

    public int getNextServerIndex() {
        return nextServerIndex;
    }

    public int getTotalServerCount() {
        return totalServerCount;
    }

    @Override
    public ShardedRedisServer getSourceServer() {
        return shardedRedisServer;
    }

    public NodeAddedEvent(int nextServerIndex, int totalServerCount, ShardedRedisServer shardedRedisServer) {
        this.nextServerIndex = nextServerIndex;
        this.totalServerCount = totalServerCount;
        this.shardedRedisServer = shardedRedisServer;
    }
}
