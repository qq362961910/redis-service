package cn.t.redisservice.server.builder;

import cn.t.redisservice.server.ShardedRedisServer;
import cn.t.redisservice.server.SimpleShardedRedisServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author yj
 * @since 2020-06-15 19:40
 **/
public class ShardedRedisServerBuilder {
    public static List<ShardedRedisServer> build(int quantity) {
        int minHash = Integer.MIN_VALUE;
        int maxHash = Integer.MAX_VALUE;
        List<ShardedRedisServer> shardedRedisServerList = new ArrayList<>();
        TreeMap<Integer, Integer> hashRangeServerIdMap = new TreeMap<>();
        //添加物理节点
        addPhysicalNodes(quantity, minHash, maxHash, shardedRedisServerList, hashRangeServerIdMap);
        //添加虚拟节点
//        addVirtualNodes(hashRangeServerIdMap, 6, minHash);
        for(ShardedRedisServer server: shardedRedisServerList) {
            server.initializeCluster(hashRangeServerIdMap);
        }
        return shardedRedisServerList;
    }

    public static void addVirtualNodes(TreeMap<Integer, Integer> hashRangeServerIdMap, int split, int minHash) {
        int splitToUse = Math.abs(split);
        int hashBegin = minHash;
        Map<Integer, Integer> virtualNodes = new TreeMap<>();
        for(Map.Entry<Integer, Integer> entry: hashRangeServerIdMap.entrySet()) {
            int hashEnd = entry.getKey();
            int difference = hashEnd - hashBegin + 1;
            int unit = difference / splitToUse;
            int forwardHashEnd = hashEnd;
            for(int i=1; i<splitToUse; i++) {
                Map.Entry<Integer, Integer> hashRangeServerEntry = hashRangeServerIdMap.higherEntry(forwardHashEnd);
                if(hashRangeServerEntry == null) {
                    hashRangeServerEntry = hashRangeServerIdMap.firstEntry();
                }
                hashEnd = hashEnd - unit;
                virtualNodes.put(hashEnd, hashRangeServerEntry.getValue());
                forwardHashEnd = hashRangeServerEntry.getKey();
            }
            hashBegin = entry.getKey() + 1;
        }
        hashRangeServerIdMap.putAll(virtualNodes);
    }

    public static void addPhysicalNodes(int quantity, int minHash, int maxHash, List<ShardedRedisServer> shardedRedisServerList, TreeMap<Integer, Integer> hashRangeServerIdMap) {
        long minHashToUse = minHash;
        long maxHashToUse = maxHash;
        long difference = maxHashToUse - minHashToUse;
        long unit = difference / quantity;
        long remain = difference % quantity;
        for(int i=1; i<quantity; i++) {
            int hashEnd = (int)(minHashToUse + (i * unit));
            addSimpleShardedRedisServer(i, hashEnd, shardedRedisServerList, hashRangeServerIdMap);
        }
        if(remain != 0) {
            addSimpleShardedRedisServer(quantity, maxHash, shardedRedisServerList, hashRangeServerIdMap);
        }
    }

    private static void addSimpleShardedRedisServer(int id, int hashEnd, List<ShardedRedisServer> shardedRedisServerList, TreeMap<Integer, Integer> hashRangeServerIdMap) {
        hashRangeServerIdMap.put(hashEnd, id);
        SimpleShardedRedisServer redisServer = new SimpleShardedRedisServer(id, hashEnd);
        shardedRedisServerList.add(redisServer);
    }
}
