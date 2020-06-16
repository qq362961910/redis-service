package cn.t.redisservice.client;

public interface RedisClient {
    String get(String key);
    void set(String key, String value);
    void remove(String key);
}
