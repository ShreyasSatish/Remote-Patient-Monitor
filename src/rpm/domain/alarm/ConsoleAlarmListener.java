package rpm.domain.alarm;


// IMPORTANT: this is just so i can test the alarm system!! will delete this once UI is set up properly.
// - sohani

public final class ConsoleAlarmListener implements AlarmListener {
    @Override
    public void onAlarmTransition(AlarmTransition t) {
        System.out.printf(
                "[ALARM] %s %s: %s → %s (%s)%n",
                t.getPatientId().getDisplayName(),
                t.getVitalType(),
                t.getFrom(),
                t.getTo(),
                t.getReason()
        );
    }

    @Override
    public void onAlarmState(rpm.domain.PatientId id, java.time.Instant time, AlarmState state) {
        // ignore continuous updates
    }
}
