package likelion.prolink.controller;

import com.sun.jdi.request.DuplicateRequestException;
import likelion.prolink.domain.dto.request.SignupRequest;
import likelion.prolink.domain.dto.response.UserResponse;
import likelion.prolink.repository.UserRepository;
import likelion.prolink.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepository;

    @GetMapping("/")
    public String home() {
        return "Welcome to the homepage!";
    }

    @PostMapping("/signup")
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
}

