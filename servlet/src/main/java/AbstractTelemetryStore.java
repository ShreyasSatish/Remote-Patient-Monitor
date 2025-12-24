//Allows different telemetry storage strategies (in-memory, database, file, etc.).

public abstract class AbstractTelemetryStore {

    //Store telemetry data for a patient
    public abstract void store(
            String bedId,
            PatientTelemetry data
    );
}
