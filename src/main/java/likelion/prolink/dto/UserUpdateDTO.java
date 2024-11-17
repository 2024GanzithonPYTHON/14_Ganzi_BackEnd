package likelion.prolink.dto;

import lombok.Getter;
import lombok.Setter;

// 개인정보 수정
@Getter
@Setter
public class UserUpdateDTO {
    private String nickName;
    private String category;
}
