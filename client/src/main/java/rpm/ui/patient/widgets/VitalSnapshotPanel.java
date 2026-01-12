package rpm.ui.patient.widgets;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import rpm.domain.PatientId;
import rpm.domain.VitalSnapshot;
import rpm.domain.VitalType;
import rpm.ui.bindings.VitalDisplay;

import java.util.Map;

public final class VitalSnapshotPanel extends VBox {

    private final Label title = new Label();
    private final Label hr = new Label();
    private final Label rr = new Label();
    private final Label bp = new Label();
    private final Label temp = new Label();

    public VitalSnapshotPanel() {
        setSpacing(8);
        setPadding(new Insets(12));
        setStyle(
                "-fx-border-color: #cccccc;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-background-color: white;"
        );


        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        getChildren().addAll(title, hr, rr, bp, temp);
    }

    public void setSnapshot(PatientId id, VitalSnapshot snap) {
        title.setText("Patient " + id.getDisplayName());

        if (snap == null) {
            hr.setText("HR: -- bpm");
            rr.setText("RR: -- br/min");
            bp.setText("BP: --/-- mmHg");
            temp.setText("Temp: -- °C");
            return;
        }

        Map<VitalType, Double> v = snap.getValues();
        double dHr = get(v, VitalType.HEART_RATE);
        double dRr = get(v, VitalType.RESP_RATE);
        double dSys = get(v, VitalType.BP_SYSTOLIC);
        double dDia = get(v, VitalType.BP_DIASTOLIC);
        double dTemp = get(v, VitalType.TEMPERATURE);

        hr.setText("HR: " + VitalDisplay.fmt1(dHr) + " bpm");
        rr.setText("RR: " + VitalDisplay.fmt1(dRr) + " br/min");
        bp.setText("BP: " + VitalDisplay.fmt0(dSys) + "/" + VitalDisplay.fmt0(dDia) + " mmHg");
        temp.setText("Temp: " + VitalDisplay.fmt1(dTemp) + " °C");
    }

    private static double get(Map<VitalType, Double> m, VitalType t) {
        Double x = m.get(t);
        return x == null ? Double.NaN : x;
    }
}
