package likelion.prolink.service;


import com.sun.jdi.request.DuplicateRequestException;
import likelion.prolink.domain.CustomUserDetails;
import likelion.prolink.domain.LikeCategory;
import likelion.prolink.domain.User;
import likelion.prolink.domain.dto.request.CategoryRequest;
import likelion.prolink.domain.dto.request.SignupRequest;
import likelion.prolink.domain.dto.response.UserResponse;
import likelion.prolink.oAuth.*;
import likelion.prolink.repository.LikeCategoryRepository;
import likelion.prolink.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final LikeCategoryRepository likeCategoryRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    // 회원가입 처리
    public UserResponse joinProcess(SignupRequest signupRequest) {
        if (userRepository.findByLoginId(signupRequest.getLoginId()).isPresent() || userRepository.findByNickName(signupRequest.getNickName()).isPresent()) {
            throw new DuplicateRequestException("이미 존재하는 아이디 혹은 닉네임입니다.");
        }

        User user = new User();
        user.setLoginId(signupRequest.getLoginId());
        user.setPassword(bCryptPasswordEncoder.encode(signupRequest.getPassword()));
        user.setUserName(signupRequest.getUserName());
        user.setNickName(signupRequest.getNickName());
        user.setNumber(signupRequest.getNumber());
        user.setPoint(15L);
        userRepository.save(user);

        List<String> categories = Collections.emptyList();

        return new UserResponse(
                user.getId(), user.getLoginId(),
                user.getUserName(), user.getNickName(),
                user.getNumber(), categories,
                user.getPoint());

    }

    public AuthTokens refresh(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        String loginId = String.valueOf(jwtTokenProvider.extractSubject(refreshToken));

        // Verify refresh token exists in database
        RefreshToken savedRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token not found"));

        // Generate new tokens
        String newAccessToken = jwtTokenProvider.generate(
                String.valueOf(loginId), new Date(System.currentTimeMillis() + 3600000)); // 1 hour validity
        String newRefreshToken = jwtTokenProvider.generate(
                String.valueOf(loginId), new Date(System.currentTimeMillis() + 604800000)); // 7 days validity

        // Update refresh token in database
        savedRefreshToken.setRefreshToken(newRefreshToken);
        refreshTokenRepository.save(savedRefreshToken);

        // Return new tokens
        AuthTokens authTokens = AuthTokens.of(newAccessToken, newRefreshToken, "Bearer", 3600L);
        return authTokens;
    }

    @Transactional
    public void logout(CustomUserDetails customUserDetails) {
        refreshTokenRepository.deleteByUserId(customUserDetails.getId());
    }

}

