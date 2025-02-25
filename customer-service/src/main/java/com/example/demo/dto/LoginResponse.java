package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class LoginResponse {
	private String jwt;
	private long expiresIn;
	
	public String getJwt() {
		return jwt;
	}
	public void setJwt(String jwt) {
		this.jwt = jwt;
	}
	public long getExpiresIn() {
		return expiresIn;
	}
	public void setExpiresIn(long expiresIn) {
		this.expiresIn = expiresIn;
	}
	public LoginResponse(String jwt, long expiresIn) {
		super();
		this.jwt = jwt;
		this.expiresIn = expiresIn;
	}
}
