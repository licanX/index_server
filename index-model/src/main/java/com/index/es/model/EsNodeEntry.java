package com.index.es.model;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Es 节点属性实体
 * 
 * @author lican
 * @date 2018年8月28日
 * @since v1.0.0
 */
public class EsNodeEntry {

	/**
	 * 索引名
	 */
	private String index;
	/**
	 * 类型名
	 */
	private String type;
	/**
	 * doc 主键id
	 */
	private String id;
	/**
	 * 字段和字段类型映射
	 */
	private Map<String, String> nameTypeSchema = Maps.newHashMap();
	/**
	 * 字段和字段值映射
	 */
	private List<Map<String, Object>> nameValueDocs = Lists.newArrayList();

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Map<String, String> getNameTypeSchema() {
		return nameTypeSchema;
	}

	public void setNameTypeSchema(Map<String, String> nameTypeSchema) {
		this.nameTypeSchema = nameTypeSchema;
	}

	public List<Map<String, Object>> getNameValueDocs() {
		return nameValueDocs;
	}

	public void setNameValueDocs(List<Map<String, Object>> nameValueDocs) {
		this.nameValueDocs = nameValueDocs;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}

}
