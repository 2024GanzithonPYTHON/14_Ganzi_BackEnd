package likelion.prolink.repository;

import likelion.prolink.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByLoginId(String loginId);
    boolean existsByNickName(String nickName);

    Optional<UserEntity> findByLoginId(String loginId);
    UserEntity findByNickName(String nickName);
}
