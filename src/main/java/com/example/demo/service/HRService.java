package com.example.demo.service;

import java.awt.Image;
import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.HR;
import com.example.demo.repository.HRRepository;

import io.micrometer.common.lang.Nullable;

@Service
public class HRService {


	@Autowired
	private HRRepository hrRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;


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


	public boolean registerHR(String name, String surname, String username, String password, @Nullable String phone, @Nullable MultipartFile file) throws Exception {
		final String usernameRegex = "^[A-Za-z][A-Za-z0-9_]{7,29}$";
		final String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

		if (HrExistsByUsername(username)) {
			throw new IllegalArgumentException("Username already exists");
		}

		if (phone != null && HrExistsByPhoneNumber(phone)) {
			throw new IllegalArgumentException("Phone number already exists");
		}

		if (name == null || surname == null || username == null || password == null) {
			throw new IllegalArgumentException("Missing required fields");
		}

		if (!username.matches(usernameRegex)) {
			throw new IllegalArgumentException("Invalid username format");
		}

		if (!password.matches(passwordRegex)) {
			throw new IllegalArgumentException("Invalid password format");
		}

		HR hr = new HR();
		hr.setFirstName(name);
		hr.setLastName(surname);
		hr.setUsername(username);
		hr.setPassword(passwordEncoder.encode(password));
		hr.setPhoneNumber(phone);

		byte[] imageProfile = saveProfilePicture(hr, file);
		hr.setProfilePicture(imageProfile);

		hrRepository.save(hr);
		return true;
	}


	public byte[] saveProfilePicture(HR hr, MultipartFile image) throws IOException {
		if(image != null && !image.isEmpty()) {
			byte[] imageBytes = image.getBytes();
			return imageBytes;
		}
		return null;
	}
}

