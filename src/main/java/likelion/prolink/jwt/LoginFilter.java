package likelion.prolink.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import likelion.prolink.domain.CustomUserDetails;
import likelion.prolink.domain.dto.request.LoginRequest;
import likelion.prolink.oAuth.AuthTokens;
import likelion.prolink.oAuth.JwtTokenProvider;
import likelion.prolink.oAuth.RefreshToken;
import likelion.prolink.oAuth.RefreshTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil,JwtTokenProvider jwtTokenProvider, RefreshTokenRepository refreshTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        setFilterProcessesUrl("/api/auth/login"); // 커스텀 로그인 엔드포인트 설정
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            if (request.getInputStream() == null || request.getContentLength() <= 0) {
                throw new RuntimeException("요청 본문이 비어 있습니다.");
            }
            ObjectMapper objectMapper = new ObjectMapper();
            LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
            System.out.println("Received LoginRequest: " + loginRequest);

            String loginId = loginRequest.getLoginId();
            String password = loginRequest.getPassword();

            log.info("loginId: {}, password: {}", loginId, password);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginId, password, null);
            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    ///로그인 성공시 실행하는 메소드 (여기서 JWT를 발급하면 됨)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String loginId = customUserDetails.getLoginId();

        // Generate access and refresh tokens
        String accessToken = jwtUtil.createJwt(String.valueOf(loginId));
        String refreshToken = jwtTokenProvider.generate(String.valueOf(loginId), new Date(System.currentTimeMillis() + 604800000)); // 7 days validity

        // Save refresh token in database
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByUserId(customUserDetails.getId())
                .orElse(new RefreshToken());
        refreshTokenEntity.setUserId(customUserDetails.getId());
        refreshTokenEntity.setRefreshToken(refreshToken);
        refreshTokenRepository.save(refreshTokenEntity);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Return tokens as response
        AuthTokens authTokens = AuthTokens.of(accessToken, refreshToken, "Bearer", 3600L);
        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getWriter(), authTokens);

        log.info("Login successful: {}", customUserDetails.getLoginId());
    }



    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        response.setStatus(401);
        log.info("login failed");
    }
}