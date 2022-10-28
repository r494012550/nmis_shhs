package com.healta.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

public class AuthInterceptor implements Interceptor{
	/**
	 * 防止未登录的操作
	 */
	@Override
	public void intercept(Invocation inv) {
		Object user = inv.getController().getSession().getAttribute("user");
		if(user == null) {
			inv.getController().redirect("/");
		}else {
			inv.invoke();  
		}
	}

}
