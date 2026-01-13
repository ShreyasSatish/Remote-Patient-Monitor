package rpm.ui.patient;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import rpm.domain.PatientId;
import rpm.domain.VitalSnapshot;
import rpm.ui.app.AppContext;
import rpm.ui.app.Router;
import rpm.ui.patient.widgets.EcgPanel;
import rpm.ui.patient.widgets.HistorySearchPanel;
import rpm.ui.patient.widgets.VitalSnapshotPanel;

public final class PatientDetailView extends BorderPane {

    private final AppContext ctx;
    private final PatientId patientId;

    private final VitalSnapshotPanel snapshotPanel;
    private final EcgPanel ecgPanel;
    private final HistorySearchPanel historyPanel;

    private final Timeline uiTick;

    public PatientDetailView(AppContext ctx, Router router, PatientId patientId) {
        this.ctx = ctx;
        this.patientId = patientId;

        snapshotPanel = new VitalSnapshotPanel();
        snapshotPanel.getStyleClass().add("panel-card");

        ecgPanel = new EcgPanel();
        ecgPanel.reset();

        historyPanel = new HistorySearchPanel(ctx, patientId);
        historyPanel.getStyleClass().add("panel-card");

        Button reportBtn = new Button("Generate Report (external website");
        reportBtn.getStyleClass().add("primary-btn");
        reportBtn.setMaxWidth(Double.MAX_VALUE);
        reportBtn.setOnAction(e -> {
            try {
                java.awt.Desktop.getDesktop().browse(new java.net.URI("https://bioeng-rancho-app.impaas.uk/"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox left = new VBox(12, snapshotPanel, reportBtn, historyPanel);
        left.getStyleClass().add("patient-sidebar");
        left.setPrefWidth(380);

        //setLeft(left);
        //setCenter(ecgPanel);

        BorderPane content = new BorderPane();
        content.getStyleClass().add("app-bg");
        content.setLeft(left);
        content.setCenter(ecgPanel);

        setCenter(content);

        // update frequently for ECG
        uiTick = new Timeline(new KeyFrame(Duration.millis(40), e -> refresh()));
        uiTick.setCycleCount(Timeline.INDEFINITE);
        uiTick.play();

        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) uiTick.stop();
        });

        refresh();
    }

    private void refresh() {
        VitalSnapshot snap = ctx.ward.getPatientLatestSnapshot(patientId);
        snapshotPanel.setSnapshot(patientId, snap);

        double[] seg = ctx.ward.getPatientLastEcgSegment(patientId);
        ecgPanel.append(seg);
    }
}
