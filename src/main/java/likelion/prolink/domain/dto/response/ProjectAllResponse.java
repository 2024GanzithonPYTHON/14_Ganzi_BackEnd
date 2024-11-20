package likelion.prolink.domain.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Getter @Setter
@NoArgsConstructor
public class ProjectAllResponse {
    private Long projectId;
    private String nickName;
    private String projectName;
    private String title;
    private Boolean isRecruit;
    private Long contributorNum;
    private Long isAcceptedNum;
    private String category;
    private String recruitmentPosition;
    private String content;
    private LocalDate deadLine;

    public ProjectAllResponse(Long projectId, String nickName, String projectName, String title, Boolean isRecruit, Long contributorNum,Long isAcceptedNum, String category, String recruitmentPosition, String content, LocalDate deadLine) {
        this.projectId = projectId;
        this.nickName = nickName;
        this.projectName = projectName;
        this.title = title;
        this.isRecruit = isRecruit;
        this.contributorNum = contributorNum;
        this.isAcceptedNum = isAcceptedNum;
        this.category = category;
        this.recruitmentPosition = recruitmentPosition;
        this.content = content;
        this.deadLine = deadLine;
    }
}
