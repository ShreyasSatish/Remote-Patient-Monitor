package rpm.ui.app;

public final class Session {
    private NurseUser currentUser;

    public boolean isLoggedIn() { return currentUser != null; }
    public NurseUser getUser() { return currentUser; }
    public void setUser(NurseUser user) { this.currentUser = user; }
    public void clear() { this.currentUser = null; }
}
