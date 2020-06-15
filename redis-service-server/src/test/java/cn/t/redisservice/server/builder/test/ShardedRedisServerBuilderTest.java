package cn.t.redisservice.server.builder.test;

import cn.t.redisservice.server.ShardedRedisServer;
import cn.t.redisservice.server.builder.ShardedRedisServerBuilder;
import org.junit.Test;

import java.util.List;

/**
 * @author yj
 * @since 2020-06-15 21:01
 **/
public class ShardedRedisServerBuilderTest {

    @Test
    public void buildTest() {
        List<ShardedRedisServer> shardedRedisServerList = ShardedRedisServerBuilder.build(6);
        for(ShardedRedisServer server: shardedRedisServerList) {
            System.out.println(server);
        }
    }
}
