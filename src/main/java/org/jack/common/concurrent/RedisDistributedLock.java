package org.jack.common.concurrent;

import java.io.Serializable;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisDistributedLock {
	@Autowired
    private RedisTemplate<Serializable, Object> redisTemplate;
	@Resource(name="redisTemplate")
	private ListOperations<String, String> listOps;
	private void lock() {
//		listOps.
//		redisTemplate.execute(script, keys, args)c
	}
	private void unLock() {
		
	}
}
