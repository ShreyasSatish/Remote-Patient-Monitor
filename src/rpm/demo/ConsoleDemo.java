package rpm.demo;

import rpm.domain.VitalSnapshot;
import rpm.simulation.PatientSimulator;

import java.time.Instant;

/**
 * Console-based demo that prints a short stream of simulated vital snapshots.
 */

public class ConsoleDemo {

    public static void main(String[] args) throws InterruptedException {
        PatientSimulator patientSimulator = new PatientSimulator();

        for (int stepIndex = 0; stepIndex < 20; stepIndex++) {
            Instant now = Instant.now();
            VitalSnapshot snapshot = patientSimulator.nextSnapshot(now);

            System.out.println(now + " " + snapshot.getValues());

            Thread.sleep(1000L);
        }
    }
}
