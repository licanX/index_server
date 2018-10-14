package com.index.es.component;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 按上下文获取Bean组件
 * 
 * @author lican
 * @date 2018年8月27日
 * @since v1.0.0
 */
@Component
public class SpringBeanComponent implements ApplicationContextAware {

	private static ApplicationContext ac = null;
	private static SpringBeanComponent springBeanComponent = null;

	public static SpringBeanComponent instance() {
		if (springBeanComponent == null) {
			synchronized (SpringBeanComponent.class) {
				if (springBeanComponent == null) {
					springBeanComponent = new SpringBeanComponent();
				}
			}
		}
		return springBeanComponent;
	}

	public synchronized Object getBean(String beanName) {
		return ac.getBean(beanName);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ac = applicationContext;
	}

}
