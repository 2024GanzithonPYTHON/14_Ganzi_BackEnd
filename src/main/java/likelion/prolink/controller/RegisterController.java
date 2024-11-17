package likelion.prolink.controller;


import likelion.prolink.dto.RegisterDTO;
import likelion.prolink.service.RegisterService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class RegisterController {

    private final RegisterService registerService;

    public RegisterController(RegisterService registerService) {

        this.registerService = registerService;
    }

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<String> RegisterProcess(@RequestBody RegisterDTO registerDTO){
        // 아이디, 닉네임 중복 체크
        if (registerService.isLoginIdExist(registerDTO.getLoginId())) {
            return ResponseEntity.status(409).body("아이디가 존재합니다. 다른 아이디를 사용해주세요.");
        }

        if (registerService.isNickNameExist(registerDTO.getNickName())) {
            return ResponseEntity.status(409).body("닉네임이 존재합니다. 다른 닉네임을 사용해주세요.");
        }

        // 회원가입 성공
        boolean success = registerService.registerProcess(registerDTO);
        if (success) {
            return ResponseEntity.status(200).body("회원가입에 성공하셨습니다.");
        } else {
            return ResponseEntity.status(400).body("회원가입에 실패하셨습니다.");
        }
    }
}
