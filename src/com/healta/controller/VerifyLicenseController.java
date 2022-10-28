package com.healta.controller;

import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;

public class VerifyLicenseController extends Controller {
	public void index() {
		render("license_invalid.html");
	}
	
	@ActionKey("/login")
	public void login(){
		renderJsp("license_invalid.html");
	}
}
