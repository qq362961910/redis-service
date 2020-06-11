package cn.t.redisservice.server.sharded.event;

import cn.t.redisservice.server.ShardedRedisServer;

public interface ShardedEvent {
    ShardedRedisServer getSourceServer();
}
