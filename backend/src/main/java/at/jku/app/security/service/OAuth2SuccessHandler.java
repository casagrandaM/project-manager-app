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

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
	
	private final AuthService authService;
	private final OAuth2AdditionalDataService additionalDataService;
	private final OAuth2AuthorizedClientService authorizedClientService;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
		
		OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
		OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
		AuthProvider provider = AuthProvider.valueOf(token.getAuthorizedClientRegistrationId().toUpperCase());
		System.out.println(oauthUser.getAttributes());
		OAuthUserInfo userInfo = createUserInfo(provider, token, oauthUser);
		
		String jwt = authService.loginWithOAuth(userInfo).token();
		
		response.sendRedirect("http://localhost:4200/oauth/callback?token=" + jwt);
	}
	
	private OAuthUserInfo createUserInfo(AuthProvider provider, OAuth2AuthenticationToken token, OAuth2User oauthUser) {
		return switch (provider) {
			case GOOGLE -> {
				String email = oauthUser.getAttribute("email");
				String name = oauthUser.getAttribute("name");
				
				yield new OAuthUserInfo(
						provider,
						oauthUser.getName(),
						email,
						name
				);
			}
			
			case GITHUB -> {
				OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(token.getAuthorizedClientRegistrationId(), token.getName());
				
				String accessToken = client.getAccessToken().getTokenValue();
				String email = additionalDataService.getGithubPrimaryEmail(accessToken);
				String name = oauthUser.getAttribute("name");
				
				if (name == null || name.isBlank()) {
					name = oauthUser.getAttribute("login");
				}
				
				yield new OAuthUserInfo(
						provider,
						oauthUser.getName(),
						email,
						name
				);
			}
		};
	}
}