package com.index.es.schedule;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.index.es.component.RedisComponent;
import com.index.es.config.ConfigCenter;
import com.index.es.enums.DBEventType;
import com.index.es.model.IndexConfig;
import com.index.es.service.IndexService;

/**
 * 全量定时任务
 * 
 * @author lican
 * @date 2018年8月28日
 * @since v1.0.0
 */
@Component
public class IndexSchedule implements ApplicationListener<ApplicationEvent>{

	private static final Logger LOG = LoggerFactory.getLogger(IndexSchedule.class);

	private static final String INDEX_LOCK_KEY = "com:index:es:daily:lock:";
	private static final String INDEX_LOCK_VALUE = "true";
	
	@Autowired
	private ConfigCenter config;
	@Autowired
	private RedisComponent redis;
	@Autowired
	private IndexService indexService;
	
	/**
	 * 定时任务，全部索引全量更新
	 */
	@Scheduled(cron="${index.daily.cron}")
	public void daily() {
		List<IndexConfig> indexConfigs = config.getIndexConfig();
		indexConfigs.forEach(indexConfig->{
			String lockKey = getRedisLockKey(indexConfig);
			if(redis.setNX(lockKey, INDEX_LOCK_VALUE,60,TimeUnit.SECONDS)) {
				LOG.info("[daily]schedule daily begin,current index:{}",indexConfig);
				indexService.createNode(indexConfig);
				indexService.indexDaily(indexConfig);
				redis.delete(lockKey);
			}
		});
	}
	
	/**
	 * 指定索引，全量更新
	 * @param index
	 */
	public void daily(String index) {
		List<IndexConfig> indexConfigs = config.getIndexConfig();
		indexConfigs.forEach(indexConfig->{
			if(indexConfig.getTableName().equals(index)) {
				indexConfig.setEventType(DBEventType.UPDATE);
				String lockKey = getRedisLockKey(indexConfig);
				if(redis.setNX(lockKey, INDEX_LOCK_VALUE,60,TimeUnit.SECONDS)) {
					LOG.info("[daily]load daily begin,current index:{},indexConfig:{}",index,indexConfig);
					// TODO : index.createNode;
					// TODO : index.createIndex;
					redis.delete(lockKey);
				}
			}
		});
		
	}
	
	/**
	 * 容器关闭时，清除索引抢占锁
	 */
	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if(event instanceof ContextStoppedEvent || event instanceof ContextClosedEvent) {
			cleanLock();
		}
	}

	public void cleanLock() {
		List<IndexConfig> indexConfigs = config.getIndexConfig();
		indexConfigs.forEach(indexConfig->{
			redis.delete(getRedisLockKey(indexConfig));
		});
	}
	
	private String getRedisLockKey(IndexConfig indexConfig) {
		return new StringBuilder(INDEX_LOCK_KEY).append(JSON.toJSONString(indexConfig)).toString();
	}
	
}
