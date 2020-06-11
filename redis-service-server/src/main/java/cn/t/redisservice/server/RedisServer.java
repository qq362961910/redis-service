package cn.t.redisservice.server;

public interface RedisServer {
    int getIndex();
    String get(String key);
    void set(String key, String value);
    void remove(String key);
}
