package likelion.prolink.service;

import likelion.prolink.domain.CustomUserDetails;
import likelion.prolink.domain.User;
import likelion.prolink.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

@Service
@RequiredArgsConstructor
public class GlobalSevice {

    private final UserRepository userRepository;

    public User findUser(CustomUserDetails customUserDetails){
        User user = userRepository.findById(customUserDetails.getId())
                .orElseThrow(() -> new NotFoundException("User Not Found"));
        return user;
    }
}
