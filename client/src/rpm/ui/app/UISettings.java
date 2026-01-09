package rpm.ui.app;

public final class UISettings {
    private int patientsPerScreen = 2; // default for your dashboard
    private boolean rotationEnabled = false;
    private int rotationSeconds = 10;

    private AlertPreference alertPreference = AlertPreference.VISUAL_ONLY;
    private AlertDuration alertDuration = AlertDuration.UNTIL_RESOLVED;

    public int getPatientsPerScreen() { return patientsPerScreen; }
    public void setPatientsPerScreen(int n) { patientsPerScreen = clamp(n, 1, 16); }

    public boolean isRotationEnabled() { return rotationEnabled; }
    public void setRotationEnabled(boolean enabled) { rotationEnabled = enabled; }

    public int getRotationSeconds() { return rotationSeconds; }
    public void setRotationSeconds(int secs) { rotationSeconds = clamp(secs, 5, 60); }

    public AlertPreference getAlertPreference() { return alertPreference; }
    public void setAlertPreference(AlertPreference pref) { alertPreference = pref; }

    public AlertDuration getAlertDuration() { return alertDuration; }
    public void setAlertDuration(AlertDuration d) { alertDuration = d; }

    public void resetDefaults() {
        patientsPerScreen = 2;
        rotationEnabled = false;
        rotationSeconds = 10;
        alertPreference = AlertPreference.VISUAL_ONLY;
        alertDuration = AlertDuration.UNTIL_RESOLVED;
    }

    private static int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(hi, v));
    }
}
