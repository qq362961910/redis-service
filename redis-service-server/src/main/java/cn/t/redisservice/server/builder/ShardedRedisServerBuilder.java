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
        int maxHash = Integer.MAX_VALUE;
        int unit = maxHash / quantity;
        int remain = maxHash % quantity;
        List<ShardedRedisServer> shardedRedisServerList = new ArrayList<>();
        TreeMap<Integer, Integer> hashRangeServerIdMap = new TreeMap<>();
        for(int i=1; i<quantity; i++) {
            int hashEnd = i * unit;
            addSimpleShardedRedisServer(i, hashEnd, shardedRedisServerList, hashRangeServerIdMap);
        }
        if(remain > 0) {
            addSimpleShardedRedisServer(quantity, maxHash, shardedRedisServerList, hashRangeServerIdMap);
        }
        for(ShardedRedisServer server: shardedRedisServerList) {
            server.initializeCluster(hashRangeServerIdMap);
        }
        return shardedRedisServerList;
    }
    private static void addSimpleShardedRedisServer(int id, int hashEnd, List<ShardedRedisServer> shardedRedisServerList, TreeMap<Integer, Integer> hashRangeServerIdMap) {
        hashRangeServerIdMap.put(hashEnd, id);
        SimpleShardedRedisServer redisServer = new SimpleShardedRedisServer(id, hashEnd);
        shardedRedisServerList.add(redisServer);
    }
}
