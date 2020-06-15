package cn.t.redisservice.server;

import cn.t.redisservice.common.util.KeyUtil;
import cn.t.redisservice.server.sharded.AbstractRedisServer;
import cn.t.redisservice.server.sharded.event.ShardedEvent;

import java.util.TreeMap;


/**
 * @author yj
 * @since 2020-06-11 19:27
 **/
public abstract class ShardedRedisServer extends AbstractRedisServer {

    private final int hashEnd;
    //hashRange -> serverId map
    private final TreeMap<Integer, Integer> hashRangeServerIdMap;

    public abstract void onEvent(ShardedEvent shardedEvent);

    protected boolean belongsToMe(String key) {
        int hash = KeyUtil.hashKey(key);
        return hash < hashEnd;
    }

    public TreeMap<Integer, Integer> getHashRangeServerIdMap() {
        return hashRangeServerIdMap;
    }


    public int getHashEnd() {
        return hashEnd;
    }

    public ShardedRedisServer(int id, int hashEnd, TreeMap<Integer, Integer> hashRangeServerIdMap) {
        super(id);
        this.hashEnd = hashEnd;
        this.hashRangeServerIdMap = hashRangeServerIdMap;
    }
}
