package rpm.ui.app;

import javafx.application.Application;
import javafx.stage.Stage;
import rpm.domain.alarm.*;
import rpm.domain.report.InMemoryPatientDataStore;
import rpm.domain.report.ReportGenerator;
import rpm.simulation.WardManager;

import java.time.Duration;

public final class RpmApp extends Application {

    @Override
    public void start(Stage stage) {


        // core services (already exist)
        WardManager ward = new WardManager(8);

        AlarmEngine alarmEngine = new AlarmEngine(AlarmConfig.defaultAdult());
        AlarmService alarmService = new AlarmService(alarmEngine);
        ward.addListener(alarmService);
        alarmService.addListener(new ConsoleAlarmListener());
        /**
        alarmService.addListener(new AlarmListener() {
            @Override public void onAlarmTransition(AlarmTransition t) {
                System.out.println("TRANSITION: " + t.getTime() + " " + t.getVitalType() + " " + t.getFrom() + "->" + t.getTo());
            }
            @Override public void onAlarmState(PatientId id, java.time.Instant time, AlarmState state) {
                // Comment out if too spammy
                // System.out.println(id.getDisplayName() + " overall=" + state.getOverall());
            }
        });
         **/

        alarmService.addListener(new AlarmListener() {
            @Override public void onAlarmTransition(AlarmTransition t) {
                java.awt.Toolkit.getDefaultToolkit().beep();
            }
            @Override public void onAlarmState(rpm.domain.PatientId id, java.time.Instant time, AlarmState state) {}
        });

        // in memory history for demo/reporting
        InMemoryPatientDataStore store = new InMemoryPatientDataStore(Duration.ofMinutes(10));
        ward.addListener(store);
        alarmService.addListener(store);

        // Report generator
        ReportGenerator reportGenerator = new ReportGenerator();

        // UI state
        Session session = new Session();
        UISettings settings = new UISettings();

        // simulation clock
        SimulationClock clock = new SimulationClock(ward);
        clock.start();

        AppContext ctx = new AppContext(ward, alarmService, store, reportGenerator, session, settings, clock);
        Router router = new Router(stage, ctx);
        router.showLogin();
        stage.show();


    }

    public static void main(String[] args) {
        launch(args);
    }
}
