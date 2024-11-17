package likelion.prolink.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterDTO {

    private String loginId;
    private String password;

    private String userName;
    private String nickName;
    private String number;
    private String category;
}
