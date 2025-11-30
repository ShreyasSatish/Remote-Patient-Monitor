package rpm.demo;

import rpm.domain.VitalSnapshot;
import rpm.domain.VitalType;
import rpm.simulation.PatientScenario;
import rpm.simulation.PatientScenarioFactory;
import rpm.simulation.PatientSimulator;
import rpm.ui.EcgPanel;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;

/**
 * Swing demo window showing:
 *   - live ECG strip with a moving sweep cursor, and
 *   - numeric vitals (HR, RR, BP, Temp) along the bottom.
 */
public class EcgDemo {

    private final PatientSimulator simulator =
            PatientScenarioFactory.create(PatientScenario.NORMAL_ADULT);

    private final EcgPanel ecgPanel = new EcgPanel();
    private final JLabel vitalsLabel = new JLabel(
            "HR: -- bpm    RR: -- breaths/min    BP: --/-- mmHg    Temp: -- °C"
    );

    private Instant currentTime = Instant.now();
    private double secondsSinceLastVitals = 0.0;

    // UI refresh period in seconds
    private static final double DT_SECONDS = 0.04;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EcgDemo::new);
    }

    public EcgDemo() {
        JFrame frame = new JFrame("Remote Patient Monitor - ECG Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        frame.add(ecgPanel, BorderLayout.CENTER);

        vitalsLabel.setForeground(Color.WHITE);
        vitalsLabel.setBackground(Color.BLACK);
        vitalsLabel.setOpaque(true);
        frame.add(vitalsLabel, BorderLayout.SOUTH);

        frame.setSize(900, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        int delayMs = (int) Math.round(DT_SECONDS * 1000.0);
        Timer timer = new Timer(delayMs, e -> tick());
        timer.start();
    }

    private void tick() {
        currentTime = currentTime.plusMillis((long) Math.round(DT_SECONDS * 1000.0));

        // Advance ECG continuously and feed the sweep
        simulator.advanceEcg(DT_SECONDS);
        double[] newSegment = simulator.getLastEcgSegment();
        ecgPanel.appendSamples(newSegment);

        // Update numeric vitals about once per second
        secondsSinceLastVitals += DT_SECONDS;
        if (secondsSinceLastVitals >= 1.0) {
            secondsSinceLastVitals = 0.0;
            VitalSnapshot snapshot = simulator.nextSnapshot(currentTime);
            updateVitalsLabel(snapshot);
        }
    }

    private void updateVitalsLabel(VitalSnapshot snapshot) {
        Map<VitalType, Double> values = snapshot.getValues();

        double hr   = safeGet(values, VitalType.HEART_RATE);
        double rr   = safeGet(values, VitalType.RESP_RATE);
        double sys  = safeGet(values, VitalType.BP_SYSTOLIC);
        double dia  = safeGet(values, VitalType.BP_DIASTOLIC);
        double temp = safeGet(values, VitalType.TEMPERATURE);

        String text = String.format(Locale.UK,
                "HR: %.1f bpm    RR: %.1f breaths/min    BP: %.0f/%.0f mmHg    Temp: %.2f \u00b0C",
                hr, rr, sys, dia, temp);

        vitalsLabel.setText(text);
    }

    private double safeGet(Map<VitalType, Double> values, VitalType type) {
        Double v = values.get(type);
        return v != null ? v : Double.NaN;
    }
}
