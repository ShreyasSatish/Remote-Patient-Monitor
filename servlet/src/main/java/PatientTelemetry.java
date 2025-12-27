import java.util.ArrayList;
import java.util.List;

//Holds the FULL historical telemetry for 1 patient (running log).

public class PatientTelemetry {

    private final List<Double> hr   = new ArrayList<>();
    private final List<Double> rr   = new ArrayList<>();
    private final List<Double> sys  = new ArrayList<>();
    private final List<Double> dia  = new ArrayList<>();
    private final List<Double> temp = new ArrayList<>();
    private final List<Double> ecg  = new ArrayList<>();

    //Append new telemetry values to the running log

    public synchronized void append(
            PatientTelemetry incoming
    ) {
        hr.addAll(incoming.hr);
        rr.addAll(incoming.rr);
        sys.addAll(incoming.sys);
        dia.addAll(incoming.dia);
        temp.addAll(incoming.temp);
        ecg.addAll(incoming.ecg);
    }

    // Getters needed for JSON output
    public List<Double> getHr()   { return hr; }
    public List<Double> getRr()   { return rr; }
    public List<Double> getSys()  { return sys; }
    public List<Double> getDia()  { return dia; }
    public List<Double> getTemp() { return temp; }
    public List<Double> getEcg()  { return ecg; }
}
