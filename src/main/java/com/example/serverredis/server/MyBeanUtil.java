package com.example.serverredis.server;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class MyBeanUtil implements ApplicationContextAware {
    protected static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext app) throws BeansException {
        if(applicationContext == null){
            applicationContext = app;
        }
    }

    public static <T> T getBean(Class <T> cla){
        return applicationContext.getBean(cla);
    }
}
