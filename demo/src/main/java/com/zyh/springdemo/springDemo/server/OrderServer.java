package com.zyh.springdemo.springDemo.server;


import com.zyh.springdemo.springDemo.zyhspring.Autowired;
import com.zyh.springdemo.springDemo.zyhspring.BeanNameAware;
import com.zyh.springdemo.springDemo.zyhspring.Component;
import com.zyh.springdemo.springDemo.zyhspring.Scope;

/**
 * <p></p>
 *
 * @author : zyh
 **/
@Component
@Scope(value = "prototype")
public class OrderServer implements OrderInterface, BeanNameAware {

    @Autowired
    private MemberServer memberServer;

    private String beanName;

    @Override
    public void getOut() {
        System.out.println("---bean--name"+beanName);
    }

    @Override
    public void setBeanName(String name) {
        beanName = name;
    }
}
