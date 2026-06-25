package at.jku.app.security.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT authentication filter that processes incoming requests and sets the
 * security context if a valid JWT token is provided.
 * <p>
 * This filter extracts the Bearer token from the {@code Authorization} header,
 * validates it, and authenticates the user in the Spring Security context.
 */
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
	
	private final JwtService jwtService;
	private final AppUserDetailService userDetailsService;

	/**
	 * Executes the filter logic for each request.
	 *
	 * @param request     The incoming HTTP request
	 * @param response    The HTTP response
	 * @param filterChain The filter chain for continuation
	 *
	 * @throws ServletException If a servlet error occurs
	 * @throws IOException      If an I/O error occurs
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String authHeader = request.getHeader("Authorization");
		
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}
		
		String token = authHeader.substring(7);
		String email = jwtService.extractUsername(token);
		
		if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = userDetailsService.loadUserByUsername(email);
			if (jwtService.isTokenValid(token, userDetails)) {
				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities()
				);
				
				SecurityContextHolder.getContext().setAuthentication(auth);
			}
		}
		
		filterChain.doFilter(request, response);
	}
}
