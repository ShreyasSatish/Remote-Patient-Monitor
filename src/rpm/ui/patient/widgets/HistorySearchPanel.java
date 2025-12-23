package rpm.ui.patient.widgets;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import rpm.domain.PatientId;
import rpm.domain.VitalSnapshot;
import rpm.domain.VitalType;
import rpm.ui.app.AppContext;
import rpm.ui.bindings.VitalDisplay;

import java.time.*;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public final class HistorySearchPanel extends VBox {

    private final DatePicker datePicker = new DatePicker(LocalDate.now());
    private final Spinner<Integer> hour = new Spinner<>(0, 23, LocalTime.now().getHour());
    private final Spinner<Integer> minute = new Spinner<>(0, 59, LocalTime.now().getMinute());
    private final Spinner<Integer> second = new Spinner<>(0, 59, LocalTime.now().getSecond());

    private final Label result = new Label("History result: (none)");

    public HistorySearchPanel(AppContext ctx, PatientId patientId) {
        setSpacing(8);
        setPadding(new Insets(12));
        setStyle("-fx-border-color: #cccccc; -fx-border-radius: 10; -fx-background-radius: 10;");

        Button fetch = new Button("Fetch snapshot");
        fetch.setOnAction(e -> {
            Instant target = toInstant();
            // For demo: find nearest snapshot from the in-memory store (within retention)
            Instant from = target.minusSeconds(120);
            Instant to = target.plusSeconds(120);
            List<VitalSnapshot> snaps = ctx.store.getVitals(patientId, from, to);

            VitalSnapshot nearest = snaps.stream()
                    .min(Comparator.comparingLong(s -> Math.abs(s.getTimestamp().toEpochMilli() - target.toEpochMilli())))
                    .orElse(null);

            if (nearest == null) {
                result.setText("History result: no data in memory window.");
            } else {
                Map<VitalType, Double> v = nearest.getValues();
                result.setText(
                        "At ~" + nearest.getTimestamp() +
                                " | HR " + VitalDisplay.fmt1(get(v, VitalType.HEART_RATE)) +
                                " | RR " + VitalDisplay.fmt1(get(v, VitalType.RESP_RATE)) +
                                " | BP " + VitalDisplay.fmt0(get(v, VitalType.BP_SYSTOLIC)) + "/" + VitalDisplay.fmt0(get(v, VitalType.BP_DIASTOLIC)) +
                                " | Temp " + VitalDisplay.fmt1(get(v, VitalType.TEMPERATURE))
                );
            }
        });

        getChildren().addAll(
                new Label("History lookup (date/time):"),
                datePicker,
                new Label("Time (H:M:S):"),
                hour, minute, second,
                fetch,
                result
        );
    }

    private Instant toInstant() {
        LocalDate d = datePicker.getValue();
        LocalTime t = LocalTime.of(hour.getValue(), minute.getValue(), second.getValue());
        // Use system default timezone for demo
        return ZonedDateTime.of(d, t, ZoneId.systemDefault()).toInstant();
    }

    private static double get(Map<VitalType, Double> m, VitalType t) {
        Double x = m.get(t);
        return x == null ? Double.NaN : x;
    }
}
