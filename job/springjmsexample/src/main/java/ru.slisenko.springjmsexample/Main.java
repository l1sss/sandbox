package ru.slisenko.springjmsexample;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.slisenko.springjmsexample.jms.JmsMessageSender;


public class Main {

    public static void main(String[] args) {
        //System.out.println(Thread.currentThread().getName());
        ApplicationContext ctx = new ClassPathXmlApplicationContext("spring/spring-context.xml");
        JmsMessageSender jmsMessageSender = (JmsMessageSender)ctx.getBean("jmsMessageSender");
        try {
            jmsMessageSender.send();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //((ClassPathXmlApplicationContext)ctx).close();
    }
}
