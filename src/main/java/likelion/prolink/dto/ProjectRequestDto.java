package likelion.prolink.dto;

import likelion.prolink.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ProjectRequestDto {
    private String projectName;
    private String title;
    private String grade;
    private Boolean isRecruit;
    private Boolean isActive;
    private int contributorNum;
    private Category category;
    private String recruitmentPosition;
    private String content;
    private LocalDateTime end;
}
