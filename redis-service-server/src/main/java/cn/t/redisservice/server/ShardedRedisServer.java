package cn.t.redisservice.server;

import cn.t.redisservice.common.util.KeyUtil;
import cn.t.redisservice.server.sharded.AbstractRedisServer;
import cn.t.redisservice.server.sharded.event.ShardedEvent;
import cn.t.util.common.CollectionUtil;

import java.util.TreeMap;


/**
 * @author yj
 * @since 2020-06-11 19:27
 **/
public abstract class ShardedRedisServer extends AbstractRedisServer {

    protected final int hashEnd;
    //hashRange -> server map
    protected final TreeMap<Integer, ShardedRedisServer> hashRangeServerMap = new TreeMap<>();

    public void initializeCluster(TreeMap<Integer, ShardedRedisServer> hashRangeServerMap) {
        if(!CollectionUtil.isEmpty(hashRangeServerMap)) {
            this.hashRangeServerMap.putAll(hashRangeServerMap);
        }
    }

    public abstract void onEvent(ShardedEvent shardedEvent);

    protected boolean belongsToMe(String key) {
        int hash = KeyUtil.hashKey(key);
        return hash < hashEnd;
    }

    public TreeMap<Integer, ShardedRedisServer> getHashRangeServerMap() {
        return hashRangeServerMap;
    }


    public int getHashEnd() {
        return hashEnd;
    }

    public ShardedRedisServer(int id, int hashEnd, TreeMap<Integer, ShardedRedisServer> hashRangeServerMap) {
        super(id);
        this.hashEnd = hashEnd;
        if(!CollectionUtil.isEmpty(hashRangeServerMap)) {
            this.hashRangeServerMap.putAll(hashRangeServerMap);
        }
    }

    @Override
    public String toString() {
        return String.format("sharded-redis-server: id: %d, hashEnd: %d, size: %d", id, hashEnd, size());
    }
}
