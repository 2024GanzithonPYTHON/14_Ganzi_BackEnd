package likelion.prolink.dto;

import likelion.prolink.entity.UserEntity;
import lombok.Getter;
import lombok.Setter;

// 유저 정보
@Getter
@Setter
public class UserDTO {

    private Long id;
    private String userName;
    private String nickName;
    private String number;
    private String category;
    private Long credit;
    private Long temper;

    // 기존 생성자
    public UserDTO(Long id, String userName, String nickName, String number, String category, Long credit, Long temper) {
        this.id = id;
        this.userName = userName;
        this.nickName = nickName;
        this.number = number;
        this.category = category;
        this.credit = credit;
        this.temper = temper;
    }

    // UserEntity 객체를 받아서 DTO로 변환하는 생성자
    public UserDTO(UserEntity userEntity) {
        this.id = userEntity.getId();
        this.userName = userEntity.getUserName();
        this.nickName = userEntity.getNickName();
        this.number = userEntity.getNumber();
        this.category = userEntity.getCategory();
        this.credit = userEntity.getCredit();
        this.temper = userEntity.getTemper();
    }
}

