package cn.t.redisservice.client.demo;

import cn.t.redisservice.client.ShardedRedisServerClusterClient;
import cn.t.redisservice.common.RedisServer;
import cn.t.redisservice.server.ShardedRedisServer;
import cn.t.redisservice.server.builder.ShardedRedisServerBuilder;
import cn.t.redisservice.server.sharded.event.NodeRemovedEvent;
import cn.t.util.common.RandomUtil;

import java.util.List;
import java.util.TreeMap;

/**
 * @author yj
 * @since 2020-06-16 21:00
 **/
public class ShardedRedisServerClusterTest {
    public static void main(String[] args) {
        //构建分片集群
        List<ShardedRedisServer> shardedRedisServerList = ShardedRedisServerBuilder.build(6);
        //构建客户端
        TreeMap<Integer, RedisServer> hashRangeServerIdMap = new TreeMap<>();
        for(ShardedRedisServer server: shardedRedisServerList) {
            hashRangeServerIdMap.put(server.getHashEnd(), server);
        }
        ShardedRedisServerClusterClient client = new ShardedRedisServerClusterClient(hashRangeServerIdMap);
        for(int i=0; i< 1000; i++) {
            client.set(RandomUtil.randomString(10), String.valueOf(i));
        }
        //打印集群分片信息
        echoClusterInfo(shardedRedisServerList);
        //移除分片节点
        int indexToBeRemoved = RandomUtil.randomInt(0, shardedRedisServerList.size());
        ShardedRedisServer removedServer = shardedRedisServerList.remove(indexToBeRemoved);
        NodeRemovedEvent removedEvent = new NodeRemovedEvent(removedServer);
        for(ShardedRedisServer server: shardedRedisServerList) {
            server.onEvent(removedEvent);
        }
        //打印集群分片信息
        echoClusterInfo(shardedRedisServerList);
    }

    private static void echoClusterInfo(List<ShardedRedisServer> shardedRedisServerList) {
        for(ShardedRedisServer server: shardedRedisServerList) {
            System.out.println(server);
        }
    }
}
