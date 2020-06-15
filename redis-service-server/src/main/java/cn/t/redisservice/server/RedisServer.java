package cn.t.redisservice.server;

import java.util.Set;

public interface RedisServer {
    int getId();
    String get(String key);
    void set(String key, String value);
    void remove(String key);
    Set<String> allKeys();
}
