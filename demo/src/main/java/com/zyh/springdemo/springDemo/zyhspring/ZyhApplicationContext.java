package com.zyh.springdemo.springDemo.zyhspring;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>初始bean类</p>
 *
 * @author : zyh
 **/
public class ZyhApplicationContext {

    Class aClass;

    Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();

    Map<String, Object> singletonObj = new ConcurrentHashMap<String, Object>();

    List<BeanPostProcessor> postProcessors = new ArrayList<>(20);

    public ZyhApplicationContext(Class appconfigClass) {
        this.aClass = appconfigClass;

        scan(aClass);

        //创建bean
        for (Map.Entry<String, BeanDefinition> enpty : beanDefinitionMap.entrySet()) {
            String beanName = enpty.getKey();
            BeanDefinition value = enpty.getValue();
            if ("singleton".equals(value)) {
                //创建bean
                Object bean = createBean(beanName, value);
                singletonObj.put(beanName, bean);
            }
        }
    }

    private Object createBean(String beanName, BeanDefinition value) {
        Class type = value.getType();
        Object bean = null;
        try {
            bean = type.getConstructor().newInstance();
            //注入属性
            for (Field field : type.getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    //反射字段，关闭安全检查
                    field.setAccessible(true);
                    //
                    field.set(bean, getBean(field.getName()));
                }
            }
            //bean初始化
            if (bean instanceof InitializingBean) {
                ((InitializingBean) bean).afterPropertiesSet();
            }
            //方法回调
            if (bean instanceof BeanNameAware) {
                ((BeanNameAware) bean).setBeanName(beanName);
            }
            //前置处理
            for (BeanPostProcessor postProcessor :
                    postProcessors) {
                bean = postProcessor.postProcessBeforeInitialization(bean, beanName);
            }
            //后置处理
            for (BeanPostProcessor postProcessor :
                    postProcessors) {
                bean = postProcessor.postProcessAfterInitialization(bean, beanName);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return bean;
    }


    //获取bean
    public Object getBean(String beanName) {

        BeanDefinition definition = beanDefinitionMap.get(beanName);

        if (definition == null) {
            throw new RuntimeException("no bean in map");
        }

        if (definition.getScope().equals("prototype")) {
            return createBean(beanName, definition);
        } else {
            Object obejct = singletonObj.get(beanName);
            if (obejct == null) {
                obejct = createBean(beanName, definition);
            }
            return obejct;
        }
    }

    private void scan(Class aClass) {
        if (aClass.isAnnotationPresent(ComponentScan.class)) {//获取需要扫描的类
            ComponentScan componentScan = (ComponentScan) aClass.getAnnotation(ComponentScan.class);
            String value = componentScan.value();

            //将扫描路径替换成文件路径
            String path = value.replace(".", "/");
            System.out.println(path);

            //获取当前类的appload
            ClassLoader classLoader = ZyhApplicationContext.class.getClassLoader();
            //获取资源
            URL resource = classLoader.getResource(path);
            File files = new File(resource.getFile());
            //判断是否是文件夹
            if (files.isDirectory()) {
                for (File file : files.listFiles()) {
                    //获取路径
                    String absolutePath = file.getAbsolutePath();
                    System.out.println(absolutePath);
                    //获取文件类名
                    absolutePath = absolutePath.substring(absolutePath.indexOf("com"), absolutePath.indexOf(".class"));
                    //替换文件字符
                    absolutePath = absolutePath.replace("\\", ".");
                    //加载类
                    try {
                        Class<?> loadClass = classLoader.loadClass(absolutePath);
                        //判断需要spring管理的类
                        if (loadClass.isAnnotationPresent(Component.class)) {
                            //获取beanName
                            Component component = loadClass.getAnnotation(Component.class);
                            String beanName = component.value();
                            if ("".equals(beanName)) {
                                beanName = Introspector.decapitalize(loadClass.getSimpleName());
                            }


                            //创建后置处理器
                            if (BeanPostProcessor.class.isAssignableFrom(loadClass)) {
                                BeanPostProcessor beanPostProcessor = (BeanPostProcessor) loadClass.getConstructor().newInstance();
                                postProcessors.add(beanPostProcessor);
                            }

                            //装在bean
                            BeanDefinition definition = new BeanDefinition();
                            definition.setType(loadClass);
                            //判断是否是单例
                            if (loadClass.isAnnotationPresent(Scope.class)) {
                                Scope scope = loadClass.getAnnotation(Scope.class);
                                String scopeValue = scope.value();
                                if ("".equals(scopeValue)) {
                                    definition.setScope("singleton");
                                } else if ("prototype".equals(scopeValue)) {
                                    definition.setScope("prototype");
                                } else {
                                    definition.setScope("singleton");
                                }
                            } else {
                                definition.setScope("singleton");
                            }
                            beanDefinitionMap.put(beanName, definition);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            }


        }
    }
}
