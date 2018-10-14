package com.index.es.service;

import org.elasticsearch.action.update.UpdateRequest;

import com.index.es.model.EsNodeEntry;
import com.index.es.model.IndexConfig;

/**
 * 索引构建接口
 * 
 * @author lican
 * @date 2018年8月28日
 * @since v1.0.0
 */
public interface IndexService {

	/**
	 * 删除节点
	 * @param index
	 */
	public void deleteNode(String index);
	
	/**
	 * 创建节点和节点mapping
	 * @param indexConfig
	 */
	public void createNode(IndexConfig indexConfig);
	
	/**
	 * 全量索引
	 * @param indexConfig
	 */
	public void indexDaily(IndexConfig indexConfig);
	
	/**
	 * 更新索引
	 * @param updateRequest
	 */
	public void updateIndex(UpdateRequest updateRequest);
	
	/**
	 * 删除索引
	 * @param esNode
	 */
	public void deleteIndex(EsNodeEntry esNode);
	
}
