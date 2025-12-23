package rpm.ui.dashboard;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import rpm.ui.bindings.VitalDisplay;

public final class PatientCardView extends VBox {

    public PatientCardView(PatientTileModel t) {
        setSpacing(8);
        setPadding(new Insets(12));
        setStyle("-fx-border-color: #cccccc; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label title = new Label(t.displayName + "  (" + t.id.getDisplayName() + ")");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        Label hr = new Label("HR: " + VitalDisplay.fmt1(t.hr) + " bpm");
        Label rr = new Label("RR: " + VitalDisplay.fmt1(t.rr) + " br/min");
        Label bp = new Label("BP: " + VitalDisplay.fmt0(t.sys) + "/" + VitalDisplay.fmt0(t.dia) + " mmHg");
        Label temp = new Label("Temp: " + VitalDisplay.fmt1(t.temp) + " °C");

        getChildren().addAll(title, hr, rr, bp, temp);

        // hover effect
        setOnMouseEntered(e -> setStyle(getStyle() + "-fx-background-color: #f5f5f5;"));
        setOnMouseExited(e -> setStyle("-fx-border-color: #cccccc; -fx-border-radius: 10; -fx-background-radius: 10;"));
    }
}
