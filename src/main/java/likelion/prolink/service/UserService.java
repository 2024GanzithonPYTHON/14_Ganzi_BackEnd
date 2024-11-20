package likelion.prolink.service;

import com.sun.jdi.request.DuplicateRequestException;
import likelion.prolink.domain.CustomUserDetails;
import likelion.prolink.domain.LikeCategory;
import likelion.prolink.domain.User;
import likelion.prolink.domain.dto.request.CheckRequest;
import likelion.prolink.domain.dto.request.PasswordRequest;
import likelion.prolink.domain.dto.response.UserResponse;
import likelion.prolink.domain.dto.request.UserUpdateRequest;

import likelion.prolink.repository.LikeCategoryRepository;
import likelion.prolink.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final GlobalSevice globalSevice;
    private final LikeCategoryRepository likeCategoryRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserResponse getUserInfo(CustomUserDetails customUserDetails){
        User user = globalSevice.findUser(customUserDetails);

        List<String> categoryList = likeCategoryRepository.findByUserId(customUserDetails.getId())
                .stream().map(LikeCategory::getCategory).collect(Collectors.toList());

        return new UserResponse(
                user.getId(), user.getLoginId(),
                user.getUserName(), user.getNickName(),
                user.getNumber(), categoryList,
                user.getPoint());
    }

    @Transactional
    public void deleteUser(CustomUserDetails customUserDetails, PasswordRequest passwordRequest){
        User user = globalSevice.findUser(customUserDetails);
        if (!passwordEncoder.matches(passwordRequest.getPassword(), user.getPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        };

        userRepository.deleteById(customUserDetails.getId());
    }

    @Transactional
    public UserResponse updateUser(CustomUserDetails customUserDetails, UserUpdateRequest userUpdateRequest, String nickName){
        User user = globalSevice.findUser(customUserDetails);
        if (!user.getNickName().equals(nickName)) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        if (userRepository.findByNickName(userUpdateRequest.getNickName()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        user.setNickName(userUpdateRequest.getNickName());
        userRepository.save(user);

        likeCategoryRepository.deleteAllByUserId(user.getId());

        List<LikeCategory> categories = userUpdateRequest.getCategory().stream().map(cat -> new LikeCategory(user, cat)).collect(Collectors.toList());
        likeCategoryRepository.saveAll(categories);

        List<String> categoryList = categories.stream().map(LikeCategory::getCategory).collect(Collectors.toList());

        return new UserResponse(
                user.getId(), user.getLoginId(),
                user.getUserName(), user.getNickName(),
                user.getNumber(), categoryList,
                user.getPoint());
    }

    public void checkName(CheckRequest checkRequest){
        Optional<User> user = userRepository.findByNickName(checkRequest.getSentence());

        if(user.isPresent()){
            throw new DuplicateRequestException("이미 존재하는 닉네임입니다.");
        }
    }

    public void checkId(CheckRequest checkRequest){
        Optional<User> user = userRepository.findByLoginId(checkRequest.getSentence());

        if(user.isPresent()){
            throw new DuplicateRequestException("이미 존재하는 아이디입니다.");
        }
    }

}


