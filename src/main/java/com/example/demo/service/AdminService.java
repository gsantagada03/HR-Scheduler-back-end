package com.example.demo.service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;
import java.nio.file.Path;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.Admin;
import com.example.demo.model.HR;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.HRRepository;


@Service
public class AdminService {

	@Autowired
	private AdminRepository adminRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private HRRepository hrRepository;

	public boolean changePassword(String newPassword) {
		final String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
		Optional<Admin> admin = adminRepository.findByUsername("admin");
		if(admin.isPresent()) {
			Admin adminObj = admin.get();
			if(newPassword.matches(passwordRegex)) {
				adminObj.setPassword(passwordEncoder.encode(newPassword));
				adminRepository.save(adminObj);
				return true;
			}
		}
		return false;
	}

	public boolean isFirstLogin() {
		Optional<Admin> admin = adminRepository.findByUsername("admin");
		if(admin.isPresent()) {
			Admin adminObj = admin.get();
			if(passwordEncoder.matches("admin", adminObj.getPassword())) { 
				return true;
			}
		}
		return false;
	}

	

}





