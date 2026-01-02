package zhenghc.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void set(String key, Object value){
        redisTemplate.opsForValue().set(key, value);
    }

    public Object getValue(String key){
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 限时，按分钟
     */
    public void setMin(String key, Object value, Integer time){
        redisTemplate.opsForValue().set(key, value, time, java.util.concurrent.TimeUnit.MINUTES);
    }

    public void delete(String key){
        redisTemplate.delete(key);
    }

}
