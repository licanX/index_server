package com.index.es.model;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.index.es.enums.DBEventType;

/**
 * 单个数据库配置项
 * 
 * @author lican
 * @date 2018年8月27日
 * @since v1.0.0
 */
public class IndexConfig {

	private static final Logger LOG = LoggerFactory.getLogger(IndexConfig.class);
	
	/*********************** database *******************************/

	private String dbName;

	private String tableName;

	private DBEventType eventType = DBEventType.INSERT;

	private String dbPrimaryKey = "id";

	private Integer currentId = 0;

	private Integer maxId = 0;

	private Integer pageSize = 80;

	/*********************** elasticsearch ***************************/

	private String esPrimaryKey;

	private Integer shard = 3;

	private Integer replica = 1;

	private XContentBuilder mapping;

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public DBEventType getEventType() {
		return eventType;
	}

	public void setEventType(DBEventType eventType) {
		this.eventType = eventType;
	}

	public String getDbPrimaryKey() {
		return dbPrimaryKey;
	}

	public void setDbPrimaryKey(String dbPrimaryKey) {
		this.dbPrimaryKey = dbPrimaryKey;
	}

	public Integer getCurrentId() {
		return currentId;
	}

	public void setCurrentId(Integer currentId) {
		this.currentId = currentId;
	}

	public Integer getMaxId() {
		return maxId;
	}

	public void setMaxId(Integer maxId) {
		this.maxId = maxId;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public String getEsPrimaryKey() {
		return esPrimaryKey;
	}

	public void setEsPrimaryKey(String esPrimaryKey) {
		this.esPrimaryKey = esPrimaryKey;
	}

	public Integer getShard() {
		return shard;
	}

	public void setShard(Integer shard) {
		this.shard = shard;
	}

	public Integer getReplica() {
		return replica;
	}

	public void setReplica(Integer replica) {
		this.replica = replica;
	}

	public XContentBuilder getMapping() {
		return mapping;
	}

	public void setMapping(XContentBuilder mapping) {
		this.mapping = mapping;
	}

	/**
	 * 构建Mapping
	 * 
	 * @param fieldTypeMap void
	 * @author lic
	 * @date 2018年8月28日
	 */
	public void setMapping(Map<String, String> fieldTypeMap) {
		XContentBuilder schema = null;
		try {
			schema = jsonBuilder().startObject().startObject("properties");
			for (Entry<String, String> fieldType : fieldTypeMap.entrySet()) {
				String name = fieldType.getKey();
				String type = fieldType.getValue();
				schema.startObject(name).field("type", type).field("store", "true").endObject();
			}
			schema.endObject().endObject();
		} catch (Exception e) {
			LOG.error("[setMapping]build error,schema :{}",schema.toString(),e);
		}
		mapping = schema;
	}

	/**
	 * 将数据结构体转换为索引结构
	 * 
	 * @param fieldValueMap
	 * @return
	 * @throws Exception XContentBuilder
	 * @author lic
	 * @date 2018年8月28日
	 */
	public static XContentBuilder parseDocument(Map<String, Object> fieldValueMap) throws Exception {
		XContentBuilder document = jsonBuilder().startObject();
		for(Entry<String, Object> fieldValue : fieldValueMap.entrySet()) {
			String name = fieldValue.getKey();
			Object value = fieldValue.getValue();
			document.field(name,value);
		}
		document.field("update_time",System.currentTimeMillis()/1000);
		document.endObject();
		return document;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}

}
