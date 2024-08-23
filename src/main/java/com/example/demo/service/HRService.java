package com.example.demo.service;

import java.awt.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Optional;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.HrSchedulerApplication;
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
		if(phoneNumber!=null && hrRepository.existsByPhoneNumber(phoneNumber)) {
			return true;
		}
		return false;
	}


	public boolean registerHR(String name, String surname, String username, String password, @Nullable String phone, @Nullable MultipartFile image) throws Exception {
		final String usernameRegex = "^[A-Za-z][A-Za-z0-9_]{7,29}$";
		final String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
		String phoneRegex = "^(\\+39)?\\s?3\\d{2}\\s?\\d{6,7}$";

		
		if (HrExistsByUsername(username)) {
			throw new IllegalArgumentException("Username already exists");
		}

		if (phone != null && HrExistsByPhoneNumber(phone)) {
			throw new IllegalArgumentException("Phone number already exists");
		}else if(phone != null && !phone.matches(phoneRegex)) {
			throw new IllegalArgumentException("Invalid phone number");

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
		saveProfilePicture(hr, image);
		hrRepository.save(hr);

		return true;
	}


	private byte[] saveProfilePicture(HR hr, MultipartFile image) throws IOException {
	    String directory = "src/main/resources/static/profile images/";

	    if (image != null && !image.isEmpty()) {
	        String fileName = image.getOriginalFilename();
	        File file = new File(directory + fileName);

	        try (FileOutputStream outputStream = new FileOutputStream(file)) {
	            outputStream.write(image.getBytes());
	        }
	        hr.setProfilePicture(image.getBytes());
	        hr.setImageType(image.getContentType());
	        return image.getBytes();

	    } else {
	        try (InputStream defaultImageStream = getClass().getResourceAsStream("/static/profile images/img_avatar.png")) {
	            byte[] defaultImageBytes = defaultImageStream.readAllBytes();
	            hr.setProfilePicture(defaultImageBytes);
	            hr.setImageType("png");
	            return defaultImageBytes;
	        }
	    }
	}
	
	public boolean deleteHrAccount(String username) {
		if(username != null && hrRepository.existsByUsername(username)) {
			Optional<HR> hrObj = hrRepository.findByUsername(username);
			HR hrToDelete = hrObj.get();
			hrRepository.delete(hrToDelete);
			return true;
		}
		return false;
	}

}

