package likelion.prolink.domain;

public enum Authority {
    LEADER("팀장"),
    MEMBER("팀원");
    private final String description;

    Authority(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
