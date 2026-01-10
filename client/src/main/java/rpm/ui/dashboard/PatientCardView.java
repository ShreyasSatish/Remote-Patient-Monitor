package rpm.ui.dashboard;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import rpm.ui.bindings.VitalDisplay;

public final class PatientCardView extends VBox {

    private final Label title = new Label();
    private final Label hr = new Label();
    private final Label rr = new Label();
    private final Label bp = new Label();
    private final Label temp = new Label();
    private final Button resolve = new Button("Resolve");

    private Runnable onResolve = () -> {};

    public PatientCardView() {
        setSpacing(8);
        setPadding(new Insets(12));
        setMinSize(220, 140);
        setStyle(baseStyle());

        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        resolve.setVisible(false);

        resolve.setOnAction(e -> onResolve.run());

        getChildren().addAll(title, hr, rr, bp, temp, resolve);
    }

    public void setOnResolve(Runnable r) {
        this.onResolve = (r != null) ? r : () -> {};
    }

    public void setModel(PatientTileModel t, boolean showResolveButton) {
        title.setText(t.displayName + " (" + t.id.getDisplayName() + ")");
        hr.setText("HR: " + VitalDisplay.fmt1(t.hr) + " bpm");
        rr.setText("RR: " + VitalDisplay.fmt1(t.rr) + " br/min");
        bp.setText("BP: " + VitalDisplay.fmt0(t.sys) + "/" + VitalDisplay.fmt0(t.dia) + " mmHg");
        temp.setText("Temp: " + VitalDisplay.fmt1(t.temp) + " °C");

        if (t.alerting) {
            setStyle(alertStyle());
            resolve.setVisible(showResolveButton); // ONLY show when UNTIL_RESOLVED
        } else {
            setStyle(baseStyle());
            resolve.setVisible(false);
        }
    }



    private static String baseStyle() {
        return "-fx-border-color: #cccccc; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: white;";
    }

    private static String alertStyle() {
        return "-fx-border-color: #a94442; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: #c85c5c;";
    }
}
