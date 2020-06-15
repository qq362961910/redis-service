package cn.t.redisservice.server.sharded.event;

import cn.t.redisservice.server.ShardedRedisServer;

/**
 * @author yj
 * @since 2020-06-11 19:29
 **/
public class NodeRemovedEvent implements ShardedEvent {

    private final ShardedRedisServer shardedRedisServer;

    @Override
    public ShardedRedisServer getSourceServer() {
        return shardedRedisServer;
    }

    public NodeRemovedEvent(ShardedRedisServer shardedRedisServer) {
        this.shardedRedisServer = shardedRedisServer;
    }
}
