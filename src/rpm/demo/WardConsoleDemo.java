package rpm.demo;

import rpm.domain.PatientId;
import rpm.simulation.PatientVitalsRow;
import rpm.simulation.WardManager;

import java.time.Instant;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class WardConsoleDemo {
    private static final double DT_SECONDS = 0.04;

    public static void main(String[] args) throws InterruptedException {
        int initial = parseInitialPatients(args);
        WardManager ward = new WardManager(initial);

        AtomicBoolean running = new AtomicBoolean(true);
        startCommandThread(ward, running);

        Instant time = Instant.now();
        double secondsSincePrint = 0.0;

        System.out.println("Commands: a (add), r <n> (remove bed n, only n>8), s <n> (select), q (quit)");

        while (running.get()) {
            time = time.plusMillis((long) Math.round(DT_SECONDS * 1000.0));
            ward.tick(time, DT_SECONDS);

            secondsSincePrint += DT_SECONDS;
            if (secondsSincePrint >= 1.0) {
                secondsSincePrint = 0.0;
                printTable(ward.getLatestVitalsTable(), ward.getSelectedPatientId(), ward.getPatientCount());
                printSelectedEcgInfo(ward);
            }

            Thread.sleep((long) Math.round(DT_SECONDS * 1000.0));
        }
    }

    private static int parseInitialPatients(String[] args) {
        if (args.length == 0) {
            return WardManager.MIN_PATIENTS;
        }
        try {
            return Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            return WardManager.MIN_PATIENTS;
        }
    }

    private static void startCommandThread(WardManager ward, AtomicBoolean running) {
        Thread t = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (running.get()) {
                String line;
                try {
                    line = scanner.nextLine();
                } catch (Exception e) {
                    return;
                }

                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                if (line.equalsIgnoreCase("q")) {
                    running.set(false);
                    return;
                }

                if (line.equalsIgnoreCase("a")) {
                    PatientId id = ward.addPatient();
                    System.out.println("Added: " + id.getDisplayName());
                    continue;
                }

                if (line.startsWith("s ")) {
                    Integer n = parseBedNumber(line.substring(2));
                    if (n != null) {
                        ward.setSelectedPatientId(new PatientId(n));
                        System.out.println("Selected: Bed " + String.format("%02d", n));
                    }
                    continue;
                }

                if (line.startsWith("r ")) {
                    Integer n = parseBedNumber(line.substring(2));
                    if (n != null) {
                        boolean ok = ward.removePatient(new PatientId(n));
                        System.out.println("Remove Bed " + String.format("%02d", n) + ": " + ok);
                    }
                }
            }
        });

        t.setDaemon(true);
        t.start();
    }

    private static Integer parseBedNumber(String s) {
        s = s.trim();
        if (s.isEmpty()) return null;
        try {
            int n = Integer.parseInt(s);
            if (n <= 0) return null;
            return n;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static void printTable(List<PatientVitalsRow> rows, PatientId selected, int count) {
        System.out.println();
        System.out.println("Ward (" + count + " patients) - selected: " + selected.getDisplayName());
        System.out.println("Bed   HR     RR     BP        Temp");
        System.out.println("----------------------------------------");

        for (PatientVitalsRow r : rows) {
            System.out.printf("%-5s %-6.1f %-6.1f %-4.0f/%-4.0f %-6.2f%n",
                    r.getPatientId().getDisplayName(),
                    r.getHr(),
                    r.getRr(),
                    r.getSys(),
                    r.getDia(),
                    r.getTemp()
            );
        }
    }

    private static void printSelectedEcgInfo(WardManager ward) {
        double[] seg = ward.getSelectedPatientLastEcgSegment();
        System.out.println("Selected ECG segment: " + seg.length + " samples");
    }
}
