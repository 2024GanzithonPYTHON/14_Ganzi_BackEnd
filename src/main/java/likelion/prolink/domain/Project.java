package likelion.prolink.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity @Getter @Setter
@NoArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String projectName;
    private String title;
    private String grade;
    private Boolean isRecruit;
    private Boolean isActive;
    private Long contributorNum;
    private String category;
    private String recruitmentPosition;
    private String Content;
    private String link;
    private String deadLine;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
