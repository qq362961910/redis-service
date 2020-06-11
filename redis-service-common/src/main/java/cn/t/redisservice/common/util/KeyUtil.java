package cn.t.redisservice.common.util;

/**
 * @author yj
 * @since 2020-06-11 19:47
 **/
public class KeyUtil {

    /**
     * key hash
     */
    public static int hashKey(String key) {
        return key == null ? 0 : key.hashCode();
    }

    /**
     * 计算key服务器索引
     */
    public static int calculateShardedServerIndex(String key, int serverCount) {
        return hashKey(key) % serverCount;
    }
}
