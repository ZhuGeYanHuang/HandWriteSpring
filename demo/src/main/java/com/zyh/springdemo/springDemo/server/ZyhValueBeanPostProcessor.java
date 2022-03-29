package com.zyh.springdemo.springDemo.server;

import com.zyh.springdemo.springDemo.zyhspring.BeanPostProcessor;
import com.zyh.springdemo.springDemo.zyhspring.Component;
import com.zyh.springdemo.springDemo.zyhspring.ZyhTestValue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * <p></p>
 *
 * @author : zyh
 **/
@Component
public class ZyhValueBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {

        for (Field field : bean.getClass().getDeclaredFields()){
            if(field.isAnnotationPresent(ZyhTestValue.class)){
                field.setAccessible(true);
                try {
                    field.set(bean,field.getAnnotation(ZyhTestValue.class).value());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }
}
