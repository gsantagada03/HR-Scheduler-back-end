package com.example.demo.webtoken;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtService {

	public static final String SECRET = "64D646C3B9CF6B7FD371E1D8EE58B9104CDCBDC2C2F0240CF52A3B333E232555CEDD939E2707D5C119F277EACE5FC8FC5F08A5428AC9716E08250AF457E995F5";
	private static final long VALIDITY = TimeUnit.MINUTES.toMillis(30);
	
	public String generateToken(UserDetails userDetails) {
		return Jwts.builder()
		.subject(userDetails.getUsername())
		.issuedAt(Date.from(Instant.now()))
		.expiration(Date.from(Instant.now().plusMillis(VALIDITY)))
		.signWith(generateKey())
		.compact();
	}
	
	private SecretKey generateKey() {
		byte[] decodedKey = Base64.getDecoder().decode(SECRET);
		return Keys.hmacShaKeyFor(decodedKey);
	}
}
