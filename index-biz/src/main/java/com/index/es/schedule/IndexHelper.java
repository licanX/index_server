package com.index.es.schedule;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.index.es.component.RedisComponent;
import com.index.es.model.IndexConfig;

/**
 * 索引构建工具类
 * 
 * @author lic
 * @date 2018年8月28日
 * @since
 */
@Component
public class IndexHelper {
	
	private static final Logger LOG = LoggerFactory.getLogger(IndexHelper.class);
	
	@Autowired
	private RedisComponent redis;

	private final static String INDEXREQUEST_FAILLIST_REDISKEY = "smart-index.es.failure.indexRequestBuilder";

	/** 从上一次失败的id继续建索引 */
	public void lastSpotRedisLoader(IndexConfig indexConfig) {
		String redisKey = getIndexConfigRedisKey(indexConfig);
		Integer lastSpotCurrentId = redis.get(redisKey, Integer.class);
		if (null != lastSpotCurrentId && lastSpotCurrentId < indexConfig.getMaxId()) {
			indexConfig.setCurrentId(lastSpotCurrentId);
		}
	}

	/** 将中断现场id存入redis */
	public void currentSpotRedisWriter(IndexConfig indexConfig) {
		String redisKey = getIndexConfigRedisKey(indexConfig);
		redis.set(redisKey, indexConfig.getCurrentId(), 300,TimeUnit.SECONDS);
	}

	/***
	 * 从redis读出建索引失败的requestBuilder
	 *
	 * @return 2017年10月18日
	 */
	@SuppressWarnings(value="unchecked")
	public void failListRedisReader(List<IndexRequestBuilder> requestBuilders) {
		List<JSONObject> jsonObjects = redis.get(INDEXREQUEST_FAILLIST_REDISKEY, List.class);
		if (CollectionUtils.isEmpty(jsonObjects)) {
			LOG.info("[failListRedisReader]redis target doc is empty;");
			return ;
		}
		requestBuilders.addAll(toIndexRequestBuilders(jsonObjects));
		/* 读完失败列表，删除redis中的记录 */
		redis.delete(INDEXREQUEST_FAILLIST_REDISKEY);
	}

	/**
	 * 索引失败列表写入redis
	 *
	 * @param requestBuilder 2017年10月18日
	 */
	@SuppressWarnings(value="unchecked")
	public void failListRedisWriter(List<IndexRequestBuilder> requestBuilders) {
		List<JSONObject> jsonObjects = redis.get(INDEXREQUEST_FAILLIST_REDISKEY, List.class);
		if (CollectionUtils.isNotEmpty(jsonObjects)) {
			LOG.info("[failListRedisReader]redis target docs :{}",jsonObjects);
			requestBuilders.addAll(toIndexRequestBuilders(jsonObjects));
		}
		redis.set(INDEXREQUEST_FAILLIST_REDISKEY, JSON.toJSON(requestBuilders), 300,TimeUnit.SECONDS);
	}

	/**
	 * JSONObjects 转 IndexRequestBuilders
	 *
	 * @param jsonObjects
	 * @return 2017年10月18日
	 */
	private List<IndexRequestBuilder> toIndexRequestBuilders(List<JSONObject> jsonObjects) {
		List<IndexRequestBuilder> requestBuilders = Lists.newArrayList();
		for (JSONObject json : jsonObjects) {
			IndexRequestBuilder requestBuilder = json.toJavaObject(IndexRequestBuilder.class);
			requestBuilders.add(requestBuilder);
		}
		return requestBuilders;
	}

	/** 构造索引现场的redis key */
	private String getIndexConfigRedisKey(IndexConfig indexConfig) {
		return new StringBuilder().append(indexConfig.getDbName()).append(indexConfig.getTableName())
				.append(indexConfig.getEsPrimaryKey()).append(indexConfig.getDbPrimaryKey()).toString();
	}
}
