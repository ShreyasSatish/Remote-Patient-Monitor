package rpm.ui.alerts;

import javafx.scene.media.AudioClip;
import rpm.ui.app.AlertPreference;
import rpm.ui.app.UISettings;

public final class AudioAlertManager {

    private final UISettings settings;
    private final AudioClip clip;
    private long stopAtMs = 0;

    public AudioAlertManager(UISettings settings) {
        this.settings = settings;

        java.net.URL url = AudioAlertManager.class.getResource("/rpm/ui/assets/alert.wav");
        if (url == null) {
            System.err.println("[WARN] alert.wav not found at /rpm/ui/assets/alert.wav. Audio disabled.");
            this.clip = null;
            return;
        }

        this.clip = new AudioClip(url.toExternalForm());
        this.clip.setCycleCount(AudioClip.INDEFINITE);
    }


    public void startFor(long nowMs) {
        if (clip == null) return;
        if (settings.getAlertPreference() != AlertPreference.AUDIO_AND_VISUAL) return;

        java.time.Duration d = settings.getAlertDuration().toDurationOrNull();

        if (!clip.isPlaying()) clip.play();
        stopAtMs = (d == null) ? Long.MAX_VALUE : (nowMs + d.toMillis());
    }

    public void tick(long nowMs) {
        if (clip == null) return;
        if (clip.isPlaying() && nowMs >= stopAtMs) clip.stop();
    }

    public void stop() {
        if (clip == null) return;
        clip.stop();
        stopAtMs = 0;
    }

}
