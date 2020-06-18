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
public class ShardedRedisServerUtil {
    public static List<ShardedRedisServer> buildCluster(int minHash, int maxHash, int quantity) {
        List<ShardedRedisServer> shardedRedisServerList = new ArrayList<>();
        TreeMap<Integer, ShardedRedisServer> hashRangeServerMap = new TreeMap<>();
        //添加物理节点
        addPhysicalNodes(quantity, minHash, maxHash, shardedRedisServerList, hashRangeServerMap);
        //添加虚拟节点
//        addVirtualNodes(hashRangeServerMap, 6, minHash);
        for(ShardedRedisServer server: shardedRedisServerList) {
            server.initializeCluster(hashRangeServerMap);
        }
        return shardedRedisServerList;
    }

    public static void addVirtualNodes(TreeMap<Integer, Integer> hashRangeServerMap, int split, int minHash) {
        int splitToUse = Math.abs(split);
        int hashBegin = minHash;
        Map<Integer, Integer> virtualNodes = new TreeMap<>();
        for(Map.Entry<Integer, Integer> entry: hashRangeServerMap.entrySet()) {
            int hashEnd = entry.getKey();
            int difference = hashEnd - hashBegin + 1;
            int unit = difference / splitToUse;
            int forwardHashEnd = hashEnd;
            for(int i=1; i<splitToUse; i++) {
                Map.Entry<Integer, Integer> hashRangeServerEntry = hashRangeServerMap.higherEntry(forwardHashEnd);
                if(hashRangeServerEntry == null) {
                    hashRangeServerEntry = hashRangeServerMap.firstEntry();
                }
                hashEnd = hashEnd - unit;
                virtualNodes.put(hashEnd, hashRangeServerEntry.getValue());
                forwardHashEnd = hashRangeServerEntry.getKey();
            }
            hashBegin = entry.getKey() + 1;
        }
        hashRangeServerMap.putAll(virtualNodes);
    }

    public static void addPhysicalNodes(int quantity, int minHash, int maxHash, List<ShardedRedisServer> shardedRedisServerList, TreeMap<Integer, ShardedRedisServer> hashRangeServerMap) {
        long minHashToUse = minHash;
        long maxHashToUse = maxHash;
        long difference = maxHashToUse - minHashToUse;
        long unit = difference / quantity;
        long remain = difference % quantity;
        for(int i=1; i<quantity; i++) {
            int hashEnd = (int)(minHashToUse + (i * unit));
            addSimpleShardedRedisServer(i, hashEnd, shardedRedisServerList, hashRangeServerMap);
        }
        if(remain != 0) {
            addSimpleShardedRedisServer(quantity, maxHash, shardedRedisServerList, hashRangeServerMap);
        }
    }

    private static void addSimpleShardedRedisServer(int id, int hashEnd, List<ShardedRedisServer> shardedRedisServerList, TreeMap<Integer, ShardedRedisServer> hashRangeServerMap) {
        SimpleShardedRedisServer redisServer = new SimpleShardedRedisServer(id, hashEnd);
        hashRangeServerMap.put(hashEnd, redisServer);
        shardedRedisServerList.add(redisServer);
    }
}
