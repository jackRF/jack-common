package org.jack.common.config;

import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;

import org.jack.common.util.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Value("${spring.redis.cluster.nodes}")
    private String clusterNodes;
    @Value("${spring.redis.cluster.password}")
    private String password;
    @Value("${spring.redis.cluster.master}")
    private String master;

    @Bean
    public RedisConnectionFactory jedisConnectionFactory() {
        RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration()
        .master(master);
        for(String node:clusterNodes.split(",")){
            String[] pair=node.split(":");
            sentinelConfig.sentinel(pair[0], Integer.parseInt(pair[1]));
        }
        sentinelConfig.setPassword(password);
        return new LettuceConnectionFactory(sentinelConfig);
    }

    /**
     * 设置数据存入redis 的序列化方式 </br>
     * @param redisConnectionFactory
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        FastJsonConfig fastJsonConfig=new FastJsonConfig();
        fastJsonConfig.setDateFormat(DateUtils.DATE_FORMAT_DATETIME);
        FastJsonRedisSerializer<String> vs = new FastJsonRedisSerializer<>(String.class);
        vs.setFastJsonConfig(fastJsonConfig);
        redisTemplate.setValueSerializer(vs);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}