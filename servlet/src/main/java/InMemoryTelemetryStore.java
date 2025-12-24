import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//In-memory storage for telemetry data.

public class InMemoryTelemetryStore
        extends AbstractTelemetryStore {

    // bedId → running telemetry history
    private final Map<String, PatientTelemetry> store =
            new ConcurrentHashMap<>();

    @Override
    public void store(
            String bedId,
            PatientTelemetry incoming
    ) {
        store.compute(bedId, (id, existing) -> {
            if (existing == null) {
                return incoming;
            }
            existing.append(incoming);
            return existing;
        });
    }

    //retrieve all telemetry data
    public Map<String, PatientTelemetry> getAll() {
        return store;
    }
}
