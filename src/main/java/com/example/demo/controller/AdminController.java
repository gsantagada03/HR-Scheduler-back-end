package com.example.demo.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.security.UserDetailService;
import com.example.demo.service.AdminService;
import com.example.demo.webtoken.JwtService;
import com.example.demo.webtoken.LoginForm;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {


	@Autowired
	private AdminService adminService;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtService jwtService;
	@Autowired
	private UserDetailService userDetailService;

	@PostMapping("/authenticate")
	public ResponseEntity<?> authenticateAndGetToken(@RequestBody LoginForm loginForm) {
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginForm.username(), loginForm.password()));
		if(authentication.isAuthenticated()) {
			if(adminService.isFirstLogin()) {
				String token = jwtService.generateToken(userDetailService.loadUserByUsername(loginForm.username()));
				return ResponseEntity.ok(Map.of("redirect", "/change-password", "token", token));

			}else {
				String token = jwtService.generateToken(userDetailService.loadUserByUsername(loginForm.username()));
				return ResponseEntity.ok(Collections.singletonMap("token", token));
			}
		}else {
			throw new UsernameNotFoundException("Invalid credentials");
		}
	}

	@GetMapping("/admin/home")
	public String adminHome() {
		return "Ciao a tutti";
	}

	@PutMapping("/admin/change-password")
	public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request){
		String newPassword = request.get("password");
		if(adminService.changePassword(newPassword)) {
			return ResponseEntity.ok(Collections.singletonMap("redirect", "/home"));
		}else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Errore nel cambio password");
		}
	}

}
