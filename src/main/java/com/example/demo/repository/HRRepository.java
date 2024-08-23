package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.HR;

public interface HRRepository extends JpaRepository<HR, Long> {

	boolean existsById(Long id);
	Optional<HR> findByUsername(String username);
	boolean existsByPhoneNumber(String phoneNumber);
	boolean existsByUsername(String username);
	
}
