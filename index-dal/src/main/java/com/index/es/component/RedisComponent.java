package com.index.es.component;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

/**
 * redis 组件
 * 
 * @author lican
 * @date 2018年8月27日
 * @since v1.0.0
 */
@Component
public class RedisComponent {

	@Autowired
	private RedisTemplate<String, String> redis;

	/**
	 * 写入redis,并设置失效时间
	 * 
	 * @param key
	 * @param data
	 * @param expire
	 * @param unit
	 */
	public void set(String key, Object data, long expire, TimeUnit unit) {
		set(key, data);
		if(expire > 0) {
			redis.expire(key, expire, unit);
		}
	}

	/**
	 * 写入redis,永久生效
	 * 
	 * @param key
	 * @param data
	 */
	public void set(String key, Object data) {
		if (data instanceof String) {
			redis.opsForValue().set(key, (String) data);
		}
		redis.opsForValue().set(key, JSON.toJSONString(data));
	}

	/**
	 * 分布式抢占锁
	 * 
	 * @param key
	 * @param data
	 */
	public boolean setNX(String key, Object data) {
		if (data instanceof String) {
			return redis.opsForValue().setIfAbsent(key, (String) data);
		}
		return redis.opsForValue().setIfAbsent(key, JSON.toJSONString(data));
	}
	
	/**
	 * 分布式抢占锁,指定占用时间
	 * 
	 * @param key
	 * @param data
	 */
	public boolean setNX(String key, Object data, long expire, TimeUnit unit) {
		boolean nx = setNX(key,data);
		if(expire > 0) {
			return nx && redis.expire(key, expire, unit);
		}
		return nx;
	}

	/**
	 * 读取redis,并解析为对象
	 * 
	 * @param key
	 * @param clazz
	 * @return
	 */
	public <T> T get(String key, Class<T> clazz) {
		String data = get(key);
		if (data == null) {
			return null;
		}
		return JSON.parseObject(data, clazz);
	}

	/**
	 * 读取redis字符串
	 * 
	 * @param key
	 * @return
	 */
	public String get(String key) {
		return redis.opsForValue().get(key);
	}
	
	/**
	 * 删除key
	 * @param key
	 */
	public void delete(String key) {
		redis.delete(key);
	}
}
