package org.jack.common.util.net;

public class AuthenticatePair {
	private final String username;
	private final String password;
	public AuthenticatePair(String username,String password) {
		this.username=username;
		this.password=password;
	}
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
}
