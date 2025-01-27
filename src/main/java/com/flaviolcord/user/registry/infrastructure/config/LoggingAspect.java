package com.flaviolcord.user.registry.infrastructure.config;

import com.flaviolcord.user.registry.util.SensitiveDataSanitizer;
import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
@AllArgsConstructor
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* com.flaviolcord.user.registry..*(..))")
    public void applicationPackagePointcut() {
    }

    @Around("applicationPackagePointcut()")
    public Object logAroundMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        logger.info("Entering method: {} with arguments: {}",
                joinPoint.getSignature(),
                SensitiveDataSanitizer.sanitizeArgs(joinPoint.getArgs())
        );

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable ex) {
            logger.error("Exception in method: {} with cause: {}", joinPoint.getSignature(), ex.getMessage());
            throw ex;
        }

        long duration = System.currentTimeMillis() - startTime;

        logger.info("Exiting method: {} with result: {} (Execution time: {} ms)",
                joinPoint.getSignature(),
                SensitiveDataSanitizer.sanitizeArg(result),
                duration
        );

        return result;
    }
}