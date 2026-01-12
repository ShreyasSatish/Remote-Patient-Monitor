package rpm.ui.patient.widgets;

import javafx.scene.layout.BorderPane;
import rpm.ui.EcgCanvas;

public final class EcgPanel extends BorderPane {

    private final EcgCanvas canvas = new EcgCanvas();

    public EcgPanel() {
        setCenter(canvas);
        // canvas resizing: let parent size it
        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());
    }

    public void append(double[] segment) {
        if (segment == null || segment.length == 0) return;
        canvas.appendSamples(segment);
    }
    public void reset() {
        canvas.reset();
    }

}
