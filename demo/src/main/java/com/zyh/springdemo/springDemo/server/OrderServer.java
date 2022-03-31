package com.zyh.springdemo.springDemo.server;


import com.zyh.springdemo.springDemo.zyhspring.*;

/**
 * <p></p>
 *
 * @author : zyh
 **/
@Component
@Scope(value = "prototype")
public class OrderServer implements OrderInterface, BeanNameAware, InitializingBean {

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

    @Override
    public void afterPropertiesSet() {
        System.out.println("----q--");
    }
}
