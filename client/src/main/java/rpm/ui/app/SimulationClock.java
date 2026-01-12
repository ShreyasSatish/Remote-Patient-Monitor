package rpm.ui.app;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import rpm.net.TelemetryPublisher;
import rpm.simulation.WardManager;

import java.time.Instant;

public final class SimulationClock {

    private static final double DT_SECONDS = 0.04;

    private final WardManager ward;
    private final TelemetryPublisher telemetry;
    private final Timeline timeline;

    private Instant simTime;

    public SimulationClock(WardManager ward) {
        this(ward, null);
    }

    public SimulationClock(WardManager ward, TelemetryPublisher telemetry) {
        this.ward = ward;
        this.telemetry = telemetry;
        this.simTime = Instant.now();
        this.timeline = new Timeline(new KeyFrame(Duration.millis(40), e -> tick()));
        this.timeline.setCycleCount(Timeline.INDEFINITE);
    }

    private void tick() {
        simTime = simTime.plusMillis(40);
        ward.tick(simTime, DT_SECONDS);

        if (telemetry != null) {
            telemetry.onTick(ward, simTime);
        }
    }

    public void start() { timeline.play(); }
    public void stop() { timeline.stop(); }
    public Instant getSimTime() { return simTime; }
}
