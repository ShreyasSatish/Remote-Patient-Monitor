package rpm.demo;

import rpm.domain.PatientId;
import rpm.simulation.PatientVitalsRow;
import rpm.simulation.WardManager;
import rpm.ui.EcgPanel;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.time.Instant;
import java.util.List;

public class WardEcgDemo {
    private static final double DT_SECONDS = 0.04;

    public static void main(String[] args) {
        int initial = WardManager.MIN_PATIENTS;
        if (args.length > 0) {
            try {
                initial = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {
            }
        }

        int finalInitial = initial;
        SwingUtilities.invokeLater(() -> start(finalInitial));
    }

    private static void start(int initialPatients) {
        WardManager ward = new WardManager(initialPatients);

        JFrame frame = new JFrame("Ward ECG Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        EcgPanel ecgPanel = new EcgPanel();
        JTextArea table = new JTextArea(10, 40);
        table.setEditable(false);

        JComboBox<PatientId> selector = new JComboBox<>();
        refreshSelector(selector, ward);
        selector.setSelectedItem(new PatientId(1));

        selector.addActionListener(e -> {
            PatientId id = (PatientId) selector.getSelectedItem();
            if (id != null) {
                ward.setSelectedPatientId(id);
                ecgPanel.reset();
            }
        });

        JButton addBtn = new JButton("Add patient");
        addBtn.addActionListener(e -> {
            PatientId added = ward.addPatient();
            refreshSelector(selector, ward);
            selector.setSelectedItem(added);
        });

        JButton removeBtn = new JButton("Remove selected (>=09 only)");
        removeBtn.addActionListener(e -> {
            PatientId id = (PatientId) selector.getSelectedItem();
            if (id == null) {
                return;
            }
            boolean ok = ward.removePatient(id);
            if (ok) {
                refreshSelector(selector, ward);
                selector.setSelectedItem(new PatientId(1));
            }
        });

        JPanel top = new JPanel(new BorderLayout());
        JPanel controls = new JPanel();
        controls.add(selector);
        controls.add(addBtn);
        controls.add(removeBtn);

        top.add(controls, BorderLayout.WEST);

        frame.add(top, BorderLayout.NORTH);
        frame.add(ecgPanel, BorderLayout.CENTER);
        frame.add(new JScrollPane(table), BorderLayout.SOUTH);

        frame.setSize(900, 600);
        frame.setVisible(true);

        Instant[] time = {Instant.now()};
        double[] sinceTable = {0.0};

        Timer timer = new Timer((int) Math.round(DT_SECONDS * 1000.0), e -> {
            time[0] = time[0].plusMillis((long) Math.round(DT_SECONDS * 1000.0));
            ward.tick(time[0], DT_SECONDS);

            ecgPanel.appendSamples(ward.getSelectedPatientLastEcgSegment());

            sinceTable[0] += DT_SECONDS;
            if (sinceTable[0] >= 1.0) {
                sinceTable[0] = 0.0;
                table.setText(formatTable(ward.getLatestVitalsTable(), ward.getSelectedPatientId(), ward.getPatientCount()));
            }
        });
        timer.start();
    }

    private static void refreshSelector(JComboBox<PatientId> selector, WardManager ward) {
        selector.removeAllItems();
        for (PatientId id : ward.getPatientIds()) {
            selector.addItem(id);
        }
    }

    private static String formatTable(List<PatientVitalsRow> rows, PatientId selected, int count) {
        StringBuilder sb = new StringBuilder();
        sb.append("Ward (").append(count).append(" patients) - selected: ").append(selected.getDisplayName()).append("\n");
        sb.append("Bed   HR     RR     BP        Temp\n");
        sb.append("----------------------------------------\n");

        for (PatientVitalsRow r : rows) {
            sb.append(String.format("%-5s %-6.1f %-6.1f %-4.0f/%-4.0f %-6.2f%n",
                    r.getPatientId().getDisplayName(),
                    r.getHr(),
                    r.getRr(),
                    r.getSys(),
                    r.getDia(),
                    r.getTemp()
            ));
        }
        return sb.toString();
    }
}
