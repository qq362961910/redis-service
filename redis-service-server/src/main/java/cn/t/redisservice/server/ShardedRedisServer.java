package cn.t.redisservice.server;

import cn.t.redisservice.common.util.KeyUtil;
import cn.t.redisservice.server.sharded.AbstractRedisServer;
import cn.t.redisservice.server.sharded.event.ShardedEvent;

import java.util.Map;
import java.util.TreeMap;


/**
 * @author yj
 * @since 2020-06-11 19:27
 **/
public abstract class ShardedRedisServer extends AbstractRedisServer {

    //hashRange -> serverId map
    private final TreeMap<Integer, Integer> hashRangeServerIdMap;

    public abstract void onEvent(ShardedEvent shardedEvent);

    protected boolean belongsToMe(String key) {
        int hash = KeyUtil.hashKey(key);
        Map.Entry<Integer, Integer> entry = hashRangeServerIdMap.higherEntry(hash);
        if(entry == null) {
            return false;
        }
        return id == entry.getValue();
    }

    public TreeMap<Integer, Integer> getHashRangeServerIdMap() {
        return hashRangeServerIdMap;
    }

    public ShardedRedisServer(int id, TreeMap<Integer, Integer> hashRangeServerIdMap) {
        super(id);
        this.hashRangeServerIdMap = hashRangeServerIdMap;
    }
}
