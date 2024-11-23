package likelion.prolink.controller;

import com.sun.jdi.request.DuplicateRequestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import likelion.prolink.domain.CustomUserDetails;
import likelion.prolink.domain.dto.request.SignupRequest;
import likelion.prolink.domain.dto.response.UserResponse;
import likelion.prolink.oAuth.AuthTokens;
import likelion.prolink.oAuth.RefreshTokenRequest;
import likelion.prolink.repository.UserRepository;
import likelion.prolink.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "회원가입 API")
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepository;

    @GetMapping("/")
    @Operation(summary = "배포 테스트용 API", description = "테스트용 입니다.")
    public String home() {
        return "Welcome to the homepage!";
    }

    @PostMapping("/auth/signup")
    @Operation(summary = "회원가입 API")
    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest) {
        try {
            UserResponse userResponse = authService.joinProcess(signupRequest);
            return ResponseEntity.ok(userResponse);
        }catch (DuplicateRequestException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("아이디 혹은 닉네임 중복 검사를 해주세요.");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원가입에 실패했습니다.");
        }
    }
    @PostMapping("auth/refresh")
    @Operation(summary = "토큰 재발급 API")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        AuthTokens tokens = authService.refresh(refreshTokenRequest);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃 API")
    public ResponseEntity<?> logout(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        authService.logout(customUserDetails);
        return ResponseEntity.ok("Logged out successfully");
    }
}

