package com.flaviolcord.user.registry.infrastructure.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoggingAspectTest {

    @Mock
    private ProceedingJoinPoint joinPoint;

    @InjectMocks
    private LoggingAspect loggingAspect;

    @Mock
    private Signature signature;

    @Test
    void testLogAroundMethod_successfulExecution() throws Throwable {
        // Mock join point behavior
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toString()).thenReturn("methodName");

        // Mock arguments and proceed behavior
        when(joinPoint.getArgs()).thenReturn(new Object[]{"arg1", "arg2"});
        when(joinPoint.proceed()).thenReturn("result");

        // Call the aspect method
        Object result = loggingAspect.logAroundMethod(joinPoint);

        // Verify behavior
        assertEquals("result", result);
        verify(joinPoint, times(1)).proceed();
    }

    @Test
    void testLogAroundMethod_exceptionThrown() throws Throwable {
        // Mock join point behavior
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toString()).thenReturn("methodName");

        when(joinPoint.getArgs()).thenReturn(new Object[]{"arg1", "arg2"});
        when(joinPoint.proceed()).thenThrow(new RuntimeException("Test exception"));

        // Assert the exception is propagated
        assertThrows(RuntimeException.class, () -> loggingAspect.logAroundMethod(joinPoint));

        // Verify logs and method behavior
        verify(joinPoint, times(1)).proceed();
    }
}