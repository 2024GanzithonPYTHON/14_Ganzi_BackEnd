package likelion.prolink.domain.dto.request;

import lombok.Getter;

@Getter
public class ProjectRequest {
    private String projectName;
    private String title;
    private String grade;
    private Long contributorNum;
    private String category;
    private String recruitmentPosition;
    private String Content;
    private String deadLine;
    private String link;
}
