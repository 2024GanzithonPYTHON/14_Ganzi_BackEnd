package likelion.prolink.domain.dto.request;

import lombok.Getter;

import java.time.LocalDate;
import java.util.Date;

@Getter
public class ProjectRequest {
    private String projectName;
    private String title;
    private String grade;
    private Long contributorNum;
    private String category;
    private String recruitmentPosition;
    private String content;
    private LocalDate deadLine;
    private String link;
}
