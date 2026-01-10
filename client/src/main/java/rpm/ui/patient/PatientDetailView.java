package rpm.ui.patient;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.util.Duration;
import rpm.domain.PatientId;
import rpm.domain.VitalSnapshot;
import rpm.domain.report.PatientReport;
import rpm.domain.report.VitalSummary;
import rpm.ui.app.AppContext;
import rpm.ui.app.Router;
import rpm.ui.layout.AppShell;
import rpm.ui.patient.widgets.HistorySearchPanel;
import rpm.ui.patient.widgets.VitalSnapshotPanel;
import rpm.ui.patient.widgets.EcgPanel;

import java.time.Instant;
import java.util.Map;

public final class PatientDetailView extends BorderPane {

    private final AppContext ctx;
    private final Router router;
    private final PatientId patientId;

    private final VitalSnapshotPanel snapshotPanel;
    private final EcgPanel ecgPanel;
    private final HistorySearchPanel historyPanel;

    private final Timeline uiTick;

    public PatientDetailView(AppContext ctx, Router router, PatientId patientId) {
        this.ctx = ctx;
        this.router = router;
        this.patientId = patientId;

        AppShell shell = new AppShell(ctx, router);

        snapshotPanel = new VitalSnapshotPanel();
        ecgPanel = new EcgPanel();
        ecgPanel.reset();
        historyPanel = new HistorySearchPanel(ctx, patientId);

        Button reportBtn = new Button("Generate report (last 1 min)");
        reportBtn.setOnAction(e -> generateLastMinuteReport());

        VBox left = new VBox(12, snapshotPanel, reportBtn, historyPanel);
        left.setPadding(new Insets(15));
        left.setPrefWidth(380);

        BorderPane content = new BorderPane();
        content.setLeft(left);
        content.setCenter(ecgPanel);

        shell.setContent(content);
        setCenter(shell);

        // update every second - 40 ms for ecg to work
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

    private void generateLastMinuteReport() {
        Instant to = Instant.now(); // for now; could use simTime later if you centralize it
        Instant from = to.minusSeconds(60);

        PatientReport r = ctx.reports.generate(patientId, from, to, ctx.store);

        System.out.println("==== REPORT " + patientId.getDisplayName() + " ====");
        for (Map.Entry<rpm.domain.VitalType, VitalSummary> e : r.getSummaries().entrySet()) {
            VitalSummary vs = e.getValue();
            System.out.printf("%s: n=%d mean=%.2f min=%.2f max=%.2f%n",
                    e.getKey(), vs.getN(), vs.getMean(), vs.getMin(), vs.getMax());
        }
    }
}
