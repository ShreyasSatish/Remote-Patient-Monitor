package rpm.domain.alarm;

public enum AlarmLevel {
    GREEN, AMBER, RED;

    public static AlarmLevel max(AlarmLevel a, AlarmLevel b) {
        return (a.ordinal() >= b.ordinal()) ? a : b;
    }
}
