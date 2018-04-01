package ru.slisenko.aop;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Demo {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("context.xml");
        Developer developer = (Developer) context.getBean("developer");
        System.out.println(developer);
        developer.throwSomeMysticException();
    }
}
