package ru.slisenko.camel.example;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
    public static void main(final String[] args) throws Exception {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("camel-context.xml");
        // Keep main thread alive for some time to let application finish processing the data files.
        Thread.sleep(5000);
    }
}
