package rpm.ui;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;

/**
 * ECG display panel with a moving sweep that travels left to right.
 *
 * The panel keeps its own circular screen buffer (one value per horizontal pixel).
 * Each time new samples arrive we write them at the current sweep position and
 * advance the sweep. On repaint we draw:
 *   - the ECG trace from that screen buffer, and
 *   - a vertical cursor that moves left to right.
 *
 * Behind the cursor the trace stays as it was last written, and it is only
 * replaced when the cursor comes around again (like a real monitor).
 */
public class EcgPanel extends JPanel {

    /** One value per pixel column on the screen. */
    private double[] screenValues = null;
    private static final double CALIBRATION_MAX_MV = 1.5;

    /** Current sweep position in pixels (0 .. width-1). */
    private int sweepPos = 0;

    public EcgPanel() {
        setBackground(Color.BLACK);
    }

    public void reset() {
        screenValues = null;
        sweepPos = 0;
        repaint();
    }

    public void appendSamples(double[] samples) {
        if (samples == null || samples.length == 0) {
            return;
        }

        int width = getWidth();
        if (width <= 0) {
            return;
        }

        if (screenValues == null || screenValues.length != width) {
            screenValues = new double[width];
            sweepPos = 0;
        }

        for (double v : samples) {
            screenValues[sweepPos] = v;
            sweepPos++;
            if (sweepPos >= screenValues.length) {
                sweepPos = 0;
            }
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int width = getWidth();
        int height = getHeight();

        g.setColor(new Color(40, 40, 40)); // fine grid
        int smallStep = 10;
        for (int x = 0; x < width; x += smallStep) {
            g.drawLine(x, 0, x, height);
        }
        for (int y = 0; y < height; y += smallStep) {
            g.drawLine(0, y, width, y);
        }

        g.setColor(new Color(70, 70, 70)); // thicker grid
        int bigStep = 50;
        for (int x = 0; x < width; x += bigStep) {
            g.drawLine(x, 0, x, height);
        }
        for (int y = 0; y < height; y += bigStep) {
            g.drawLine(0, y, width, y);
        }

        if (screenValues == null || screenValues.length < 2) {
            return;
        }

        int baselineY = height / 2;

        double yScale = (height * 0.4) / CALIBRATION_MAX_MV;  

        // Baseline
        g.setColor(new Color(120, 120, 120));
        g.drawLine(0, baselineY, width, baselineY);

        g.setColor(new Color(0, 255, 80));

        int n = screenValues.length;
        int prevX = 0;
        int prevY = baselineY - (int) Math.round(screenValues[0] * yScale);

        for (int x = 1; x < n; x++) {
            int y = baselineY - (int) Math.round(screenValues[x] * yScale);
            g.drawLine(prevX, prevY, x, y);
            prevX = x;
            prevY = y;
        }

        int gapWidth = 6;
        int gapX = sweepPos;

        g.setColor(getBackground());
        g.fillRect(gapX, 0, gapWidth, height);

        int cursorX = gapX + gapWidth / 2;
        g.setColor(Color.YELLOW);
        g.drawLine(cursorX, 0, cursorX, height);
    }
}
