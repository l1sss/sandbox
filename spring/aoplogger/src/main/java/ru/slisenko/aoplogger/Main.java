package ru.slisenko.aoplogger;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("context.xml");
        SomeService service = (SomeService) context.getBean("service");
        service.method1();
        service.method2();
    }
}
