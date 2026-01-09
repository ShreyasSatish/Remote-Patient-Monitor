package rpm.net;

import rpm.domain.PatientId;
import rpm.domain.VitalSnapshot;
import rpm.domain.VitalType;
import rpm.simulation.WardManager;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TelemetryPublisher {
    // Shared HTTP client for POSTing telemetry JSON
    private final HttpClient client = HttpClient.newHttpClient();

    // Telemetry endpoint so its local by default but then overridable via -Drpm.telemetry.url
    private final String url;

    // ECG is simulated at 250 Hz, we downsample to 125 Hz ( it is a factor 4)
    private final int rawFsHz = 250;
    private final int targetFsHz = 125;
    private final int downsampleFactor = 2;

    // Send ECG in 1-second chunks @ 125 Hz
    private final int chunkSamples = 125;

    // Per-patient ECG buffer it is used to accumulate samples until a chunk can be sent
    private final Map<PatientId, EcgBuf> ecg = new HashMap<>();

    // network sends to 1 Hz
    private long lastSendMs = -1;

    public TelemetryPublisher() {
        this(System.getProperty("rpm.telemetry.url", "http://localhost:8080/servlet/telemetry"));
    }

    public TelemetryPublisher(String url) {
        this.url = url;
    }

    // This function is called each simulation tick and its the buffer ECG
    // and periodically POST a payload with latest vitals as well as ECG chunks
    public void onTick(WardManager ward, Instant simTime) {
        long nowMs = simTime.toEpochMilli();

        // Accumulate ECG segments for each patient ( this is downsampled) into their buffer
        for (PatientId id : ward.getPatientIds()) {
            double[] seg = ward.getPatientLastEcgSegment(id);
            if (seg == null || seg.length == 0) continue;
            EcgBuf b = ecg.computeIfAbsent(id, k -> new EcgBuf());
            b.append(seg, downsampleFactor, nowMs);
        }

        // Only send once per second
        if (lastSendMs >= 0 && (nowMs - lastSendMs) < 1000) return;
        lastSendMs = nowMs;

        String json = buildPayload(ward, nowMs);
        postAsync(json);
    }

    // This part is to build the JSON payload expected by the servlet so patients to bed to arrays of vitals and finally ecg chunk
    private String buildPayload(WardManager ward, long nowMs) {
        StringBuilder sb = new StringBuilder(64 * ward.getPatientCount());
        sb.append("{\"patients\":{");

        List<PatientId> ids = ward.getPatientIds();
        for (int i = 0; i < ids.size(); i++) {
            PatientId id = ids.get(i);
            String bed = id.getDisplayName();
            VitalSnapshot snap = ward.getPatientLatestSnapshot(id);

            // Latest snapshot values (wrapped as single-element arrays for storage compatibility)
            Double hr = val(snap, VitalType.HEART_RATE);
            Double rr = val(snap, VitalType.RESP_RATE);
            Double sys = val(snap, VitalType.BP_SYSTOLIC);
            Double dia = val(snap, VitalType.BP_DIASTOLIC);
            Double temp = val(snap, VitalType.TEMPERATURE);

            // Pull a 1-second ECG chunk if available
            EcgBuf b = ecg.get(id);
            double[] chunk = (b != null) ? b.takeChunk(chunkSamples) : null;
            Long chunkStart = (b != null && chunk != null) ? b.lastChunkStartMs : null;

            if (i > 0) sb.append(",");
            sb.append("\"").append(bed).append("\":{");

            sb.append("\"ts\":[").append(nowMs).append("],");
            sb.append("\"hr\":[").append(numOrNull(hr)).append("],");
            sb.append("\"rr\":[").append(numOrNull(rr)).append("],");
            sb.append("\"sys\":[").append(numOrNull(sys)).append("],");
            sb.append("\"dia\":[").append(numOrNull(dia)).append("],");
            sb.append("\"temp\":[").append(numOrNull(temp)).append("]");

            // ECG is optional: only included when we have a full chunk
            if (chunk != null && chunk.length > 0 && chunkStart != null) {
                sb.append(",\"ecgTsStart\":").append(chunkStart);
                sb.append(",\"ecgFs\":").append(targetFsHz);
                sb.append(",\"ecg\":[");
                for (int k = 0; k < chunk.length; k++) {
                    if (k > 0) sb.append(",");
                    sb.append(chunk[k]);
                }
                sb.append("]");
            }

            sb.append("}");
        }

        sb.append("}}");
        return sb.toString();
    }

    // we don't block the simulation thread waiting for the response
    private void postAsync(String json) {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        client.sendAsync(req, HttpResponse.BodyHandlers.discarding());
    }

    // Safe value lookup from snapshot map also to treat as null if it is missing
    private static Double val(VitalSnapshot snap, VitalType type) {
        if (snap == null || snap.getValues() == null) return null;
        Double v = snap.getValues().get(type);
        if (v == null || v.isNaN()) return null;
        return v;
    }

    private static String numOrNull(Double v) {
        return v == null ? "null" : Double.toString(v);
    }

    // Minimal ECG buffer will store the downsampled samples until a fixed-size chunk can be taken
    private static final class EcgBuf {
        private double[] buf = new double[512];
        private int size = 0;

        // Timestamp associated with the next chunk start
        private long startMs = -1;
        long lastChunkStartMs;

        // Append a segment, taking every factor sample (this is where the downsampling happens)
        void append(double[] seg, int factor, long nowMs) {
            if (seg == null || seg.length == 0) return;
            if (startMs < 0) startMs = nowMs;

            for (int i = 0; i < seg.length; i += factor) {
                ensure(size + 1);
                buf[size++] = seg[i];
            }
        }

        // Take the next n samples from the front of the buffer or null if insufficient
        double[] takeChunk(int n) {
            if (size < n) return null;

            double[] out = new double[n];
            System.arraycopy(buf, 0, out, 0, n);

            // Shift remaining samples to the front
            int remain = size - n;
            if (remain > 0) {
                System.arraycopy(buf, n, buf, 0, remain);
            }
            size = remain;

            // Record the chunk start timestamp, then reset for the next chunk window
            lastChunkStartMs = startMs;
            startMs = -1;

            return out;
        }

        // Grow buffer capacity as needed
        private void ensure(int need) {
            if (need <= buf.length) return;
            int cap = buf.length;
            while (cap < need) cap *= 2;
            double[] nbuf = new double[cap];
            System.arraycopy(buf, 0, nbuf, 0, size);
            buf = nbuf;
        }
    }
}
