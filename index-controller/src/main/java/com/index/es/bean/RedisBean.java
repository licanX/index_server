package com.index.es.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * redis注入
 * @author lic
 * @date 2018年8月27日
 * @since v1.0.0
 */
public class RedisBean {
	
	@Bean
	public static JedisConnectionFactory redisFactory(Environment env) {
		JedisConnectionFactory redisFactory = new JedisConnectionFactory();
		redisFactory.setHostName(env.getProperty("redis.connect.host"));
		redisFactory.setPort(Integer.parseInt(env.getProperty("redis.connect.port")));
		redisFactory.setPassword(env.getProperty("redis.connect.pwd"));
		redisFactory.setUsePool(true);
		return redisFactory;
	}
	
	@Bean
	public static RedisTemplate<String, String> redisTemplate(Environment env){
		RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisFactory(env));
		return redisTemplate;
	}
	
	
}
