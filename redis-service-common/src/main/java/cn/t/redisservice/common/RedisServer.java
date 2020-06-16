package cn.t.redisservice.common;

import java.util.Map;
import java.util.Set;

public interface RedisServer {
    int getId();
    String get(String key);
    void set(String key, String value);
    void remove(String key);
    int size();
    Set<String> allKeys();
    Set<Map.Entry<String, String>> dump();
    void addAll(Set<Map.Entry<String, String>> set);
}
