package cn.t.redisservice.server.builder;

import cn.t.redisservice.server.ShardedRedisServer;
import cn.t.redisservice.server.SimpleShardedRedisServer;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * @author yj
 * @since 2020-06-15 19:40
 **/
public class ShardedRedisServerBuilder {
    public static List<ShardedRedisServer> build(int quantity) {
//        int maxHash = Integer.MAX_VALUE;
        int maxHash = 1000;
        int unit = maxHash / quantity;
        int remain = maxHash % quantity;
        List<ShardedRedisServer> shardedRedisServerList = new ArrayList<>();
        TreeMap<Integer, Integer> hashRangeServerIdMap = new TreeMap<>();
        for(int i=1; i<quantity; i++) {
            int id = i;
            int hashEnd = i * unit;
            hashRangeServerIdMap.put(hashEnd, id);
            SimpleShardedRedisServer redisServer = new SimpleShardedRedisServer(id, hashEnd, hashRangeServerIdMap);
            shardedRedisServerList.add(redisServer);
        }
        if(remain > 0) {
            int id = quantity;
            int hashEnd = maxHash;
            SimpleShardedRedisServer redisServer = new SimpleShardedRedisServer(id, hashEnd, hashRangeServerIdMap);
            shardedRedisServerList.add(redisServer);
        }
        return shardedRedisServerList;
    }
}
