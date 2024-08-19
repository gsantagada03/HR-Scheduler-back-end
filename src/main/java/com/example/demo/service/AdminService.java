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

	public boolean HrExistsByUsername(String username) {
		if(hrRepository.existsByUsername(username)) {
			return true;
		}
		return false;
	}


	public boolean HrExistsByPhoneNumber(String phoneNumber) {
		if(hrRepository.existsByPhoneNumber(phoneNumber)) {
			return true;
		}
		return false;
	}


	public boolean registerHR(HR hr) throws Exception {
		final String usernameRegex = "^[A-Za-z][A-Za-z0-9_]{7,29}$";
		final String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

		if(HrExistsByUsername(hr.getUsername())) {
			return false;
		}

		if(HrExistsByPhoneNumber(hr.getPhoneNumber())) {
			return false;
		}

		if(hr.getFirstName() != null && hr.getLastName()!= null && hr.getUsername() != null && hr.getPassword() !=null) {
			if(hr.getUsername().matches(usernameRegex) && hr.getPassword().matches(passwordRegex)) {
				hr.setPassword(passwordEncoder.encode(hr.getPassword()));
				hrRepository.save(hr);
				return true;
			}
		}
		return false;
	}

	public String saveImage( MultipartFile imageFile) throws Exception {
		String folder = "/photos";
		Path uploadPath = Paths.get(folder);

		if(!Files.exists(uploadPath)){
			Files.createDirectories(uploadPath);
		}

		String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
		Path filePath = uploadPath.resolve(fileName);
		Files.write(filePath, imageFile.getBytes());

		return fileName;
	}
}
