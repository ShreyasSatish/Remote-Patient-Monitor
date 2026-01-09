package rpm.ui.dashboard;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import rpm.ui.app.AppContext;
import rpm.ui.app.Router;
import rpm.ui.layout.AppShell;

public final class DashboardView extends BorderPane {

    private final AppContext ctx;
    private final Router router;

    private final DashboardController controller;
    private final PatientGridView grid;

    private final Timeline uiTick;

    public DashboardView(AppContext ctx, Router router) {
        this.ctx = ctx;
        this.router = router;

        AppShell shell = new AppShell(ctx, router);

        this.grid = new PatientGridView();
        this.controller = new DashboardController(ctx, router, grid, shell.getBanner());

        shell.setContent(grid);
        setCenter(shell);

        controller.refreshPatients();
        controller.renderPage();

        // UI refresh every 1 second (matches vitals snapshot frequency)
        uiTick = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            controller.refreshPatients();
            controller.renderPage();
        }));
        uiTick.setCycleCount(Timeline.INDEFINITE);
        uiTick.play();

        // stop timeline when view is removed
        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) uiTick.stop();
        });
    }
}
