package ru.javabegin.training.spring.start;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.javabegin.training.spring.impls.robot.RobotImpl;
import ru.javabegin.training.spring.interfaces.Robot;

public class Start {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("context.xml");
        Robot robot = (RobotImpl) context.getBean("robot");
        robot.action();
    }
}
