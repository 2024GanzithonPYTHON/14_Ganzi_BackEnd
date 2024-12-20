package likelion.prolink.domain.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor
public class ProjectResponse {
    private Long id;
    private Long userId;
    private String nickName;
    private String projectName;
    private String title;
    private String grade;
    private Boolean isRecruit;
    private Boolean isActive;
    private Long contributorNum;
    private String category;
    private String recruitmentPosition;
    private String content;
    private LocalDate deadLine;
    private String link;
    private String successLink;

    public ProjectResponse(Long id, String nickName, Long userId, String projectName, String title, String grade, Boolean isRecruit, Boolean isActive, Long contributorNum, String category, String recruitmentPosition, String content, LocalDate deadLine, String link, String successLink) {
        this.id = id;
        this.nickName = nickName;
        this.userId = userId;
        this.projectName = projectName;
        this.title = title;
        this.grade = grade;
        this.isRecruit = isRecruit;
        this.isActive = isActive;
        this.contributorNum = contributorNum;
        this.category = category;
        this.recruitmentPosition = recruitmentPosition;
        this.content = content;
        this.deadLine = deadLine;
        this.link = link;
        this.successLink = successLink;
    }
}
