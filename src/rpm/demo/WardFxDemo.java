package rpm.demo;

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
import javafx.stage.Stage;
import javafx.util.Duration;
import rpm.domain.PatientId;
import rpm.simulation.PatientVitalsRow;
import rpm.simulation.WardManager;
import rpm.ui.fx.EcgCanvas;

import java.time.Instant;
import java.util.List;

public class WardFxDemo extends Application {
    private static final double DT_SECONDS = 0.04;

    @Override
    public void start(Stage stage) {
        WardManager ward = new WardManager(8);
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

        HBox topBar = new HBox(10, selector, addBtn, removeBtn);

        TableView<PatientVitalsRow> table = new TableView<>();
        table.setItems(FXCollections.observableArrayList());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<PatientVitalsRow, String> bedCol = new TableColumn<>("Bed");
        bedCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getPatientId().getDisplayName()));

        TableColumn<PatientVitalsRow, String> hrCol = new TableColumn<>("HR");
        hrCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(String.format("%.1f", c.getValue().getHr())));

        TableColumn<PatientVitalsRow, String> rrCol = new TableColumn<>("RR");
        rrCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(String.format("%.1f", c.getValue().getRr())));

        TableColumn<PatientVitalsRow, String> bpCol = new TableColumn<>("BP");
        bpCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(
                String.format("%.0f/%.0f", c.getValue().getSys(), c.getValue().getDia())
        ));

        TableColumn<PatientVitalsRow, String> tempCol = new TableColumn<>("Temp");
        tempCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(String.format("%.2f", c.getValue().getTemp())));

        table.getColumns().addAll(bedCol, hrCol, rrCol, bpCol, tempCol);

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(ecg);
        root.setBottom(table);

        Scene scene = new Scene(root, 900, 700);
        stage.setScene(scene);
        stage.setTitle("Ward FX Demo");
        stage.show();

        ecg.widthProperty().bind(root.widthProperty());
        ecg.heightProperty().bind(root.heightProperty().multiply(0.55));

        Instant[] time = {Instant.now()};
        double[] sinceVitalsUpdate = {0.0};

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(40), e -> {
            time[0] = time[0].plusMillis(40);
            ward.tick(time[0], DT_SECONDS);

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

    private static void updateTable(TableView<PatientVitalsRow> table, List<PatientVitalsRow> rows) {
        table.getItems().setAll(rows);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
