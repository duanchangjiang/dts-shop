package com.qiguliuxing.dts.admin.config;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qiguliuxing.dts.core.util.ResponseUtil;

@ControllerAdvice
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class ShiroExceptionHandler {

	@ExceptionHandler(AuthenticationException.class)
	@ResponseBody
	public Object unauthenticatedHandler(AuthenticationException e) {
		e.printStackTrace();
		return ResponseUtil.unlogin();
	}

	@ExceptionHandler(AuthorizationException.class)
	@ResponseBody
	public Object unauthorizedHandler(AuthorizationException e) {
		e.printStackTrace();
		return ResponseUtil.unauthz();
	}

}
