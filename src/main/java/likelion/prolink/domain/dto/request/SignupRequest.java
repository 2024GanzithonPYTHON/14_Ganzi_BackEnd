package likelion.prolink.domain.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SignupRequest {
    private String loginId;
    private String password;
    private String userName;
    private String nickName;
    private String number;
    private List<String> category;
}
