package rpm.ui.dashboard;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import rpm.ui.bindings.VitalDisplay;

public final class PatientCardView extends VBox {

    private final Label title = new Label();
    private final Label hr = new Label();
    private final Label rr = new Label();
    private final Label bp = new Label();
    private final Label temp = new Label();

    private Timeline flasher;
    private boolean flashOn = false;

    // track hover separately so it doesn't fight alert styling
    private boolean hovered = false;
    private boolean alerting = false;

    public PatientCardView(PatientTileModel t) {
        setSpacing(8);
        setPadding(new Insets(12));

        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        getChildren().addAll(title, hr, rr, bp, temp);

        setModel(t);

        setOnMouseEntered(e -> {
            hovered = true;
            applyStyle();
        });
        setOnMouseExited(e -> {
            hovered = false;
            applyStyle();
        });
    }

    public void setModel(PatientTileModel t) {
        title.setText(t.displayName + "  (" + t.id.getDisplayName() + ")");
        hr.setText("HR: " + VitalDisplay.fmt1(t.hr) + " bpm");
        rr.setText("RR: " + VitalDisplay.fmt1(t.rr) + " br/min");
        bp.setText("BP: " + VitalDisplay.fmt0(t.sys) + "/" + VitalDisplay.fmt0(t.dia) + " mmHg");
        temp.setText("Temp: " + VitalDisplay.fmt1(t.temp) + " °C");

        setAlerting(t.alerting);
    }

    public void setAlerting(boolean on) {
        if (this.alerting == on) return;
        this.alerting = on;

        if (alerting) startFlash();
        else stopFlash();

        applyStyle();
    }

    private void startFlash() {
        if (flasher != null) return;
        flasher = new Timeline(new KeyFrame(Duration.millis(400), e -> {
            flashOn = !flashOn;
            applyStyle();
        }));
        flasher.setCycleCount(Timeline.INDEFINITE);
        flasher.play();
    }

    private void stopFlash() {
        if (flasher != null) {
            flasher.stop();
            flasher = null;
        }
        flashOn = false;
    }

    private void applyStyle() {
        // priority: alert flashing overrides hover background
        if (alerting) {
            setStyle(flashOn ? alertStyle() : baseStyle());
            return;
        }
        setStyle(hovered ? hoverStyle() : baseStyle());
    }

    private static String baseStyle() {
        return "-fx-border-color: #cccccc; -fx-border-radius: 10; -fx-background-radius: 10;";
    }

    private static String hoverStyle() {
        return "-fx-border-color: #cccccc; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: #f5f5f5;";
    }

    private static String alertStyle() {
        return "-fx-border-color: red; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: #ffcccc;";
    }
}
