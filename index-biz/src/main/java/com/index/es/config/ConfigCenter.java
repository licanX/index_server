package com.index.es.config;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.index.es.model.DBServer;
import com.index.es.model.IndexConfig;

/**
 * 配置项
 * @author lic
 * @date 2018年8月27日
 * @since v1.0.0
 */
@Component
public class ConfigCenter {

	private final static String URL_KEY = "jdbc.url";
	private final static String USER_KEY = "jdbc.user";
	private final static String PASSWORD_KEY = "jdbc.password";
	
	private final static String DB_NAME_KEY = "dbName";
	private final static String ES_PRIMARY_KEY = "esPrimaryKey";
	private final static String TABLE_NAME_KEY = "tableName";
	private final static String DB_PRIMARY_KEY = "dbPrimaryKey";
	private final static String PAGE_SIZE_KEY = "pageSize";
	private final static String SHARD_KEY = "shard";
	private final static String REPLICA_KEY = "replica";
	
	@Value("${common.database}")
	private String dbConfig;
	@Value("${common.driver.class}")
	private String driver;
	@Value("${common.index.table}")
	private String tableConfig;
	
	private Map<String,Map<String,String>> dbMap = Maps.newHashMap();
	private List<Map<String,String>> tables = Lists.newArrayList();
	
	@SuppressWarnings("unchecked")
	@PostConstruct
	private void initDBConfig() {
		dbMap = JSON.parseObject(dbConfig,Map.class);
		tables = JSON.parseObject(tableConfig,List.class);
	}
	
	public Map<String,DBServer> getDBServerMap(){
		Map<String, DBServer> dbEntry = Maps.newHashMap();
		for(Entry<String, Map<String,String>> entry : dbMap.entrySet()) {
			String dbKey = entry.getKey();
			Map<String,String> map = entry.getValue();
			DBServer server = new DBServer();
			server.setDriver(driver);
			server.setUrl(map.get(URL_KEY));
			server.setUser(map.get(USER_KEY));
			server.setPass(map.get(PASSWORD_KEY));
			dbEntry.put(dbKey, server);
		}
		return dbEntry;
	}
	
	public List<IndexConfig> getIndexConfig(){
		List<IndexConfig> indexConfigs = Lists.newArrayList();
		tables.forEach(table->{
			IndexConfig indexConfig = new IndexConfig();
			indexConfig.setDbName(table.get(DB_NAME_KEY));
			indexConfig.setDbPrimaryKey(table.get(DB_PRIMARY_KEY));
			indexConfig.setEsPrimaryKey(table.get(ES_PRIMARY_KEY));
			indexConfig.setPageSize(NumberUtils.toInt(table.get(PAGE_SIZE_KEY)));
			indexConfig.setTableName(table.get(TABLE_NAME_KEY));
			indexConfig.setReplica(NumberUtils.toInt(table.get(REPLICA_KEY)));
			indexConfig.setShard(NumberUtils.toInt(table.get(SHARD_KEY)));
			indexConfigs.add(indexConfig);
		});
		return indexConfigs;
	}
	
	
}
