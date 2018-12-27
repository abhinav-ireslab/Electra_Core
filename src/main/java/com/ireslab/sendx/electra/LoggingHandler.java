/*package com.ireslab.sendx.electra;

import java.util.Arrays;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;

@Aspect
@Component
public class LoggingHandler {

	private static final Logger log = LoggerFactory.getLogger(LoggingHandler.class);

	@Autowired
	private ObjectWriter objectWriter;

	@Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
	public void restControllerPointcut() {
	}

	@Pointcut("execution(* *.*(..))")
	protected void anyMethod() {
	}

	// Around -> Any method within resource annotated with @Controller annotation
	@Around("restControllerPointcut() &&  anyMethod() ")
	public Object loggingAroundMethod(ProceedingJoinPoint joinPoint) throws Throwable {

		long start = System.currentTimeMillis();
		try {
			String className = joinPoint.getSignature().getDeclaringTypeName();
			String methodName = joinPoint.getSignature().getName();
			Object[] args = joinPoint.getArgs();
			log.debug("Request for  " + className + "" + methodName + " ()" + getValue(args));
			Object result = joinPoint.proceed();
			long elapsedTime = System.currentTimeMillis() - start;
			log.debug("Response from  " + className + "" + methodName + " ()" + getValue(result) + " execution time : "
					+ elapsedTime + " ms");

			return result;
		} catch (IllegalArgumentException e) {
			log.error("Illegal argument " + Arrays.toString(joinPoint.getArgs()) + " in "
					+ joinPoint.getSignature().getName() + "()");
			throw e;
		}
	}

	// After -> Any method within resource annotated with @Controller annotation
	// throws an exception ...Log it
	@AfterThrowing(pointcut = "restControllerPointcut() &&  anyMethod() ", throwing = "exception")
	public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
		String className = joinPoint.getSignature().getDeclaringTypeName();
		String methodName = joinPoint.getSignature().getName();
		// Object[] args = joinPoint.getArgs();
		log.debug("Request for  " + className + "" + methodName + " ()" + " throws exception");
		log.debug("An exception has been thrown in " + joinPoint.getSignature().getName() + " ()");
		log.debug("Cause : " + ExceptionUtils.getStackTrace(exception));
	}

	private String getValue(Object result) {
		String returnValue = null;
		if (null != result) {
			try {
				returnValue = objectWriter.writeValueAsString(result);

			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				returnValue = "ERROR WHILE CONVERTING TO JSON";
			}
			
			 * if(result instanceof Object[]){ returnValue = gson. } if
			 * (result.toString().endsWith("@" + Integer.toHexString(result.hashCode()))) {
			 * returnValue = ReflectionToStringBuilder.toString(result); } else {
			 * returnValue = result.toString(); }
			 
		}
		return returnValue;
	}
}*/