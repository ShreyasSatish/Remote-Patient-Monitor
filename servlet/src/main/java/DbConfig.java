public final class DbConfig {
    public final String url;
    public final String user;
    public final String password;

    public DbConfig(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public static DbConfig fromEnv() {
        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String pw = System.getenv("DB_PASSWORD");
        if (url == null || url.isBlank()) return null;
        return new DbConfig(url, user, pw);
    }
}
