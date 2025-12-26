package zhenghc.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisUtil {

    @Autowired
    private static RedisTemplate<String, Object> redisTemplate;

    public static void setKey(String key, Object value){
        redisTemplate.opsForValue().set(key, value);
    }

    public static Object getKey(String key){
        return redisTemplate.opsForValue().get(key);
    }

}
