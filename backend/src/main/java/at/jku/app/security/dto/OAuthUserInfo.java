package at.jku.app.security.dto;

import at.jku.app.security.data.AuthProvider;

public record OAuthUserInfo(
		AuthProvider provider,
		String providerUserId,
		String email,
		String name
) {}
