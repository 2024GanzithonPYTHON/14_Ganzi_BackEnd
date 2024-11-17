package likelion.prolink.dto;

import likelion.prolink.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RecruitDto {
    private Long id;
    private String projectName;
    private String title;
    private int contributorNum;
    private Boolean isRecruit;
    private Boolean isActive;
    private Category category;
    private LocalDateTime end;
    //private String nickname;
}
