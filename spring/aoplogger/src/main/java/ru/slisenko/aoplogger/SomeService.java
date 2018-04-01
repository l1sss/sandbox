package ru.slisenko.aoplogger;

import org.springframework.stereotype.Component;

@Component("service")
public class SomeService {

    @Loggable
    public void method1() {
        //... some work
    }

    public void method2() {
        //... some work
    }
}
