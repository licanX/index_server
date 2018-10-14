package com.index.es.dal;

import java.util.Map;

import com.index.es.model.EsNodeEntry;
import com.index.es.model.IndexConfig;

/**
 * mysql 数据拉取
 * @author lic
 * @date 2018年8月28日
 * @since v1.0.0
 */
public interface DataCommonDao {

	/**
	 * 获取最大主键id
	 * @param indexConfig
	 * @return int
	 * @author lic
	 * @date 2018年8月28日
	 */
	public int getMaxId(IndexConfig indexConfig);
	
	/**
	 * 分页获取数据
	 * @param indexConfig
	 * @return EsNodeEntry
	 * @author lic
	 * @date 2018年8月28日
	 */
	public EsNodeEntry selectWithPage(IndexConfig indexConfig);
	
	/**
	 * 获取表结构
	 * @param indexConfig
	 * @return Map<String,String>
	 * @author lic
	 * @date 2018年8月28日
	 */
	public Map<String,String> getTableMapping(IndexConfig indexConfig);
}
