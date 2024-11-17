package likelion.prolink.domain;

public enum Category {
    PLANNING_IDEA("기획/아이디어"),
    ADVERTISING_MARKETING("광고/마케팅"),
    DESIGN("디자인"),
    WEB_MOBILE_IT("웹/모바일/IT"),
    GAME_SOFTWARE("게임/소프트웨어"),
    EMPLOYMENT_STARTUP("취업/창업");

    private final String description;

    Category(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
