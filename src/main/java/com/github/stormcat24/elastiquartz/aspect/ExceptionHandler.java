package com.github.stormcat24.elastiquartz.aspect;

import com.github.stormcat24.elastiquartz.exception.FatalException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author stormcat24
 */
@Aspect
@Component
public class ExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    @Around("execution(public * com.github.stormcat24.elastiquartz.task.*.check())")
    public Object handle(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (FatalException e) {
            logger.error("Fatal error occurs.", e);
            System.exit(1);
        } catch (Exception e){
            throw e;
        }
        return null;
    }
}
