package likelion.prolink.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApplyResponseDto {
    private Long userProjectId;
    private String careerUrl;
    private String content;
}
