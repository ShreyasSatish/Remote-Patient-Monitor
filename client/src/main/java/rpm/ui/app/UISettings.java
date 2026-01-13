package rpm.ui.app;

public final class UISettings {


    private int patientsPerScreen = 4;

    private boolean rotationEnabled = true;
    private int rotationSeconds = 5;


    private AlertPreference alertPreference = AlertPreference.VISUAL_ONLY;
    private AlertDuration alertDuration = AlertDuration.SEC_10;

    public UISettings() {}

    public int getPatientsPerScreen() {
        return patientsPerScreen;
    }

    public void setPatientsPerScreen(int n) {
        patientsPerScreen = clamp(n, 1, 16);
    }

    public boolean isRotationEnabled() {
        return rotationEnabled;
    }

    public void setRotationEnabled(boolean enabled) {
        rotationEnabled = enabled;
    }

    public int getRotationSeconds() {
        return rotationSeconds;
    }

    public void setRotationSeconds(int secs) {
        rotationSeconds = clamp(secs, 3, 120);
    }

    public AlertPreference getAlertPreference() {
        return alertPreference;
    }

    public void setAlertPreference(AlertPreference pref) {
        if (pref != null) alertPreference = pref;
    }

    public AlertDuration getAlertDuration() {
        return alertDuration;
    }

    public void setAlertDuration(AlertDuration d) {
        if (d != null) alertDuration = d;
    }

    public void resetDefaults() {
        patientsPerScreen = 4;
        rotationEnabled = true;
        rotationSeconds = 5;
        alertPreference = AlertPreference.VISUAL_ONLY;
        alertDuration = AlertDuration.UNTIL_RESOLVED;
    }

    private static int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(hi, v));
    }
}
