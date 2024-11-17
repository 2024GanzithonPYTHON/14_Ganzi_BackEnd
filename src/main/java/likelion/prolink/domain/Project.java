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
@Table(name = "Project")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //private Long userId;
    private String projectName;
    private String title;
    private String grade;
    private Boolean isRecruit;
    private Boolean isActive;
    private int contributorNum;

    @Enumerated(EnumType.STRING)
    private Category category;
    private String recruitmentPosition;

    @Lob
    private String content;
    private LocalDateTime end;
}
