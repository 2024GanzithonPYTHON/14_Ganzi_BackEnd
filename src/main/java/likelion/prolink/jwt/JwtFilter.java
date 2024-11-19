package likelion.prolink.jwt;


import io.micrometer.common.lang.NonNull;
import io.micrometer.common.lang.Nullable;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import likelion.prolink.domain.User;
import likelion.prolink.domain.CustomUserDetails;
import likelion.prolink.repository.UserRepository;
import likelion.prolink.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @Nullable HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        String requestURI = request.getRequestURI();

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (requestURI.startsWith("/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.split(" ")[1];

        if (jwtUtil.isExpired(token)) {
            log.info("token expired");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Return 401 for expired tokens
            return;
        }

        try {
            String loginId = jwtUtil.getLoginId(token);
            User user = userRepository.findByLoginId(loginId)
                    .orElseThrow(() -> new RuntimeException("user not found"));

            CustomUserDetails customUserDetails = new CustomUserDetails(user);

            Authentication accessToken = new UsernamePasswordAuthenticationToken(
                    customUserDetails, null, customUserDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(accessToken);
        } catch (Exception e) {
            log.error("Token processing error: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Return 401 if user not found or token invalid
            return;
        }

        filterChain.doFilter(request, response);
    }
}
