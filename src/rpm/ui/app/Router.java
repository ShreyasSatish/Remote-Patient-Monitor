package rpm.ui.app;

import javafx.scene.Scene;
import javafx.stage.Stage;
import rpm.domain.PatientId;
import rpm.ui.authentication.LoginView;
import rpm.ui.dashboard.DashboardView;
import rpm.ui.menu.MenuView;
import rpm.ui.patient.PatientDetailView;
import rpm.ui.alerts.AlertModeView;

public final class Router {
    private final Stage stage;
    private final AppContext ctx;

    public Router(Stage stage, AppContext ctx) {
        this.stage = stage;
        this.ctx = ctx;
    }

    public void showLogin() {
        LoginView view = new LoginView(ctx, this);
        stage.setTitle("RPM - Login");
        stage.setScene(new Scene(view, 1100, 750));
    }

    public void showDashboard() {
        DashboardView view = new DashboardView(ctx, this);
        stage.setTitle("RPM - Dashboard");
        stage.setScene(new Scene(view, 1100, 750));
    }

    public void showPatientDetail(PatientId id) {
        PatientDetailView view = new PatientDetailView(ctx, this, id);
        stage.setTitle("RPM - Patient " + id.getDisplayName());
        stage.setScene(new Scene(view, 1100, 750));
    }

    public void showMenu() {
        MenuView view = new MenuView(ctx, this);
        stage.setTitle("RPM - Menu");
        stage.setScene(new Scene(view, 1100, 750));
    }

    public void logout() {
        ctx.session.clear();
        showLogin();
    }

    public void restartAppUiOnly() {
        // “restart” meaning: reset UI state (NOT reboot PC)
        ctx.settings.resetDefaults();
        showDashboard();
    }

    public void powerOffApp() {
        javafx.application.Platform.exit();
    }

    public void showAlertMode() {
        AlertModeView view = new AlertModeView(ctx, this);
        stage.setTitle("RPM - Alerts");
        stage.setScene(new javafx.scene.Scene(view, 1100, 750));
    }

}
