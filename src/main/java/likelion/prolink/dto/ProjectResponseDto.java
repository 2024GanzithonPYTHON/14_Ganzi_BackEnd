package likelion.prolink.dto;

import likelion.prolink.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ProjectResponseDto {
    private Long id;
    private String projectName;
    private String title;
    private String grade;
    private Boolean recruit;
    private Boolean active;
    private int contributorNum;
    private Category category;
    private String content;
    private String recruitmentPosition;
    private LocalDateTime end;
}
