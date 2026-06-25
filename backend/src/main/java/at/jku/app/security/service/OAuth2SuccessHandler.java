package at.jku.app.security.service;

import at.jku.app.security.data.AuthProvider;
import at.jku.app.security.dto.OAuthUserInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handles successful OAuth2 authentication and issues a JWT for the authenticated user.
 * <p>
 * This handler extracts provider-specific user information (Google, GitHub),
 * creates an internal user representation and delegates authentication to {@link AuthService}.
 */
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
	
	private final AuthService authService;
	private final OAuth2AdditionalDataService additionalDataService;
	private final OAuth2AuthorizedClientService authorizedClientService;

	/**
	 * Invoked after successful OAuth2 authentication.
	 * <p>
	 * Builds an {@link OAuthUserInfo}, performs login via {@link AuthService},
	 * and redirects the user with a JWT token.
	 *
	 * @param request        The HTTP request
	 * @param response       The HTTP response
	 * @param authentication The OAuth2 authentication object
	 *
	 * @throws IOException If redirection fails
	 */
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
		
		OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
		OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
		AuthProvider provider = AuthProvider.valueOf(token.getAuthorizedClientRegistrationId().toUpperCase());
		System.out.println(oauthUser.getAttributes());
		OAuthUserInfo userInfo = createUserInfo(provider, token, oauthUser);
		
		String jwt = authService.loginWithOAuth(userInfo).token();
		
		response.sendRedirect("http://localhost:4200/oauth/callback?jwt=" + jwt);
	}

	/**
	 * Creates an {@link OAuthUserInfo} based on the OAuth2 provider.
	 *
	 * @param provider  The OAuth2 provider
	 * @param token     The OAuth2 authentication token
	 * @param oAuthUser The authenticated OAuth2 user
	 * @return The extracted user information
	 */
	private OAuthUserInfo createUserInfo(AuthProvider provider, OAuth2AuthenticationToken token, OAuth2User oAuthUser) {
		return switch (provider) {
			case GOOGLE -> {
				String email = oAuthUser.getAttribute("email");
				String name = oAuthUser.getAttribute("name");
				
				yield new OAuthUserInfo(
						provider,
						oAuthUser.getName(),
						email,
						name
				);
			}
			
			case GITHUB -> {
				OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(token.getAuthorizedClientRegistrationId(), token.getName());
				
				String accessToken = client.getAccessToken().getTokenValue();
				String email = additionalDataService.getGithubPrimaryEmail(accessToken);
				String name = oAuthUser.getAttribute("name");
				
				if (name == null || name.isBlank()) {
					name = oAuthUser.getAttribute("login");
				}
				
				yield new OAuthUserInfo(
						provider,
						oAuthUser.getName(),
						email,
						name
				);
			}
		};
	}
}