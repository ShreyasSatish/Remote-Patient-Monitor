package rpm.ui.dashboard;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import rpm.ui.bindings.VitalDisplay;

public final class PatientCardView extends VBox {

    // ---------------------------
    // UI labels
    // ---------------------------
    private final Label title = new Label();
    private final Label hr = new Label();
    private final Label rr = new Label();
    private final Label bp = new Label();
    private final Label temp = new Label();

    // ---------------------------
    // Resolve button
    // ---------------------------
    private final Button resolveBtn = new Button("Resolve");
    private Runnable onResolve = () -> {};

    // ---------------------------
    // Alert flashing
    // ---------------------------
    private Timeline flasher;
    private boolean flashOn = false;
    private boolean hovered = false;
    private boolean alerting = false;

    // ---------------------------
    // Constructors
    // ---------------------------

    /** ✅ No-arg constructor (keeps compatibility) */
    public PatientCardView() {
        buildUi();
    }

    /** ✅ Model constructor (used by grid) */
    public PatientCardView(PatientTileModel model) {
        buildUi();
        setModel(model);
    }

    // ---------------------------
    // UI setup
    // ---------------------------
    private void buildUi() {
        setSpacing(8);
        setPadding(new Insets(12));
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        // Spacer pushes resolve button down
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        resolveBtn.setVisible(false);
        resolveBtn.setOnAction(e -> onResolve.run());

        StackPane resolveBox = new StackPane(resolveBtn);
        resolveBox.setAlignment(Pos.BOTTOM_RIGHT);

        getChildren().addAll(
                title,
                hr,
                rr,
                bp,
                temp,
                spacer,
                resolveBox
        );

        // Hover tracking
        setOnMouseEntered(e -> { hovered = true; applyStyle(); });
        setOnMouseExited(e -> { hovered = false; applyStyle(); });

        applyStyle();
    }

    // ---------------------------
    // Public API
    // ---------------------------

    public void setModel(PatientTileModel t) {
        if (t == null) return;

        title.setText(t.displayName + "  (" + t.id.getDisplayName() + ")");
        hr.setText("HR: " + VitalDisplay.fmt1(t.hr) + " bpm");
        rr.setText("RR: " + VitalDisplay.fmt1(t.rr) + " br/min");
        bp.setText("BP: " + VitalDisplay.fmt0(t.sys) + "/" + VitalDisplay.fmt0(t.dia) + " mmHg");
        temp.setText("Temp: " + VitalDisplay.fmt1(t.temp) + " °C");

        setAlerting(t.alerting);
        resolveBtn.setVisible(t.showResolve);
    }

    public void setOnResolve(Runnable r) {
        this.onResolve = (r != null) ? r : () -> {};
    }

    // ---------------------------
    // Alert flashing
    // ---------------------------

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

    // ---------------------------
    // Styling
    // ---------------------------

    private void applyStyle() {
        if (alerting) {
            setStyle(flashOn ? alertStyle() : baseStyle());
        } else {
            setStyle(hovered ? hoverStyle() : baseStyle());
        }
    }

    private static String baseStyle() {
        return "-fx-border-color: #cccccc;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-background-color: white;";
    }

    private static String hoverStyle() {
        return "-fx-border-color: #cccccc;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-background-color: #f5f5f5;";
    }

    private static String alertStyle() {
        return "-fx-border-color: red;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-background-color: #ffcccc;";
    }
}
