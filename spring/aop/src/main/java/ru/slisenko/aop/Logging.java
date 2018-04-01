package ru.slisenko.aop;

import org.aspectj.lang.annotation.*;

@Aspect
public class Logging {

    @Pointcut("execution(* ru.slisenko.aop.*.*(..))")
    private void selectAllMethodsAvailable() {}

    @Before("selectAllMethodsAvailable()")
    public void beforeAdvice() {
        System.out.println("Now we are going to initiate developer's profile.");
    }

    @After("selectAllMethodsAvailable()")
    public void afterAdvice() {
        System.out.println("Developer's profile has been initiated.");
    }

    @AfterReturning(pointcut = "selectAllMethodsAvailable()", returning = "someValue")
    public void afterReturningAdvice(Object someValue) {
        System.out.println("Value: " + someValue);
    }

    @AfterThrowing(pointcut = "selectAllMethodsAvailable()", throwing = "e")
    public void inCaseOfExceptionThrowAdvice(ClassCastException e) {
        System.out.println("We have an exception here: " + e.toString());
    }
}
