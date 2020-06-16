package cn.t.redisservice.client.demo;

import cn.t.redisservice.client.ShardedRedisServerClusterClient;
import cn.t.redisservice.common.RedisServer;
import cn.t.redisservice.server.ShardedRedisServer;
import cn.t.redisservice.server.builder.ShardedRedisServerBuilder;
import cn.t.util.common.RandomUtil;

import java.util.List;
import java.util.TreeMap;

/**
 * @author yj
 * @since 2020-06-16 21:00
 **/
public class ShardedRedisServerClusterTest {
    public static void main(String[] args) {
        List<ShardedRedisServer> shardedRedisServerList = ShardedRedisServerBuilder.build(6);
        TreeMap<Integer, RedisServer> hashRangeServerIdMap = new TreeMap<>();
        for(ShardedRedisServer server: shardedRedisServerList) {
            hashRangeServerIdMap.put(server.getHashEnd(), server);
        }
        ShardedRedisServerClusterClient client = new ShardedRedisServerClusterClient(hashRangeServerIdMap);
        for(int i=0; i< 10000; i++) {
            client.set(RandomUtil.randomString(100), String.valueOf(i));
        }
        for(ShardedRedisServer server: shardedRedisServerList) {
            System.out.println(server);
        }
    }
}
