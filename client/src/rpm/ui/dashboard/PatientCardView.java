package rpm.ui.dashboard;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;

public final class PatientCardView extends VBox {

    private final boolean alert;

    public PatientCardView(PatientTileModel t) {
        this.alert = t.alerting;

        setSpacing(8);
        setPadding(new Insets(12));
        applyStyle(false);

        // ... create labels (same as you already do)

        setOnMouseEntered(e -> applyStyle(true));
        setOnMouseExited(e -> applyStyle(false));
    }

    private void applyStyle(boolean hover) {
        if (alert) {
            setStyle(alertStyle());
        } else if (hover) {
            setStyle(hoverStyle());
        } else {
            setStyle(baseStyle());
        }
    }

    private static String baseStyle() { return "-fx-border-color: #cccccc; -fx-border-radius: 10; -fx-background-radius: 10;"; }
    private static String hoverStyle() { return baseStyle() + "-fx-background-color: #f5f5f5;"; }
    private static String alertStyle() { return "-fx-border-color: red; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: #cc0000;"; }
}
