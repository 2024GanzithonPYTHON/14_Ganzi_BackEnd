package likelion.prolink.controller;

import likelion.prolink.domain.dto.request.SignupRequest;
import likelion.prolink.domain.dto.response.UserResponse;
import likelion.prolink.repository.UserRepository;
import likelion.prolink.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest) {
        try {
            UserResponse userResponse = authService.joinProcess(signupRequest);
            return ResponseEntity.ok(userResponse);
        }catch (Exception e){
            return ResponseEntity.ok("회원가입에 실패하셨습니다.");
        }
    }
}

