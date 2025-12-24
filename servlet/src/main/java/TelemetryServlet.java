import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

//This class sets up the servlet

@WebServlet(urlPatterns = "/telemetry", loadOnStartup = 1)
public class TelemetryServlet extends HttpServlet {

    private final ObjectMapper mapper = new ObjectMapper();

    // Storage backend
    private final InMemoryTelemetryStore store =
            new InMemoryTelemetryStore();

    //Receive telemetry uploads

    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {

        String body = req.getReader()
                .lines()
                .collect(Collectors.joining());

        Map<String, Object> payload =
                mapper.readValue(body, Map.class);

        Map<String, Map<String, Object>> patients =
                (Map<String, Map<String, Object>>)
                        payload.get("patients");

        patients.forEach((bedId, vitals) -> {
            PatientTelemetry pt =
                    mapper.convertValue(
                            vitals,
                            PatientTelemetry.class
                    );
            store.store(bedId, pt);
        });

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        resp.getWriter().write("{\"status\":\"stored\"}");
    }

    //View telemetry data in browser with /telemetry?bed=Bed XX

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json");

        String bed = req.getParameter("bed");

        if (bed != null) {
            mapper.writeValue(
                    resp.getWriter(),
                    store.getAll().get(bed)
            );
        } else {
            //Sort beds alphabetically (Bed 01, Bed 02, ...)
            Map<String, PatientTelemetry> sorted =
                    new TreeMap<>(store.getAll());

            mapper.writeValue(resp.getWriter(), sorted);
        }
    }
}