package likelion.prolink.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //private Long userId;
    private Long projectId;
    @Enumerated(EnumType.STRING)
    private Authority authority;
    private String careerUrl;
    @Lob
    private String content;
    private Boolean isAccepted;
    private LocalDateTime joinDate;
}
