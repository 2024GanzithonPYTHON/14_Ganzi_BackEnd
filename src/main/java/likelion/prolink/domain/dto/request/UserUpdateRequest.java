package likelion.prolink.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

// 개인정보 수정
@Getter
@Setter
public class UserUpdateRequest {
    private String nickName;
    private List<String> category;
}
