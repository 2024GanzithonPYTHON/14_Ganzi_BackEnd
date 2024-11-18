package likelion.prolink.repository;

import likelion.prolink.domain.LikeCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeCategoryRepository extends JpaRepository<LikeCategory, Long> {
    List<LikeCategory> findByUserId(Long userId);
    void deleteAllByUserId(Long userId);
}
