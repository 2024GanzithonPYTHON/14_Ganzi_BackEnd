package likelion.prolink.domain.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

// 유저 정보
@Getter
@Setter
@NoArgsConstructor
public class UserResponse {

    private Long id;
    private String loginId;
    private String userName;
    private String nickName;
    private String number;
    private List<String> category;
    private Long point;

    // 기존 생성자


    public UserResponse(Long id, String loginId, String userName, String nickName, String number, List<String> category, Long point) {
        this.id = id;
        this.loginId = loginId;
        this.userName = userName;
        this.nickName = nickName;
        this.number = number;
        this.category = category;
        this.point = point;
    }

}

