package rpm.ui.layout;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import rpm.ui.app.AppContext;
import rpm.ui.app.Router;

public final class AppShell extends BorderPane {

    private final TopBanner banner;

    public AppShell(AppContext ctx, Router router) {
        this.banner = new TopBanner(ctx, router);
        setTop(banner);
    }

    public void setContent(Node node) {
        setCenter(node);
    }

    public TopBanner getBanner() {
        return banner;
    }
}
