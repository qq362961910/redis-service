package cn.t.redisservice.server.builder;

import cn.t.redisservice.server.ShardedRedisServer;
import cn.t.redisservice.server.SimpleShardedRedisServer;
import cn.t.redisservice.server.sharded.AbstractRedisServer;
import cn.t.redisservice.server.sharded.event.NodeAddedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author yj
 * @since 2020-06-15 19:40
 **/
public class ShardedRedisServerUtil {

    private static final Logger logger = LoggerFactory.getLogger(ShardedRedisServerUtil.class);

    /**
     * 构建分片集群
     * @param minHash 最小hash
     * @param maxHash 最大hash
     * @param quantity 分片数量
     * @return 集群列表
     */
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

    public static void addNode(int minHash, List<ShardedRedisServer> shardedRedisServerList) {
        int maxRangeIndex = 0;
        int maxLoad = shardedRedisServerList.get(maxRangeIndex).getHashEnd() - minHash;
        for(int i=1; i<shardedRedisServerList.size(); i++) {
            int load = shardedRedisServerList.get(i).getHashEnd() - shardedRedisServerList.get(i-1).getHashEnd();
            if(load > maxLoad) {
                logger.info("切换最大load节点, old index: {}, old load: {}, new index: {}, new load: {}", maxRangeIndex, maxLoad, i, load);
                //切换最大load节点
                maxRangeIndex = i;
                maxLoad = load;
            }
        }
        ShardedRedisServer maxLoadServer = shardedRedisServerList.get(maxRangeIndex);
        int newNodeLoad = maxLoad / 2;
        int newNodeEndHash = maxLoadServer.getHashEnd() - newNodeLoad;
        int id = shardedRedisServerList.stream().mapToInt(AbstractRedisServer::getId).max().getAsInt() + 1;
        ShardedRedisServer shardedRedisServer = new SimpleShardedRedisServer(id, newNodeEndHash);
        shardedRedisServerList.add(shardedRedisServer);
        NodeAddedEvent nodeAddedEvent = new NodeAddedEvent(shardedRedisServer);
        for(ShardedRedisServer server: shardedRedisServerList) {
            server.onEvent(nodeAddedEvent);
        }
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
