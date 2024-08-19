package com.example.demo.controller;

import java.util.Collections;

import java.util.Map;

import org.antlr.v4.runtime.misc.TestRig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.HR;
import com.example.demo.security.UserDetailService;
import com.example.demo.service.AdminService;
import com.example.demo.webtoken.JwtService;
import com.example.demo.webtoken.LoginForm;

import io.micrometer.common.lang.Nullable;

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
				return ResponseEntity.ok(Map.of("redirect", "/cambia-password", "token", token));

			}else {
				String token = jwtService.generateToken(userDetailService.loadUserByUsername(loginForm.username()));
				return ResponseEntity.ok(Collections.singletonMap("token", token));
			}
		}else {
			throw new UsernameNotFoundException("Invalid credentials");
		}
	}
	
	@PostMapping(value = "/admin/create-HR", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> createHR(
	    @RequestPart("name") String name,
	    @RequestPart("surname") String surname,
	    @RequestPart("username") String username,
	    @RequestPart("password") String password,
	    @RequestPart("phone")@Nullable String phone,
	    @RequestPart("image")@Nullable MultipartFile file) throws Exception {

	    HR hr = new HR();
	    hr.setFirstName(name);
	    hr.setLastName(surname);
	    hr.setUsername(username);
	    hr.setPassword(password);
	    hr.setPhoneNumber(phone);
	    
	    if(adminService.HrExistsByUsername(username)) {
		   return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("usernameExistsError", "Username già esistente"));
	    }
	    
	    if(adminService.HrExistsByPhoneNumber(phone)) {
			   return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("phoneExistsError", "Numero  già esistente"));
		    }
	    
	    if(file != null && !file.isEmpty()) {
	    	String filePath = adminService.saveImage(file);
	    	hr.setImagePath(filePath);
	    }

	    if (adminService.registerHR(hr)) {
	        return ResponseEntity.ok(Collections.singletonMap("redirect", "/home-admin"));
	    } else{
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Errore nella registrazione dell'HR manager");
	    }
	    
	}

	@PutMapping("/admin/change-password")
	public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request){
		String newPassword = request.get("password");
		if(adminService.changePassword(newPassword)) {
			return ResponseEntity.ok(Collections.singletonMap("redirect", "/home-admin"));
		}else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Errore nel cambio password");
		}
	}
	
}
