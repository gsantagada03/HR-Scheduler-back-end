package com.example.demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.model.Admin;
import com.example.demo.repository.AdminRepository;

@Service
public class AdminService {

	@Autowired
	AdminRepository adminRepository;
	@Autowired
	PasswordEncoder passwordEncoder;

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
