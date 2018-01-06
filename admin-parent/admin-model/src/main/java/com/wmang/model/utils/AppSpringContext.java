package com.wmang.model.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

public class AppSpringContext implements ApplicationContextAware,InitializingBean{
	
	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext appContext) throws BeansException {
		AppSpringContext.applicationContext = appContext;
	}
	
	/** 
     * @return ApplicationContext 
     */  
    public static ApplicationContext getApplicationContext() {  
        return applicationContext;  
    }  
	/**
	 * 获取bean对象
	 * @param clazz
	 * @return
	 */
	 
	public static <T> T getBean(Class<T> clazz){
		Assert.notNull(applicationContext, "Application Context not initialize");
		return applicationContext.getBean(clazz);
	}
	/**
	 * 获取 bean 对象
	 * @param beanName
	 * @param clazz
	 * @return
	 */
	public static <T> T getBean(String beanName,Class<T> clazz){
		Assert.notNull(applicationContext, "Application Context not initialize");
		return applicationContext.getBean(beanName, clazz);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	 /** 
     * 获取对象 
     * 这里重写了bean方法，起主要作用 
     * @param name 
     * @return Object 一个以所给名字注册的bean的实例 
     * @throws BeansException 
     */  
    public static Object getBean(String name) throws BeansException {  
        return applicationContext.getBean(name);  
    }  



}
