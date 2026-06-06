package at.jku.app.security.dto;

public record GithubEmailDto(
		String email,
		boolean primary,
		boolean verified
) {}
