package likelion.prolink.service;

import likelion.prolink.dto.RegisterDTO;
import likelion.prolink.entity.UserEntity;
import likelion.prolink.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public RegisterService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {

        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public boolean isLoginIdExist(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }

    public boolean isNickNameExist(String nickName) {
        return userRepository.existsByNickName(nickName);
    }

    public boolean registerProcess(RegisterDTO registerDTO) {

        // 아이디, 닉네임 중복 체크
        if (isLoginIdExist(registerDTO.getLoginId()) || isNickNameExist(registerDTO.getNickName())) {
            return false; // 아이디 또는 닉네임이 이미 존재하면 false 반환
        }

        // 회원가입 처리
        // 아이디, 비밀번호, 이름, 닉네임, 번호, 카테고리 설정
        UserEntity data = new UserEntity();

        data.setLoginId(registerDTO.getLoginId());
        data.setPassword(bCryptPasswordEncoder.encode(registerDTO.getPassword()));
        data.setUserName(registerDTO.getUserName());
        data.setNickName(registerDTO.getNickName());
        data.setNumber(registerDTO.getNumber());
        data.setCategory(registerDTO.getCategory());

        userRepository.save(data);
        return true;

    }
}
