package gov.usgs.aqcu.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
@Component
public class PerformanceAspect {
	private Logger log = LoggerFactory.getLogger("aqcu-ext-report.logging");
    
	@Around("@annotation(LogReportExecutionTime)")
    public Object logReportExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    	
        final long start = System.currentTimeMillis();

        final Object proceed = joinPoint.proceed();

        final long executionTime = System.currentTimeMillis() - start;

        log.info(joinPoint.getThis() + " executed in " + executionTime + "ms");

        return proceed;
    }

}