import java.util.logging.Level;
import java.util.logging.Logger;

public final class TelemetryStores {
    private static final Logger log = Logger.getLogger(TelemetryStores.class.getName());
    private TelemetryStores() {}

    public static AbstractTelemetryStore create() {
        String backend = System.getenv("TELEMETRY_BACKEND");
        if (backend == null || backend.isBlank()) backend = "memory";

        if ("db".equalsIgnoreCase(backend)) {
            DbConfig cfg = DbConfig.fromEnv();
            if (cfg == null) {
                log.warning("TELEMETRY_BACKEND=db but DB_URL missing; falling back to memory");
                return new InMemoryTelemetryStore();
            }
            try {
                return new DbTelemetryStore(cfg);
            } catch (RuntimeException e) {
                log.log(Level.WARNING, "DB init failed; falling back to memory", e);
                return new InMemoryTelemetryStore();
            }
        }

        return new InMemoryTelemetryStore();
    }
}
