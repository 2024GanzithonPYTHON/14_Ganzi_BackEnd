package likelion.prolink.controller;

import likelion.prolink.dto.UserDTO;
import likelion.prolink.dto.UserUpdateDTO;
import likelion.prolink.service.RegisterService;
import likelion.prolink.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

// 유저 정보 가져오기, 수정하기
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final RegisterService registerService;

    public UserController(UserService userService, RegisterService registerService) {
        this.userService = userService;
        this.registerService = registerService;
    }

    // 유저 정보 가져오기
    @GetMapping("/{loginId}")
    public ResponseEntity<UserDTO> getUserInfo(@PathVariable String loginId) {
        UserDTO userDTO = userService.getUserInfo(loginId);
        if (userDTO != null) {
            return ResponseEntity.ok(userDTO); // 유저가 있으면 200 OK와 함께 반환
        } else {
            return ResponseEntity.status(404).body(null); // 유저가 없으면 404 반환
        }
    }

    // 닉네임 중복 확인
    @GetMapping("/check-nickname")
    public ResponseEntity<String> checkNickname(@RequestParam String nickName) {
        if (registerService.isNickNameExist(nickName)) {
            return ResponseEntity.status(409).body("Nickname already exists");
        }
        return ResponseEntity.ok("Nickname is available");
    }

    // 유저 정보 수정 (nickName, category만 수정)
    @PutMapping("/{loginId}/update")
    public ResponseEntity<UserDTO> updateUserInfo(@PathVariable String loginId,
                                                  @RequestBody UserUpdateDTO userUpdateDTO,
                                                  @AuthenticationPrincipal UserDetails userDetails){

        // 기존 닉네임과 수정하려는 닉네임이 다르면 중복 확인
        if (!userUpdateDTO.getNickName().equals(userDetails.getUsername())) {
            // 닉네임 중복 확인
            if (registerService.isNickNameExist(userUpdateDTO.getNickName())) {
                return ResponseEntity.status(409).body(null); // 닉네임 중복
            }
        }

        // 유저 정보 수정 처리
        UserDTO updatedUser = userService.updateUserInfo(loginId, userUpdateDTO);

        if (updatedUser != null) {
            // 수정된 유저 정보를 반환 (200 OK)
            return ResponseEntity.ok(updatedUser);
        } else {
            // 유저가 존재하지 않으면 404 Not Found 반환
            return ResponseEntity.notFound().build();
        }
    }
}
