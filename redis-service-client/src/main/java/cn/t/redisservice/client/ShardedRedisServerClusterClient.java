package cn.t.redisservice.client;

import cn.t.redisservice.common.RedisServer;
import cn.t.redisservice.common.util.KeyUtil;

import java.util.TreeMap;

/**
 * @author yj
 * @since 2020-06-16 19:37
 **/
public class ShardedRedisServerClusterClient implements RedisClient {

    //hashRange -> server
    private final TreeMap<Integer, RedisServer> hashRangeServerIdMap;

    @Override
    public String get(String key) {
        return getKeyServer(key).get(key);
    }

    @Override
    public void set(String key, String value) {
        getKeyServer(key).set(key, value);
    }

    @Override
    public void remove(String key) {
        getKeyServer(key).remove(key);
    }

    private RedisServer getKeyServer(String key) {
        int hash = KeyUtil.hashKey(key);
        return hashRangeServerIdMap.higherEntry(hash).getValue();
    }

    public ShardedRedisServerClusterClient(TreeMap<Integer, RedisServer> hashRangeServerIdMap) {
        this.hashRangeServerIdMap = hashRangeServerIdMap;
    }
}
