package gssato.wins_pool.models;

public class Draft {

    private int draftId;

    private User user;

    private Year year;

    private DraftPick draftPick;

    private Team team;

    public int getDraftId() {
        return draftId;
    }

    public void setDraftId(int draftId) {
        this.draftId = draftId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Year getYear() {
        return year;
    }

    public void setYear(Year year) {
        this.year = year;
    }

    public DraftPick getDraftPick() {
        return draftPick;
    }

    public void setDraftPick(DraftPick draftPick) {
        this.draftPick = draftPick;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
