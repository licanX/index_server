package com.index.es.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.index.es.dal.DataCommonDao;
import com.index.es.enums.DBEventType;
import com.index.es.model.EsNodeEntry;
import com.index.es.model.IndexConfig;
import com.index.es.schedule.IndexHelper;
import com.index.es.service.IndexService;

/**
 * 索引构建
 * 
 * @author lican
 * @date 2018年8月28日
 * @since v1.0.0
 */
@Service
public class IndexServiceImpl implements IndexService {

	private static final Logger LOG = LoggerFactory.getLogger(IndexServiceImpl.class);

	@Autowired
	private TransportClient esClient;
	@Autowired
	private DataCommonDao commonDao;
	@Autowired
	private IndexHelper indexHelper;

	/**
	 * 删除节点
	 * 
	 * @param index
	 * @author lic
	 * @date 2018年8月28日
	 * @see com.index.es.service.IndexService#deleteNode(java.lang.String)
	 */
	@Override
	public void deleteNode(String index) {
		DeleteIndexResponse delResponse = esClient.admin().indices().prepareDelete(index).get();
		if (delResponse.isAcknowledged()) {
			LOG.info("[deleteNode]delete node:{} success", index);
			return;
		}
		LOG.error("[deleteNode]delete node:{} fail", index);
	}

	/**
	 * 创建节点和节点mapping
	 * 
	 * @param indexConfig
	 * @author lic
	 * @date 2018年8月28日
	 * @see com.index.es.service.IndexService#createNode(com.index.es.model.IndexConfig)
	 */
	@Override
	public void updateIndex(UpdateRequest updateRequest) {
		try {
			esClient.update(updateRequest).get();
			LOG.info("[updateIndex]update success,updateRequest：{}", JSON.toJSONString(updateRequest));
		} catch (InterruptedException e) {
			LOG.error("[updateIndex]update fail,updateRequest：{}", JSON.toJSONString(updateRequest), e);
		} catch (ExecutionException e) {
			LOG.error("[updateIndex]update fail,updateRequest：{}", JSON.toJSONString(updateRequest), e);
		}
	}

	@Override
	public void deleteIndex(EsNodeEntry esNode) {
		if (esNode != null && esNode.getId() != null) {
			esClient.prepareDelete(esNode.getIndex(), esNode.getType(), esNode.getId()).get();
			LOG.info("[deleteIndex]delete index:{} success;", esNode);
		}
	}

	@Override
	public void indexDaily(IndexConfig indexParam) {
		try {
			int maxId = commonDao.getMaxId(indexParam);
			indexHelper.lastSpotRedisLoader(indexParam);
			do {
				long startTime = System.currentTimeMillis();
				EsNodeEntry indexEntry = commonDao.selectWithPage(indexParam);
				List<IndexRequestBuilder> requestBuilders = getIndexRequestBuilder(indexEntry);
				commitByPage(requestBuilders);
				LOG.info("indexName:" + indexParam.getTableName() + ";currentId:" + indexParam.getCurrentId()
						+ ";maxId:" + maxId + ";cost time" + (System.currentTimeMillis() - startTime));
			} while (indexParam.getCurrentId() < maxId);
		} catch (Exception e) {
			indexHelper.currentSpotRedisWriter(indexParam);
			LOG.error("build daily index error , currentId : " + indexParam.getCurrentId(), e);
		}
	}

	@Override
	public void createNode(IndexConfig indexConfig) {
		if (createCollection(indexConfig) || indexConfig.getEventType() == DBEventType.UPDATE) {
			buildFieldTypeMap(indexConfig);
			buildMapping(indexConfig);
		}
	}

	/**
	 * 按分页数据提交索引
	 *
	 * @param requestBuilders 2017年10月18日
	 */
	private void commitByPage(List<IndexRequestBuilder> requestBuilders) {
		long startTime = System.currentTimeMillis();
		indexHelper.failListRedisReader(requestBuilders);
		if (CollectionUtils.isEmpty(requestBuilders)) {
			LOG.info("[commitByPage]request builder is empty");
			return ;
		}
		BulkRequestBuilder bulkRequest = esClient.prepareBulk();
		for (IndexRequestBuilder builder : requestBuilders) {
			bulkRequest.add(builder);
		}
		BulkResponse bulkResponse = bulkRequest.get();
		if (bulkResponse.hasFailures()) {
			indexHelper.failListRedisWriter(requestBuilders);
			LOG.error("[commitByPage]commit fail,will write redis：" + bulkResponse.buildFailureMessage());
		}
		LOG.info("[commitByPage]commit success,commit page cost time : " + (System.currentTimeMillis() - startTime));
	}

	/**
	 * 构建IndexRequestBuilders
	 *
	 * @param indexEntry : 数据库查出来的doc内容
	 * @return
	 * @throws Exception 2017年10月18日
	 */
	private List<IndexRequestBuilder> getIndexRequestBuilder(EsNodeEntry indexEntry) throws Exception {
		List<IndexRequestBuilder> requestBuilders = Lists.newArrayList();
		List<Map<String, Object>> fieldValueMaps = indexEntry.getNameValueDocs();
		if (null != fieldValueMaps && null != indexEntry.getId()) {
			for (Map<String, Object> fieldValueMap : fieldValueMaps) {
				String docId = fieldValueMap.get(indexEntry.getId()).toString();
				IndexRequestBuilder builder = esClient.prepareIndex(indexEntry.getType(), indexEntry.getType(), docId)
						.setSource(IndexConfig.parseDocument(fieldValueMap));
				requestBuilders.add(builder);
			}
		}
		return requestBuilders;
	}

	/**
	 * 按配置查询schema结构，放入IndexConfigParam.Mapping
	 *
	 * @param param 2017年10月18日
	 */
	private void buildFieldTypeMap(IndexConfig indexConfig) {
		Map<String, String> fieldTypeMap = commonDao.getTableMapping(indexConfig);
		indexConfig.setMapping(fieldTypeMap);
	}

	/**
	 * 创建索引
	 *
	 * @param indexName 2017年9月8日
	 */
	private synchronized boolean createCollection(IndexConfig indexConfig) {
		try {
			Map<String, Object> setting = new HashMap<>();
			setting.put("number_of_shards", indexConfig.getShard());
			setting.put("number_of_replicas", indexConfig.getReplica());
			CreateIndexRequestBuilder requestBuilder = esClient.admin().indices()
					.prepareCreate(indexConfig.getTableName()).setSettings(setting);
			esClient.admin().indices().create(requestBuilder.request()).actionGet();
		} catch (Exception e) {
			LOG.info("[createCollection]index is exist,indexConfig:{}", indexConfig);
			return false;
		}
		LOG.info("[createCollection]create collection success,indexConfig:{}", indexConfig);
		return true;
	}

	/**
	 * 添加schema-mapping
	 *
	 * @param indexName 2017年9月13日
	 */
	private void buildMapping(IndexConfig indexConfig) {
		PutMappingRequest mapping = Requests.putMappingRequest(indexConfig.getTableName())
				.type(indexConfig.getTableName()).source(indexConfig.getMapping());
		esClient.admin().indices().putMapping(mapping).actionGet();
	}
}
