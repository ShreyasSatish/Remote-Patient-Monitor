package rpm.ui.app;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import rpm.domain.PatientId;
import rpm.ui.authentication.LoginView;
import rpm.ui.dashboard.DashboardView;
import rpm.ui.layout.AppShell;
import rpm.ui.menu.MenuView;
import rpm.ui.patient.PatientDetailView;

public final class Router {
    private final Stage stage;
    private final AppContext ctx;
    private final AppShell shell;

    public Router(Stage stage, AppContext ctx) {
        this.stage = stage;
        this.ctx = ctx;
        this.shell = new AppShell(ctx, this);

        Scene scene = new Scene(shell, 1100, 750);
        var cssUrl = Router.class.getResource("/rpm/ui/theme/rancho.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        stage.setScene(scene);
    }

    private void setContent(Node content) {
        shell.setContent(content);
    }

    public void showLogin() {
        stage.setTitle("RPM - Login");
        shell.setTop(null);
        shell.setAlertsEnabled(false);

        LoginView card = new LoginView(ctx, this);

        StackPane bg = new StackPane(card);
        bg.getStyleClass().add("login-bg");
        bg.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        StackPane.setAlignment(card, Pos.CENTER);
        StackPane.setMargin(card, new Insets(24));

        setContent(bg);
    }

    public void showDashboard() {
        stage.setTitle("RPM - Dashboard");
        shell.setTop(shell.getBanner());
        shell.setAlertsEnabled(true);
        setContent(new DashboardView(ctx, this, shell.getBanner()));

        if (ctx.session.isLoggedIn()) {
            NurseUser u = ctx.session.getUser();
            shell.getBanner().setUserText(u.getName() + " (" + u.getUsername() + ")");
        } else {
            shell.getBanner().setUserText("");
        }
    }

    public void showPatientDetail(PatientId id) {
        stage.setTitle("RPM - Patient " + id.getDisplayName());
        shell.setTop(shell.getBanner());
        shell.setAlertsEnabled(true);
        setContent(new PatientDetailView(ctx, this, id));
    }

    public void showMenu() {
        stage.setTitle("RPM - Menu");
        shell.setTop(shell.getBanner());
        shell.setAlertsEnabled(true);
        setContent(new MenuView(ctx, this));
    }

    public void logout() {
        ctx.session.clear();
        showLogin();
    }

    public void powerOffApp() {
        Platform.exit();
    }

    public void restartAppUiOnly() {
        ctx.settings.resetDefaults();
        showDashboard();
    }
}
