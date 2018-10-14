package com.index.es.dal.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.index.es.component.SpringBeanComponent;
import com.index.es.dal.DataCommonDao;
import com.index.es.model.DataSourceTemplate;
import com.index.es.model.EsNodeEntry;
import com.index.es.model.IndexConfig;
import com.index.es.type.DBType;
import com.index.es.type.EsType;

/**
 * mysql 数据拉取
 * 
 * @author lic
 * @date 2018年8月28日
 * @since v1.0.0
 */
@Repository
public class DataCommonDaoImpl implements DataCommonDao {

	/**
	 * 获取最大主键id
	 * 
	 * @param indexConfig
	 * @return
	 * @author lic
	 * @date 2018年8月28日
	 * @see com.index.es.dal.DataCommonDao#getMaxId(com.index.es.model.IndexConfig)
	 */
	@Override
	public int getMaxId(IndexConfig indexConfig) {
		JdbcTemplate template = getJdbcTemplate(indexConfig.getDbName());
		String sql = String.format("select max(%s) as maxId from %s;", indexConfig.getDbPrimaryKey(),
				indexConfig.getTableName());
		Map<String, Object> resultMap = template.queryForMap(sql);
		if (MapUtils.isEmpty(resultMap)) {
			return 0;
		}
		return NumberUtils.createBigInteger(resultMap.get("maxId").toString()).intValue();
	}

	/**
	 * 分页获取数据
	 * 
	 * @param indexConfig
	 * @return
	 * @author lic
	 * @date 2018年8月28日
	 * @see com.index.es.dal.DataCommonDao#selectWithPage(com.index.es.model.IndexConfig)
	 */
	@Override
	public EsNodeEntry selectWithPage(IndexConfig indexConfig) {
		List<Map<String, Object>> nameValues = Lists.newArrayList();
		JdbcTemplate template = getJdbcTemplate(indexConfig.getDbName());
		String sql = String.format("select * from %s where %s > %s order by %s asc limit %s;",
				indexConfig.getTableName(), indexConfig.getDbPrimaryKey(), indexConfig.getCurrentId(),
				indexConfig.getDbPrimaryKey(), indexConfig.getPageSize());
		SqlRowSet rowset = template.queryForRowSet(sql);
		SqlRowSetMetaData metaData = rowset.getMetaData();
		while (rowset.next()) {
			Map<String, Object> nameValue = Maps.newHashMap();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				String fieldName = metaData.getColumnName(i);
				String fieldType = replaceUnsigned(metaData.getColumnTypeName(i));
				if (fieldName.equals(indexConfig.getDbPrimaryKey())) {
					updateCurrentId(indexConfig, rowset.getInt(i));
				}
				if (DBType.typeOfString(fieldType)) {
					nameValue.put(fieldName, rowset.getString(i) + StringUtils.EMPTY);
				} else if (DBType.typeOfBigDecimal(fieldType)) {
					nameValue.put(fieldName, rowset.getBigDecimal(i) + StringUtils.EMPTY);
				} else if (DBType.typeOfDate(fieldType)) {
					nameValue.put(fieldName, rowset.getDate(i) + StringUtils.EMPTY);
				} else if (DBType.typeOfDouble(fieldType)) {
					nameValue.put(fieldName, NumberUtils.toDouble(rowset.getDouble(i) + StringUtils.EMPTY));
				} else if (DBType.typeOfFloat(fieldType)) {
					nameValue.put(fieldName, NumberUtils.toFloat(rowset.getFloat(i) + StringUtils.EMPTY));
				} else if (DBType.typeOfInt(fieldType)) {
					nameValue.put(fieldName, NumberUtils.toInt(rowset.getInt(i) + StringUtils.EMPTY));
				} else if (DBType.typeOfTByte(fieldType)) {
					nameValue.put(fieldName, rowset.getByte(i) + StringUtils.EMPTY);
				} else if (DBType.typeOfTime(fieldType)) {
					nameValue.put(fieldName, rowset.getTime(i) + StringUtils.EMPTY);
				} else if (DBType.typeOfTimestamp(fieldType)) {
					nameValue.put(fieldName, rowset.getTimestamp(i) + StringUtils.EMPTY);
				}
			}
			nameValues.add(nameValue);
		}
		EsNodeEntry node = new EsNodeEntry();
		node.setIndex(indexConfig.getDbName());
		node.setId(indexConfig.getDbPrimaryKey());
		node.setType(indexConfig.getTableName());
		node.setNameValueDocs(nameValues);
		return node;
	}

	/**
	 * 获取表结构
	 * 
	 * @param indexConfig
	 * @return
	 * @author lic
	 * @date 2018年8月28日
	 * @see com.index.es.dal.DataCommonDao#getTableMapping(com.index.es.model.IndexConfig)
	 */
	@Override
	public Map<String, String> getTableMapping(IndexConfig indexConfig) {
		JdbcTemplate jdbcTemplate = getJdbcTemplate(indexConfig.getDbName());
		String sql = String.format("select * from %s limit 1;", indexConfig.getTableName());
		SqlRowSet rowset = jdbcTemplate.queryForRowSet(sql);
		SqlRowSetMetaData metaData = rowset.getMetaData();
		Map<String, String> NameTypeSchema = Maps.newHashMap();
		while (rowset.next()) {
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				String fieldName = metaData.getColumnName(i);
				String fieldType = replaceUnsigned(metaData.getColumnTypeName(i));
				if (DBType.typeOfString(fieldType)) {
					NameTypeSchema.put(fieldName, EsType.STRING);
				} else if (DBType.typeOfBigDecimal(fieldType)) {
					NameTypeSchema.put(fieldName, EsType.STRING);
				} else if (DBType.typeOfDate(fieldType)) {
					NameTypeSchema.put(fieldName, EsType.STRING);
				} else if (DBType.typeOfDouble(fieldType)) {
					NameTypeSchema.put(fieldName, EsType.DOUBLE);
				} else if (DBType.typeOfFloat(fieldType)) {
					NameTypeSchema.put(fieldName, EsType.FLOAT);
				} else if (DBType.typeOfInt(fieldType)) {
					NameTypeSchema.put(fieldName, EsType.INTEGER);
				} else if (DBType.typeOfTByte(fieldType)) {
					NameTypeSchema.put(fieldName, EsType.STRING);
				} else if (DBType.typeOfTime(fieldType)) {
					NameTypeSchema.put(fieldName, EsType.STRING);
				} else if (DBType.typeOfTimestamp(fieldType)) {
					NameTypeSchema.put(fieldName, EsType.STRING);
				}
			}
		}
		return NameTypeSchema;
	}

	/**
	 * 按dbName 获取JdbcTemplate
	 * 
	 * @param dbName
	 * @return JdbcTemplate
	 * @author lic
	 * @date 2018年8月28日
	 */
	private JdbcTemplate getJdbcTemplate(String dbName) {
		DataSourceTemplate beanTemplate = (DataSourceTemplate) SpringBeanComponent.instance().getBean(dbName);
		return beanTemplate.getJdbcTemplate();
	}

	/**
	 * 更新分页最新id
	 * 
	 * @param indexConfig
	 * @param currentId   void
	 * @author lic
	 * @date 2018年8月28日
	 */
	private void updateCurrentId(IndexConfig indexConfig, int currentId) {
		int oldId = indexConfig.getCurrentId();
		indexConfig.setCurrentId(currentId > oldId ? currentId : oldId);
	}

	/**
	 * 去掉 无符号 标志
	 * 
	 * @param type
	 * @return String
	 * @author lic
	 * @date 2018年8月28日
	 */
	private String replaceUnsigned(String type) {
		return type.replace(" UNSIGNED", StringUtils.EMPTY);
	}

}
