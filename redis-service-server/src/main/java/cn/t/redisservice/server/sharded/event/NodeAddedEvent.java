package cn.t.redisservice.server.sharded.event;

import cn.t.redisservice.server.ShardedRedisServer;

/**
 * @author yj
 * @since 2020-06-11 19:29
 **/
public class NodeAddedEvent implements ShardedEvent {

    private final ShardedRedisServer sourceServer;

    @Override
    public ShardedRedisServer getSourceServer() {
        return sourceServer;
    }

    public NodeAddedEvent(ShardedRedisServer sourceServer) {
        this.sourceServer = sourceServer;
    }
}
