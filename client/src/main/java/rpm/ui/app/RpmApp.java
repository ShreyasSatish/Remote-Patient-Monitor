package rpm.ui.app;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.stage.Stage;
import rpm.domain.alarm.AlarmConfig;
import rpm.domain.alarm.AlarmEngine;
import rpm.domain.alarm.AlarmService;
import rpm.domain.alarm.ConsoleAlarmListener;
import rpm.domain.report.InMemoryPatientDataStore;
import rpm.domain.report.ReportGenerator;
import rpm.net.TelemetryPublisher;
import rpm.simulation.WardManager;

import java.time.Duration;

public final class RpmApp extends Application {

    private Timeline telemetryTimeline;

    @Override
    public void start(Stage stage) {
        stage.setMinWidth(800);
        stage.setMinHeight(600);

        System.out.println("RPM_TELEMETRY_URL=" + System.getenv("RPM_TELEMETRY_URL"));
        System.out.println("rpm.telemetry.url=" + System.getProperty("rpm.telemetry.url"));

        WardManager ward = new WardManager(8);

        AlarmEngine alarmEngine = new AlarmEngine(AlarmConfig.defaultAdult());
        AlarmService alarmService = new AlarmService(alarmEngine);
        ward.addListener(alarmService);
        alarmService.addListener(new ConsoleAlarmListener());

        InMemoryPatientDataStore store = new InMemoryPatientDataStore(Duration.ofMinutes(10));
        ward.addListener(store);
        alarmService.addListener(store);

        ReportGenerator reportGenerator = new ReportGenerator();

        Session session = new Session();
        UISettings settings = new UISettings();

        SimulationClock clock = new SimulationClock(ward);

        TelemetryPublisher telemetry = TelemetryPublisher.tryCreateFromSystem().orElse(null);
        if (telemetry != null) {
            System.out.println("[telemetry] enabled: " + telemetry.getUrl());

            // Call telemetry frequently;
            telemetryTimeline = new Timeline(
                    new KeyFrame(javafx.util.Duration.millis(200),
                            e -> telemetry.onTick(ward, clock.getSimTime()))
            );
            telemetryTimeline.setCycleCount(Timeline.INDEFINITE);
            telemetryTimeline.play();
        } else {
            System.out.println("[telemetry] disabled (set RPM_TELEMETRY_URL or -Drpm.telemetry.url)");
        }

        clock.start();

        AppContext ctx = new AppContext(ward, alarmService, store, reportGenerator, session, settings, clock);
        Router router = new Router(stage, ctx);
        router.showLogin();

        stage.show();
    }

    @Override
    public void stop() {
        if (telemetryTimeline != null) telemetryTimeline.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
