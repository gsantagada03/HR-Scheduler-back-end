package com.example.demo.security;

import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.model.Admin;
import com.example.demo.repository.AdminRepository;


@Service
public class UserDetailService implements UserDetailsService {
	
	@Autowired
	private AdminRepository adminRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<Admin> admin = adminRepository.findByUsername(username);
		
		if(admin.isPresent()) {
			var adminObj = admin.get();
			return User.builder()
					.username(adminObj.getUsername())
					.password(adminObj.getPassword())
					.roles("ADMIN")
					.build();
		}else {
			throw new UsernameNotFoundException(username);
		}
	}

}
