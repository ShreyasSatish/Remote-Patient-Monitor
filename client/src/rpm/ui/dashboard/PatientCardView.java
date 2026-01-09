package rpm.ui.dashboard;

import javafx.animation.KeyFrame;              // [REMOTE] alert flashing
import javafx.animation.Timeline;             // [REMOTE] alert flashing
import javafx.geometry.Insets;
import javafx.geometry.Pos;                   // [LOCAL] resize grip placement
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;        // [LOCAL] resize grip drag
import javafx.scene.layout.Priority;          // [LOCAL] spacer + grow
import javafx.scene.layout.Region;            // [LOCAL] spacer + resize grip
import javafx.scene.layout.StackPane;         // [LOCAL] resize grip container
import javafx.scene.layout.VBox;
import javafx.util.Duration;                  // [REMOTE] alert flashing
import rpm.ui.bindings.VitalDisplay;

import java.lang.reflect.Field;               // [EDITED] make alerting optional (works even if model has no alerting)
import java.lang.reflect.Method;              // [EDITED] make alerting optional (works even if model has no alerting)

public final class PatientCardView extends VBox {

    // ---------------------------
    // Labels as fields (updatable)
    // ---------------------------
    private final Label title = new Label();  // [REMOTE]
    private final Label hr = new Label();     // [REMOTE]
    private final Label rr = new Label();     // [REMOTE]
    private final Label bp = new Label();     // [REMOTE]
    private final Label temp = new Label();   // [REMOTE]

    // ---------------------------
    // Alert flashing state
    // ---------------------------
    private Timeline flasher;                 // [REMOTE]
    private boolean flashOn = false;          // [REMOTE]
    private boolean hovered = false;          // [REMOTE]
    private boolean alerting = false;         // [REMOTE]

    // ---------------------------
    // Resize grip state
    // ---------------------------
    private final Region resizeGrip = new Region(); // [LOCAL]
    private double startX, startY, startW, startH;  // [LOCAL]

    public PatientCardView(PatientTileModel t) {
        setSpacing(8);
        setPadding(new Insets(12));

        // Sizing hints (keep cards willing to stretch in grid/cell layouts)
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); // [LOCAL]
        setMaxHeight(Double.MAX_VALUE);                 // [LOCAL]

        // Title style as in remote
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14;"); // [REMOTE]

        // Spacer pushes resize grip to bottom
        Region spacer = new Region();                 // [LOCAL]
        VBox.setVgrow(spacer, Priority.ALWAYS);       // [LOCAL]

        // Resize grip (bottom-right)
        resizeGrip.setPrefSize(16, 16);               // [LOCAL]
        resizeGrip.setMinSize(16, 16);                // [LOCAL]
        resizeGrip.setMaxSize(16, 16);                // [LOCAL]
        resizeGrip.setPickOnBounds(true);             // [LOCAL]
        resizeGrip.setStyle(
                "-fx-cursor: se-resize;" +
                        "-fx-background-radius: 4;" +
                        "-fx-background-color: rgba(0,0,0,0.10);"
        );                                            // [LOCAL]

        StackPane gripBox = new StackPane(resizeGrip); // [LOCAL]
        gripBox.setAlignment(Pos.BOTTOM_RIGHT);        // [LOCAL]
        gripBox.setPadding(new Insets(0, 2, 2, 0));    // [LOCAL]

        // Children order: labels first, then spacer, then grip
        getChildren().addAll(title, hr, rr, bp, temp, spacer, gripBox); // [EDITED combine]

        // Initial render from model
        setModel(t); // [REMOTE] (kept) + [EDITED] (optional alert extraction)

        // Hover handling that doesn't fight alert flashing
        setOnMouseEntered(e -> { hovered = true; applyStyle(); }); // [REMOTE]
        setOnMouseExited(e -> { hovered = false; applyStyle(); }); // [REMOTE]

        // Enable resizing by drag
        enableResizeByDrag(); // [LOCAL]
    }

    // ---------------------------
    // Update displayed values
    // ---------------------------
    public void setModel(PatientTileModel t) { // [REMOTE] (kept)
        title.setText(t.displayName + "  (" + t.id.getDisplayName() + ")");
        hr.setText("HR: " + VitalDisplay.fmt1(t.hr) + " bpm");
        rr.setText("RR: " + VitalDisplay.fmt1(t.rr) + " br/min");
        bp.setText("BP: " + VitalDisplay.fmt0(t.sys) + "/" + VitalDisplay.fmt0(t.dia) + " mmHg");
        temp.setText("Temp: " + VitalDisplay.fmt1(t.temp) + " °C");

        // Remote expects t.alerting; we make it optional so this file compiles either way
        setAlerting(extractAlerting(t)); // [EDITED combine]
    }

    // ---------------------------
    // Alert flashing API
    // ---------------------------
    public void setAlerting(boolean on) { // [REMOTE] (kept)
        if (this.alerting == on) return;
        this.alerting = on;

        if (alerting) startFlash();
        else stopFlash();

        applyStyle();
    }

    private void startFlash() { // [REMOTE]
        if (flasher != null) return;
        flasher = new Timeline(new KeyFrame(Duration.millis(400), e -> {
            flashOn = !flashOn;
            applyStyle();
        }));
        flasher.setCycleCount(Timeline.INDEFINITE);
        flasher.play();
    }

    private void stopFlash() { // [REMOTE]
        if (flasher != null) {
            flasher.stop();
            flasher = null;
        }
        flashOn = false;
    }

    // ---------------------------
    // Styling
    // ---------------------------
    private void applyStyle() { // [REMOTE] (kept, but uses edited style helpers)
        // priority: alert flashing overrides hover background
        if (alerting) {
            setStyle(flashOn ? alertStyle() : baseStyle());
            return;
        }
        setStyle(hovered ? hoverStyle() : baseStyle());
    }

    // [EDITED] stronger, visible border (width=2) + darker default border
    private static String baseStyle() {
        return "-fx-border-color: #555555;" +   // darker border
                "-fx-border-width: 2;" +        // thicker border
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-background-color: white;";
    }

    // [EDITED] keep same stronger border on hover
    private static String hoverStyle() {
        return "-fx-border-color: #555555;" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-background-color: #f5f5f5;";
    }

    // [EDITED] keep alert border thick too
    private static String alertStyle() {
        return "-fx-border-color: red;" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-background-color: #ffcccc;";
    }

    // ---------------------------
    // Resize support
    // ---------------------------
    private void enableResizeByDrag() { // [LOCAL]
        resizeGrip.setOnMousePressed(e -> {
            if (e.getButton() != MouseButton.PRIMARY) return;

            startX = e.getScreenX();
            startY = e.getScreenY();
            startW = getWidth();
            startH = getHeight();
            e.consume();
        });

        resizeGrip.setOnMouseDragged(e -> {
            if (e.getButton() != MouseButton.PRIMARY) return;

            double dx = e.getScreenX() - startX;
            double dy = e.getScreenY() - startY;

            double newW = Math.max(getMinWidth(), startW + dx);
            double newH = Math.max(getMinHeight(), startH + dy);

            setPrefSize(newW, newH);
            requestLayout();
            e.consume();
        });
    }

    // ---------------------------
    // Optional alert extraction
    // ---------------------------
    private static boolean extractAlerting(PatientTileModel t) { // [EDITED]
        // Supports either:
        //  - public boolean alerting;  (field)
        //  - public boolean isAlerting(); (method)
        //  - public boolean getAlerting(); (method)
        try {
            Field f = t.getClass().getField("alerting");
            if (f.getType() == boolean.class || f.getType() == Boolean.class) {
                Object v = f.get(t);
                return (v instanceof Boolean) ? (Boolean) v : false;
            }
        } catch (Exception ignored) { }

        try {
            Method m = t.getClass().getMethod("isAlerting");
            Object v = m.invoke(t);
            return (v instanceof Boolean) ? (Boolean) v : false;
        } catch (Exception ignored) { }

        try {
            Method m = t.getClass().getMethod("getAlerting");
            Object v = m.invoke(t);
            return (v instanceof Boolean) ? (Boolean) v : false;
        } catch (Exception ignored) { }

        return false;
    }
}
