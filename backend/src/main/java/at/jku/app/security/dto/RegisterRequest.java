package at.jku.app.security.dto;

public record RegisterRequest(
		String name,
		String email,
		String password
) {}
