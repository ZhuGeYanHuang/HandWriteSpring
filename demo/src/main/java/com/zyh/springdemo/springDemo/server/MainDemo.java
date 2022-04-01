package com.zyh.springdemo.springDemo.server;


import com.zyh.springdemo.springDemo.config.Appconfig;
import com.zyh.springdemo.springDemo.zyhspring.ZyhApplicationContext;

/**
 * <p>主类获取bean</p>
 *
 * @author : zyh
 **/
public class MainDemo {

    public static void main(String[] args) {
        ZyhApplicationContext context = new ZyhApplicationContext(Appconfig.class);

        OrderServer object = (OrderServer)context.getBean("orderServer");
        object.getOut();
        System.out.println(object);
    }
}
