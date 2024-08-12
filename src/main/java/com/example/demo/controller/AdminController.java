package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.security.UserDetailService;
import com.example.demo.webtoken.JwtService;
import com.example.demo.webtoken.LoginForm;

@RestController
public class AdminController {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtService jwtService;
	@Autowired
	private UserDetailService userDetailService;
	
	@PostMapping("/authenticate")
	public String authenticateAndGetToken(@RequestBody LoginForm loginForm) {
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginForm.username(), loginForm.password()));
		if(authentication.isAuthenticated()) {
			return jwtService.generateToken(userDetailService.loadUserByUsername(loginForm.username()));
		}else {
			throw new UsernameNotFoundException("Invalid credentials");
		}
	}
	
	@GetMapping("/admin/home")
	public String adminHome() {
		return "Ciao a tutti";
	}
}
