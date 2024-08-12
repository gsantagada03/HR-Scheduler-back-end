package com.example.demo.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.model.Admin;
import com.example.demo.repository.AdminRepository;

@Configuration
public class AdminConfiguration implements CommandLineRunner{
	
	@Autowired
	private AdminRepository adminRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;  

	@Override
	public void run(String... args) throws Exception {
		if(adminRepository.count() == 0) {
			Admin admin = new Admin();
			admin.setRole("ADMIN");
			admin.setUsername("admin");
			admin.setPassword(passwordEncoder.encode("admin"));
		 
			adminRepository.save(admin);
		}
	}
}
