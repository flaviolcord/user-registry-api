package com.flaviolcord.user.registry.infrastructure.config;

import com.flaviolcord.user.registry.util.SensitiveDataSanitizer;
import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Aspect for logging method calls and their execution details in the application.
 * <p>
 * This class intercepts method executions within the specified package (com.flaviolcord.user.registry)
 * and logs the method's entry, arguments, result, and execution time. It also logs any exceptions thrown
 * during the method execution.
 * </p>
 * <p>
 * Sensitive data in method arguments and results are sanitized before logging to ensure privacy and security.
 * </p>
 */
@Aspect
@Component
@AllArgsConstructor
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    /**
     * Pointcut expression that matches all method executions within the
     * {@code com.flaviolcord.user.registry} package.
     * <p>
     * This pointcut is used to define where the logging aspect should be applied.
     * </p>
     */
    @Pointcut("execution(* com.flaviolcord.user.registry..*(..))")
    public void applicationPackagePointcut() {
    }

    /**
     * Around advice that intercepts method executions matched by the {@link #applicationPackagePointcut()}
     * pointcut expression. Logs the method entry, arguments, result, and execution time. Also logs exceptions
     * if they are thrown.
     * <p>
     * The method execution is wrapped around to log the entry and exit times as well as any exceptions thrown
     * during execution. The result is also sanitized before being logged.
     * </p>
     *
     * @param joinPoint the join point that provides access to method details and arguments
     * @return the result of the method execution
     * @throws Throwable if the method execution throws an exception, it will be rethrown after logging
     */
    @Around("applicationPackagePointcut()")
    public Object logAroundMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        logger.debug("Entering method: {} with arguments: {}",
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

        logger.debug("Exiting method: {} with result: {} (Execution time: {} ms)",
                joinPoint.getSignature(),
                SensitiveDataSanitizer.sanitizeArg(result),
                duration
        );

        return result;
    }
}