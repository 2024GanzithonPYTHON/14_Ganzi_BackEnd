package likelion.prolink.service;

import likelion.prolink.dto.UserDTO;
import likelion.prolink.dto.UserUpdateDTO;
import likelion.prolink.entity.UserEntity;
import likelion.prolink.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // loginId로 유저 정보 조회
    public UserDTO getUserInfo(String loginId) {
        Optional<UserEntity> userEntityOptional = userRepository.findByLoginId(loginId);

        if (userEntityOptional.isPresent()) {
            UserEntity userEntity = userEntityOptional.get();

            // 필요한 정보만 담은 UserDTO 생성
            return new UserDTO(
                    userEntity.getId(),
                    userEntity.getUserName(),
                    userEntity.getNickName(),
                    userEntity.getNumber(),
                    userEntity.getCategory(),
                    userEntity.getCredit(),
                    userEntity.getTemper()
            );
        }

        // 유저가 존재하지 않으면 null 반환 (혹은 예외 처리)
        return null;
    }

    // 유저 정보 수정 (nickName, category만 수정)
    public UserDTO updateUserInfo(String loginId, UserUpdateDTO userUpdateDTO) {
        // 로그인 ID로 유저 조회
        Optional<UserEntity> userEntityOptional = userRepository.findByLoginId(loginId);

        // 유저가 존재하지 않으면 null 반환
        if (!userEntityOptional.isPresent()) {
            return null;
        }

        UserEntity userEntity = userEntityOptional.get();

        // 유저 정보 수정
        if (userUpdateDTO.getNickName() != null) {
            userEntity.setNickName(userUpdateDTO.getNickName());
        }
        if (userUpdateDTO.getCategory() != null) {
            userEntity.setCategory(userUpdateDTO.getCategory());
        }

        // DB에 수정된 정보 저장
        userRepository.save(userEntity);

        // 수정된 정보를 DTO로 변환하여 반환
        return new UserDTO(userEntity);
    }

}
