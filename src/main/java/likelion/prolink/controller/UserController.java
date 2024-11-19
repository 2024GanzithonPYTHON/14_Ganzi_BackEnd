package likelion.prolink.controller;

import com.sun.jdi.request.DuplicateRequestException;
import likelion.prolink.domain.CustomUserDetails;
import likelion.prolink.domain.dto.request.CheckRequest;
import likelion.prolink.domain.dto.request.PasswordRequest;
import likelion.prolink.domain.dto.response.UserResponse;
import likelion.prolink.domain.dto.request.UserUpdateRequest;
import likelion.prolink.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@CrossOrigin("*")
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;



    // 유저 정보 가져오기
    @GetMapping()
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        try {
            UserResponse userResponse = userService.getUserInfo(customUserDetails);
            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 회원 탈퇴
    @DeleteMapping("/delete-account")
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                        @RequestBody PasswordRequest passwordRequest) {
        try {
            userService.deleteUser(customUserDetails,passwordRequest);
            return ResponseEntity.ok("Account deleted successfully");
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/{nickName}/update")
    public ResponseEntity<?> updateUser(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                   @RequestBody UserUpdateRequest userUpdateRequest,
                                        @PathVariable String nickName){
        try {
            UserResponse userResponse = userService.updateUser(customUserDetails, userUpdateRequest, nickName);
            return ResponseEntity.ok(userResponse);
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 존재하는 닉네임입니다.");
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/check/name")
    public ResponseEntity<?> checkNickName(@RequestBody CheckRequest checkRequest){
        try {
            String nickName = userService.checkName(checkRequest);
            return ResponseEntity.ok("사용 가능한 닉네임입니다!");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 존재하는 닉네임입니다.");
        }
    }

    @PostMapping("/check/Id")
    public ResponseEntity<?> checkId(@RequestBody CheckRequest checkRequest){
        try {
            String nickName = userService.checkId(checkRequest);
            return ResponseEntity.ok("사용 가능한 아이디입니다!");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 존재하는 ID입니다.");
        }
    }

}