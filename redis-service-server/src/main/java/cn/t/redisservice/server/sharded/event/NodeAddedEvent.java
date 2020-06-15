package cn.t.redisservice.server.sharded.event;

import cn.t.redisservice.server.ShardedRedisServer;

/**
 * @author yj
 * @since 2020-06-11 19:29
 **/
public class NodeAddedEvent implements ShardedEvent {

    private final int hashEnd;
    private final ShardedRedisServer sourceServer;

    @Override
    public ShardedRedisServer getSourceServer() {
        return sourceServer;
    }

    public int getHashEnd() {
        return hashEnd;
    }

    public NodeAddedEvent(int hashEnd, ShardedRedisServer sourceServer) {
        this.hashEnd = hashEnd;
        this.sourceServer = sourceServer;
    }
}
