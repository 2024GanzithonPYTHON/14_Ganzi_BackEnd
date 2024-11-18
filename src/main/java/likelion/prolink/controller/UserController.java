package likelion.prolink.controller;

import com.sun.jdi.request.DuplicateRequestException;
import likelion.prolink.domain.CustomUserDetails;
import likelion.prolink.domain.dto.request.CheckRequest;
import likelion.prolink.domain.dto.response.UserResponse;
import likelion.prolink.domain.dto.request.UserUpdateRequest;
import likelion.prolink.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@CrossOrigin("*")
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @GetMapping()
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        try {
            UserResponse userResponse = userService.getUserInfo(customUserDetails);
            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        userService.deleteUser(customUserDetails);
        return ResponseEntity.ok("Account deleted successfully");

    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                   @RequestBody UserUpdateRequest userUpdateRequest){
        try {
            UserResponse userResponse = userService.updateUser(customUserDetails, userUpdateRequest);
            return ResponseEntity.ok(userResponse);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/check-nickName")
    public ResponseEntity<?> checkNickName(@RequestBody CheckRequest checkRequest){
        try {
            String nickName = userService.checkName(checkRequest);
            return ResponseEntity.ok(nickName);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/check-loginId")
    public ResponseEntity<?> checkId(@RequestBody CheckRequest checkRequest){
        try {
            String nickName = userService.checkId(checkRequest);
            return ResponseEntity.ok(nickName);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}