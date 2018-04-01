package ru.slisenko.aoplogger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
@Aspect
public class AopLogger {
    private final Log log = LogFactory.getLog(this.getClass());

    @Around("execution(* ru.slisenko.aoplogger.SomeService.*(..)) && @annotation(Loggable)")
    public Object logTimeMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object output = joinPoint.proceed();
        stopWatch.stop();

        StringBuilder logMessage = new StringBuilder()
                .append(joinPoint.getTarget().getClass().getName())
                .append(".")
                .append(joinPoint.getSignature().getName())
                .append("(");
        Object[] args = joinPoint.getArgs();
        for (Object arg : args)
            logMessage.append(arg).append(",");
        if (args.length > 0)
            logMessage.deleteCharAt(logMessage.length() - 1);
        logMessage.append(") ")
                .append("execution time : ")
                .append(stopWatch.getTotalTimeMillis())
                .append(" ms");
        log.info(logMessage.toString());

        return output;
    }
}
