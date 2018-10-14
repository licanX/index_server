package com.index.es.model;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * JdbcTemplate Bean
 * 
 * @author lican
 * @date 2018年8月27日
 * @since v1.0.0
 */
public class DataSourceTemplate {

	public final static String DBENTRY_NAME = "dbServer";
	
	private JdbcTemplate jdbcTemplate;
	
	private DBServer dbServer;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public DBServer getDbServer() {
		return dbServer;
	}

	public void setDbServer(DBServer dbServer) {
		this.dbServer = dbServer;
		initTemplate();
	}

	private void initTemplate() {
		JdbcTemplate template = new JdbcTemplate();
		template.setDataSource(dataSource());
		this.jdbcTemplate = template;
	}
	
	private DataSource dataSource() {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setDriverClassName(dbServer.getDriver());
		dataSource.setUrl(dbServer.getUrl());
		dataSource.setUsername(dbServer.getUser());
		dataSource.setPassword(dbServer.getPass());
		dataSource.setMinIdle(3);
		dataSource.setTestOnBorrow(true);
		dataSource.setTestWhileIdle(true);
		dataSource.setMinEvictableIdleTimeMillis(60000);
		dataSource.setMaxActive(50);
		dataSource.setInitialSize(10);
		dataSource.setValidationQuery("select 1");
		return dataSource;
	}
}
