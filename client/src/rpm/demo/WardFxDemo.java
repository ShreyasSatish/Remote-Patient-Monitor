package rpm.demo;
import rpm.telemetry.TelemetryService;
import rpm.telemetry.WardTelemetryAdapter;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import rpm.domain.PatientId;
import rpm.domain.alarm.*;
import rpm.simulation.PatientEventType;
import rpm.simulation.PatientVitalsRow;
import rpm.simulation.WardManager;
import rpm.ui.fx.EcgCanvas;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.geometry.Insets;

import rpm.domain.report.InMemoryPatientDataStore;
import rpm.domain.report.ReportGenerator;
import rpm.domain.report.PatientReport;
import rpm.domain.report.VitalSummary;

import java.time.Instant;
import java.util.List;

public class WardFxDemo extends Application {
    private static final double DT_SECONDS = 0.04;

    @Override
    public void start(Stage stage) {
        WardManager ward = new WardManager(8);

        //----START TELEMETRY UPLOAD SERVICE-----
        WardTelemetryAdapter adapter =
                new WardTelemetryAdapter(ward);

        TelemetryService telemetryService =
                new TelemetryService(adapter);

        telemetryService.start();
//-----------------------------------------------

        Instant[] simTime = {Instant.now()};
        double[] sinceVitalsUpdate = {0.0};

        AlarmEngine engine = new AlarmEngine(AlarmConfig.defaultAdult());
        AlarmService alarmService = new AlarmService(engine);
        alarmService.addListener(new ConsoleAlarmListener());
        ward.addListener(alarmService);


        Label alarmBanner = new Label("Status: OK");
        alarmBanner.setMinHeight(30);
        alarmBanner.setStyle("-fx-font-weight: bold; -fx-padding: 5;");

        alarmService.addListener(new AlarmListener() {
            @Override
            public void onAlarmTransition(AlarmTransition t) {
                // ignore
            }

            @Override
            public void onAlarmState(PatientId id, Instant time, AlarmState state) {
                if (!id.equals(ward.getSelectedPatientId())) return;
                // for the actual UI. currently just console

                javafx.application.Platform.runLater(() -> {
                    alarmBanner.setText("Status: " + state.getOverall());

                    Color c;
                    switch (state.getOverall()) {
                        case GREEN:
                            c = Color.LIGHTGREEN;
                            break;
                        case AMBER:
                            c = Color.GOLD;
                            break;
                        case RED:
                            c = Color.ORANGERED;
                            break;
                        default:
                            c = Color.LIGHTGRAY;
                    }


                    alarmBanner.setBackground(new Background(
                            new BackgroundFill(c, CornerRadii.EMPTY, Insets.EMPTY)
                    ));
                });
            }
        });

        // to generate the report
        // store 5 minutes so "last 1 minute" always has data, but doesnt store crazy large amoutns of data for now
        InMemoryPatientDataStore store = new InMemoryPatientDataStore(java.time.Duration.ofMinutes(5));
        ward.addListener(store);
        alarmService.addListener(store);

        ReportGenerator reportGen = new ReportGenerator();
        Button reportBtn = new Button("Report (last 1 min)");
        reportBtn.setOnAction(e -> {
            PatientId id = ward.getSelectedPatientId();
            Instant to = simTime[0];
            Instant from = to.minusSeconds(60);

            PatientReport r = reportGen.generate(id, from, to, store);

            System.out.println("==== REPORT " + id.getDisplayName() + " ====");
            System.out.println("Window: " + from + " → " + to);

            for (var entry : r.getSummaries().entrySet()) {
                VitalSummary vs = entry.getValue();
                System.out.printf(
                        "%s: n=%d mean=%.2f min=%.2f max=%.2f%n",
                        entry.getKey(), vs.getN(), vs.getMean(), vs.getMin(), vs.getMax()
                );
            }

            if (!r.getAlarmTransitions().isEmpty()) {
                System.out.println("Alarm transitions:");
                r.getAlarmTransitions().forEach(t ->
                        System.out.printf("  %s %s %s→%s (%s)%n",
                                t.getTime(), t.getVitalType(), t.getFrom(), t.getTo(), t.getReason())
                );
            } else {
                System.out.println("No alarm transitions in window.");
            }
        });

        EcgCanvas ecg = new EcgCanvas();

        ObservableList<PatientId> ids = FXCollections.observableArrayList(ward.getPatientIds());

        ComboBox<PatientId> selector = new ComboBox<>(ids);
        selector.getSelectionModel().select(new PatientId(1));

        selector.setOnAction(e -> {
            PatientId id = selector.getSelectionModel().getSelectedItem();
            if (id != null) {
                ward.setSelectedPatientId(id);
                ecg.reset();
            }
        });

        Button addBtn = new Button("Add patient");
        addBtn.setOnAction(e -> {
            PatientId added = ward.addPatient();
            ids.setAll(ward.getPatientIds());
            selector.getSelectionModel().select(added);
            ward.setSelectedPatientId(added);
            ecg.reset();
        });

        Button removeBtn = new Button("Remove selected (>=09 only)");
        removeBtn.setOnAction(e -> {
            PatientId selected = selector.getSelectionModel().getSelectedItem();
            if (selected == null) return;

            boolean ok = ward.removePatient(selected);
            if (ok) {
                ids.setAll(ward.getPatientIds());
                PatientId bed1 = new PatientId(1);
                selector.getSelectionModel().select(bed1);
                ward.setSelectedPatientId(bed1);
                ecg.reset();
            }
        });

        Button feverBtn = new Button("Force Fever");
        feverBtn.setOnAction(e -> triggerSelected(ward, selector, PatientEventType.FEVER_SPIKE, simTime[0]));

        Button tachyBtn = new Button("Force Tachy");
        tachyBtn.setOnAction(e -> triggerSelected(ward, selector, PatientEventType.TACHY_EPISODE, simTime[0]));

        Button respBtn = new Button("Force Resp Distress");
        respBtn.setOnAction(e -> triggerSelected(ward, selector, PatientEventType.RESP_DISTRESS, simTime[0]));

        Button bpSpikeBtn = new Button("Force BP Spike");
        bpSpikeBtn.setOnAction(e -> triggerSelected(ward, selector, PatientEventType.BP_SPIKE, simTime[0]));

        Button bpDropBtn = new Button("Force BP Drop");
        bpDropBtn.setOnAction(e -> triggerSelected(ward, selector, PatientEventType.BP_DROP, simTime[0]));

        Button hfBtn = new Button("Force HF Decomp");
        hfBtn.setOnAction(e -> triggerSelected(ward, selector, PatientEventType.HEART_FAILURE_DECOMP, simTime[0]));

        // added report generator button here -sb

        HBox topBar = new HBox(10, selector, addBtn, removeBtn, reportBtn);
        HBox eventBar = new HBox(10, feverBtn, tachyBtn, respBtn, bpSpikeBtn, bpDropBtn, hfBtn);
        VBox topArea = new VBox(10, alarmBanner, topBar, eventBar);

        TableView<PatientVitalsRow> table = new TableView<>();
        table.setItems(FXCollections.observableArrayList());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<PatientVitalsRow, String> bedCol = new TableColumn<>("Bed");
        bedCol.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getPatientId().getDisplayName())
        );

        TableColumn<PatientVitalsRow, String> labelCol = new TableColumn<>("Patient");
        labelCol.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getLabel())
        );

        TableColumn<PatientVitalsRow, String> hrCol = new TableColumn<>("HR");
        hrCol.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(String.format("%.1f", c.getValue().getHr()))
        );

        TableColumn<PatientVitalsRow, String> rrCol = new TableColumn<>("RR");
        rrCol.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(String.format("%.1f", c.getValue().getRr()))
        );

        TableColumn<PatientVitalsRow, String> bpCol = new TableColumn<>("BP");
        bpCol.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(String.format("%.0f/%.0f", c.getValue().getSys(), c.getValue().getDia()))
        );

        TableColumn<PatientVitalsRow, String> tempCol = new TableColumn<>("Temp");
        tempCol.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(String.format("%.2f", c.getValue().getTemp()))
        );

        table.getColumns().addAll(bedCol, labelCol, hrCol, rrCol, bpCol, tempCol);

        BorderPane root = new BorderPane();
        root.setTop(topArea);
        root.setCenter(ecg);
        root.setBottom(table);

        Scene scene = new Scene(root, 1000, 760);
        stage.setScene(scene);
        stage.setTitle("Ward FX Demo");
        stage.show();

        ecg.widthProperty().bind(root.widthProperty());
        ecg.heightProperty().bind(root.heightProperty().multiply(0.55));

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(40), e -> {
            simTime[0] = simTime[0].plusMillis(40);
            ward.tick(simTime[0], DT_SECONDS);

            ecg.appendSamples(ward.getSelectedPatientLastEcgSegment());

            sinceVitalsUpdate[0] += DT_SECONDS;
            if (sinceVitalsUpdate[0] >= 1.0) {
                sinceVitalsUpdate[0] = 0.0;
                updateTable(table, ward.getLatestVitalsTable());
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private static void triggerSelected(WardManager ward,
                                        ComboBox<PatientId> selector,
                                        PatientEventType type,
                                        Instant simTime) {
        PatientId selected = selector.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        ward.triggerEvent(selected, type, simTime);
    }

    private static void updateTable(TableView<PatientVitalsRow> table, List<PatientVitalsRow> rows) {
        table.getItems().setAll(rows);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
