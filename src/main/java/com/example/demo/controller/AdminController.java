package com.example.demo.controller;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.misc.TestRig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.HrSchedulerApplication;
import com.example.demo.model.HR;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.HRRepository;
import com.example.demo.security.UserDetailService;
import com.example.demo.service.AdminService;
import com.example.demo.service.HRService;
import com.example.demo.webtoken.JwtService;
import com.example.demo.webtoken.LoginForm;

import io.micrometer.common.lang.Nullable;
import org.springframework.web.bind.annotation.RequestParam;


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
	@Autowired
	private HRRepository hrRepository;
	@Autowired
	private HRService hrService;

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
			@RequestPart("phone") @Nullable String phone,
			@RequestPart("image") @Nullable MultipartFile image) throws Exception {
		
		if(hrService.HrExistsByUsername(username)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Collections.singletonMap("usernameExistsError", "Username già esistente"));}

		if(hrService.HrExistsByPhoneNumber(phone)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Collections.singletonMap("phoneExistsError", "Numero di telefono già esistente"));}

		boolean isRegistered =  hrService.registerHR(name, surname, username, password, phone, image);
	
		if (isRegistered) {
		    return ResponseEntity.ok(Collections.singletonMap("successMessage" , "Account registrato con successo"));
		} 

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(Collections.singletonMap("error", "Errore nella registrazione dell'HR manager"));
	}
	
	@GetMapping("/admin/get-all-HRs")
	public ResponseEntity<?> getAllHrs() {
		List<HR> hrList = hrRepository.findAll();
		List <Map<String, String>> response = new ArrayList();
		for (HR hr : hrList) {
			Map<String, String> hrMap = new HashMap<>();
            String base64Image = Base64.getEncoder().encodeToString(hr.getProfilePicture());
            hrMap.put("image", base64Image);
            hrMap.put("imageType", hr.getImageType());
			hrMap.put("name", hr.getFirstName());
			hrMap.put("surname", hr.getLastName());
			hrMap.put("username", hr.getUsername());
			hrMap.put("phone", hr.getPhoneNumber());
			response.add(hrMap);
		}
		return ResponseEntity.ok(response);
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
	
	@DeleteMapping("/admin/delete-HR")
	public ResponseEntity<?> deleteHR (@RequestBody Map <String, String> request){
		String username = request.get("username");
		if(hrService.deleteHrAccount(username)) {
		    return ResponseEntity.ok(Collections.singletonMap("successMessage" , "Account eliminato con successo"));
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("errore nell'eliminazione dell'account");
	}


}
