package rpm.ui.layout;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import rpm.ui.alerts.AlertOverlayController;
import rpm.ui.alerts.AlertOverlayView;
import rpm.ui.app.AppContext;
import rpm.ui.app.Router;

public final class AppShell extends BorderPane {

    private final TopBanner banner;

    private final StackPane stack = new StackPane();
    private final BorderPane contentPane = new BorderPane();

    private final AlertOverlayView overlayView = new AlertOverlayView();
    private final AlertOverlayController overlayController;

    private final Timeline alertTick;
    private boolean alertsEnabled = true;

    public AppShell(AppContext ctx, Router router) {
        this.banner = new TopBanner(ctx, router);
        setTop(banner);

        overlayController = new AlertOverlayController(ctx, overlayView);

        stack.getChildren().addAll(contentPane, overlayView);
        setCenter(stack);

        alertTick = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (!alertsEnabled) return;
            long nowMs = ctx.clock.getSimTime().toEpochMilli();
            overlayController.tick(nowMs);
        }));
        alertTick.setCycleCount(Timeline.INDEFINITE);
        alertTick.play();
    }

    public void setContent(Node node) {
        contentPane.setCenter(node);
    }

    public TopBanner getBanner() {
        return banner;
    }

    public void setAlertsEnabled(boolean enabled) {
        this.alertsEnabled = enabled;
        if (!enabled) {
            overlayController.forceStop(); // we'll add this next
        }
    }
}
