package rpm.ui.app;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import rpm.domain.PatientId;
import rpm.ui.authentication.LoginView;
import rpm.ui.dashboard.DashboardView;
import rpm.ui.layout.AppShell;
import rpm.ui.patient.PatientDetailView;
import rpm.ui.layout.SettingsPopup;

import java.awt.*;

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
        } else {
            System.out.println("WARN: rancho.css not found on classpath");
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

        StackPane bg = new StackPane();
        bg.getStyleClass().add("login-bg");

        BorderPane wrapper = new BorderPane();
        wrapper.setPickOnBounds(false);
        wrapper.setCenter(card);

        BorderPane.setAlignment(card, Pos.CENTER);
        BorderPane.setMargin(card, new Insets(24));

        // top-right power button
        HBox top = new HBox();
        top.getStyleClass().add("top-banner");
        top.setPadding(new Insets(10, 14, 10, 14));
        top.setAlignment(Pos.CENTER_RIGHT);

        javafx.scene.control.Button powerBtn = new javafx.scene.control.Button("⏻");
        powerBtn.getStyleClass().add("banner-btn");
        powerBtn.setMinHeight(38);
        powerBtn.setOnAction(e -> SettingsPopup.showLoginOnlyPowerOff(powerBtn));

        top.getChildren().add(powerBtn);
        wrapper.setTop(top);

        bg.getChildren().add(wrapper);
        setContent(bg);
    }


    private Node buildLoginTopBar() {
        HBox bar = new HBox();
        bar.getStyleClass().add("top-banner"); // reuse same look
        bar.setPadding(new Insets(10, 14, 10, 14));
        bar.setAlignment(Pos.CENTER_RIGHT);

        Button powerBtn = new Button("⏻");
        powerBtn.getStyleClass().add("banner-btn");
        powerBtn.setOnAction(e -> SettingsPopup.showLoginOnlyPowerOff(powerBtn));
        powerBtn.setMinHeight(38);

        bar.getChildren().add(powerBtn);
        return bar;
    }


    public void showDashboard() {
        stage.setTitle("RPM - Dashboard");
        shell.setTop(shell.getBanner());
        shell.setAlertsEnabled(true);

        setContent(new DashboardView(ctx, this, shell.getBanner()));
        updateBannerUser();
    }


    public void showPatientDetail(PatientId id) {
        stage.setTitle("RPM - Patient " + id.getDisplayName());
        shell.setTop(shell.getBanner());
        shell.setAlertsEnabled(true);
        setContent(new PatientDetailView(ctx, this, id));
    }

    public void showSettings() {
        stage.setTitle("RPM - Settings");
        shell.setTop(shell.getBanner());
        shell.setAlertsEnabled(true);
        setContent(new rpm.ui.menu.MenuView(ctx, this));

        updateBannerUser();
        /**
        stage.setTitle("RPM - Patient " + id.getDisplayName());
        shell.setTop(shell.getBanner());
        shell.setAlertsEnabled(true);

        setContent(new PatientDetailView(ctx, this, id));
        updateBannerUser();
         **/ // this is the new code
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

    private void updateBannerUser() {
        if (ctx.session.isLoggedIn()) {
            NurseUser u = ctx.session.getUser();
            shell.getBanner().setUserText(u.getName() + " (" + u.getUsername() + ")");
        } else {
            shell.getBanner().setUserText("");
        }
    }
}
