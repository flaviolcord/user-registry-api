package com.flaviolcord.user.registry.infrastructure.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    // Define a pointcut for all methods in the project
    @Pointcut("execution(* com.flaviolcord.user.registry..*(..))")
    public void applicationPackagePointcut() {
    }

    @Around("applicationPackagePointcut()")
    public Object logAroundMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        logger.info("Entering method: {} with arguments: {}", joinPoint.getSignature(), joinPoint.getArgs());
        Object result;

        try {
            result = joinPoint.proceed();
        } catch (Throwable ex) {
            logger.error("Exception in method: {} with cause: {}", joinPoint.getSignature(), ex.getMessage());
            throw ex; // Re-throw the exception after logging
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.info("Exiting method: {} with result: {} (Execution time: {} ms)", joinPoint.getSignature(), result, duration);
        return result;
    }
}