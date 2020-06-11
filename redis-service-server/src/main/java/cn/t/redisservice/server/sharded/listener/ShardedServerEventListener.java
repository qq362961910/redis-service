package cn.t.redisservice.server.sharded.listener;

import cn.t.redisservice.server.sharded.event.ShardedEvent;

/**
 * @author yj
 * @since 2020-06-11 20:29
 **/
public interface ShardedServerEventListener {
    void onEvent(ShardedEvent shardedEvent);
}
