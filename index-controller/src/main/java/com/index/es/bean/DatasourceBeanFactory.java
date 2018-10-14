package com.index.es.bean;

import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.stereotype.Service;

import com.index.es.config.ConfigCenter;
import com.index.es.model.DBServer;
import com.index.es.model.DataSourceTemplate;

/**
 * Datasource 注入
 * @author lic
 * @date 2018年8月27日
 * @since v1.0.0
 */
@Service
public class DatasourceBeanFactory implements BeanFactoryAware{

	private DefaultListableBeanFactory beanFactory;
	
	@Autowired
	private ConfigCenter config;
	
	@Override
	public void setBeanFactory(BeanFactory factory) throws BeansException {
		this.beanFactory = (DefaultListableBeanFactory) factory;
		Map<String, DBServer> indexMap = config.getDBServerMap();
		for(Entry<String,DBServer> entry : indexMap.entrySet()) {
			String dbkey = entry.getKey();
			DBServer dbServer = entry.getValue();
			BeanDefinitionBuilder dataSourceBuilder = BeanDefinitionBuilder.genericBeanDefinition(DataSourceTemplate.class);
			dataSourceBuilder.addPropertyValue(DataSourceTemplate.DBENTRY_NAME, dbServer);
			// 注册
			beanFactory.registerBeanDefinition(dbkey, dataSourceBuilder.getBeanDefinition());
		}
	}

}
