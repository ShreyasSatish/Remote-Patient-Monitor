package rpm.ui.alerts;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import rpm.ui.app.AppContext;
import rpm.ui.app.Router;
import rpm.ui.layout.AppShell;

public final class AlertModeView extends BorderPane {

    private final Timeline uiTick;

    public AlertModeView(AppContext ctx, Router router) {
        AppShell shell = new AppShell(ctx, router);

        AlertGridView grid = new AlertGridView();
        AlertController controller = new AlertController(ctx, router, grid);

        shell.setContent(grid);
        setCenter(shell);

        controller.refresh();

        uiTick = new Timeline(new KeyFrame(Duration.seconds(1), e -> controller.refresh()));
        uiTick.setCycleCount(Timeline.INDEFINITE);
        uiTick.play();

        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) uiTick.stop();
        });
    }
}
