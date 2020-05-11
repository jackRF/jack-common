package org.jack.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

@Component
public class RedisDistributedLock {
    private static final Logger logger=LoggerFactory.getLogger(RedisDistributedLock.class);
    @Autowired
	private RedisTemplate<String, String> redisTemplate;
    private static final String UNLOCK_LUA;
    static {
        StringBuilder sb = new StringBuilder();
        sb.append("if redis.call(\"get\",KEYS[1]) == ARGV[1] ");
        sb.append("then ");
        sb.append("    return redis.call(\"del\",KEYS[1]) ");
        sb.append("else ");
        sb.append("    return 0 ");
        sb.append("end ");
        UNLOCK_LUA = sb.toString();
    }
    public boolean tryLock(String lockKey,String randomValue,long timeout, TimeUnit unit){
        logger.info("tryLock lockKey:{},randomValue:{},timeout:{},unit:{}",lockKey,randomValue, timeout, unit);
        boolean locked=redisTemplate.boundValueOps(lockKey).setIfAbsent(randomValue, timeout, unit);
        logger.info("locked:"+locked);
        return locked;
    }
    public void releaseLock(String lockKey,String randomValue){
        logger.info("releaseLock lockKey:{},randomValue:{}",lockKey,randomValue);
        DefaultRedisScript<Long> releaseLockScript=new DefaultRedisScript<Long>(UNLOCK_LUA,Long.class);
        List<String> keys=new ArrayList<String>();
        keys.add(lockKey);
        redisTemplate.execute(releaseLockScript, keys, randomValue);
    }
}