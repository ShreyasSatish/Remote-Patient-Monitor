package rpm.ui.app;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import rpm.domain.PatientId;
import rpm.ui.authentication.LoginView;
import rpm.ui.dashboard.DashboardView;
import rpm.ui.layout.AppShell;
import rpm.ui.menu.MenuView;
import rpm.ui.patient.PatientDetailView;
import rpm.ui.app.NurseUser;

public final class Router {
    private final Stage stage;
    private final AppContext ctx;
    private final AppShell shell;

    public Router(Stage stage, AppContext ctx) {
        this.stage = stage;
        this.ctx = ctx;
        this.shell = new AppShell(ctx, this);

        stage.setScene(new Scene(shell, 1100, 750));
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
        bg.setStyle("-fx-background-color: #A0C1D1;"); // same as your old blue
        StackPane.setAlignment(card, Pos.CENTER);

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
        javafx.application.Platform.exit();
    }

    public void restartAppUiOnly() {
        ctx.settings.resetDefaults();
        showDashboard();
    }

}
