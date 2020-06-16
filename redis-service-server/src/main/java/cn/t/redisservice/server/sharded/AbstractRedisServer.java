package cn.t.redisservice.server.sharded;

import cn.t.redisservice.common.RedisServer;

/**
 * @author yj
 * @since 2020-06-12 16:50
 **/
public abstract class AbstractRedisServer implements RedisServer {

    protected final int id;

    @Override
    public int getId() {
        return id;
    }

    public AbstractRedisServer(int id) {
        this.id = id;
    }
}
