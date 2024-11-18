package likelion.prolink.service;


import com.sun.jdi.request.DuplicateRequestException;
import likelion.prolink.domain.LikeCategory;
import likelion.prolink.domain.User;
import likelion.prolink.domain.dto.request.SignupRequest;
import likelion.prolink.domain.dto.response.UserResponse;
import likelion.prolink.repository.LikeCategoryRepository;
import likelion.prolink.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final LikeCategoryRepository likeCategoryRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    // 회원가입 처리
    public UserResponse joinProcess(SignupRequest signupRequest) {
        if (userRepository.findByLoginId(signupRequest.getLoginId()).isPresent()) {
            throw new DuplicateRequestException("이미 존재하는 유저입니다.");
        }
            User user = new User();
            user.setLoginId(signupRequest.getLoginId());
            user.setPassword(bCryptPasswordEncoder.encode(signupRequest.getPassword()));
            user.setUserName(signupRequest.getUserName());
            user.setNickName(signupRequest.getNickName());
            user.setNumber(signupRequest.getNumber());
            user.setPoint(10L);
            userRepository.save(user);

            List<LikeCategory> categories = signupRequest.getCategory().stream().map(cat -> new LikeCategory(user, cat)).collect(Collectors.toList());
            likeCategoryRepository.saveAll(categories);

            List<String> categoryList = categories.stream().map(LikeCategory::getCategory).collect(Collectors.toList());
            return new UserResponse(
                    user.getId(), user.getLoginId(),
                    user.getUserName(), user.getNickName(),
                    user.getNumber(), categoryList,
                    user.getPoint());
    }

}

