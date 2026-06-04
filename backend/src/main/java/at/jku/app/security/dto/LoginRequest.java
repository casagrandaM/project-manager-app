package at.jku.app.security.dto;

public record LoginRequest(
		String email,
		String password
) {}
