package com.zyh.springdemo.springDemo.server;

import com.zyh.springdemo.springDemo.zyhspring.BeanPostProcessor;
import com.zyh.springdemo.springDemo.zyhspring.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * <p></p>
 *
 * @author : zyh
 **/
@Component
public class ZyhBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {

        if ("orderServer".equals(beanName)) {
            //获取代理类
            Object proxyInstans = Proxy.newProxyInstance(bean.getClass().getClassLoader(), bean.getClass().getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                    System.out.println("jdk动态代理");

                    return method.invoke(proxy, args);
                }
            });
        }
        return bean;
    }
}
