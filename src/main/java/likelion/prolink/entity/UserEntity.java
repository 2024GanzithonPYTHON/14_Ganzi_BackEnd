package likelion.prolink.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String loginId;
    private String password;

    private String userName;
    private String nickName;
    private String number;
    private String category;

    // 디폴트 값
    private Long credit = 50L;
    private Long temper = 50L;
}
