package sakura.kooi.lib.swing.ui;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

/**
 * @author Konstantin Bulenkov
 */
public class DarculaProgressBarUI extends BasicProgressBarUI {
    private static final Color TRACK_COLOR = new Color(0xe0e0e0);
    private static final Color PROGRESS_COLOR = new Color(0xbdbdbd);
    private static final Color INDETERMINATE_START_COLOR = new Color(0xe0e0e0);
    private static final Color INDETERMINATE_END_COLOR = new Color(0xbdbdbd);

    private static final Color FAILED_COLOR = new Color(0xf44336);
    private static final Color FAILED_END_COLOR = new Color(0xff6d00);
    private static final Color PASSED_COLOR = new Color(0x76ff03);
    private static final Color PASSED_END_COLOR = new Color(0xc6ff00);

    private static final int CYCLE_TIME_DEFAULT = 800;
    private static final int REPAINT_INTERVAL_DEFAULT = 50;

    private static final int CYCLE_TIME_SIMPLIFIED = 1000;
    private static final int REPAINT_INTERVAL_SIMPLIFIED = 500;
    private static final int ourCycleTime = isSimplified() ? CYCLE_TIME_SIMPLIFIED : CYCLE_TIME_DEFAULT;
    private static final int ourRepaintInterval = isSimplified() ? REPAINT_INTERVAL_SIMPLIFIED : REPAINT_INTERVAL_DEFAULT;

    private static final int DEFAULT_WIDTH = 4;

    @SuppressWarnings({"MethodOverridesStaticMethodOfSuperclass", "UnusedDeclaration"})
    public static ComponentUI createUI(JComponent c) {
        return new DarculaProgressBarUI();
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        UIManager.put("ProgressBar.repaintInterval", ourRepaintInterval);
        UIManager.put("ProgressBar.cycleTime", ourCycleTime);
    }

    public void updateIndeterminateAnimationIndex(long startMillis) {
        int numFrames = ourCycleTime / ourRepaintInterval;
        long timePassed = System.currentTimeMillis() - startMillis;
        setAnimationIndex((int) ((timePassed / ourRepaintInterval) % numFrames));
    }

    @Override
    protected void paintIndeterminate(Graphics g, JComponent c) {

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);

            Rectangle r = new Rectangle(progressBar.getSize());
            if (c.isOpaque()) {
                g2.setColor(c.getParent().getBackground());
                g2.fill(r);
            }

            Insets i = progressBar.getInsets();

            removeFrom(r, i);
            int orientation = progressBar.getOrientation();

            // Use foreground color as a reference, don't use it directly. This is done for compatibility reason.
            // Colors are hardcoded in UI delegates by design. If more colors are needed contact designers.
            Color startColor, endColor;
            Color foreground = progressBar.getForeground();
            if (foreground == Color.RED) {
                startColor = FAILED_COLOR;
                endColor = FAILED_END_COLOR;
            } else if (foreground == Color.GREEN) {
                startColor = PASSED_COLOR;
                endColor = PASSED_END_COLOR;
            } else {
                startColor = getStartColor();
                endColor = getEndColor();
            }

            int pHeight = progressBar.getPreferredSize().height;
            int pWidth = progressBar.getPreferredSize().width;

            int yOffset = r.y + (r.height - pHeight) / 2;
            int xOffset = r.x + (r.width - pWidth) / 2;

            if (isSimplified()) {
                Color[] ca = {startColor, endColor};
                int idx = 0;
                int delta = scale(10);
                if (orientation == SwingConstants.HORIZONTAL) {
                    for (float offset = r.x; offset - r.x < r.width; offset += delta) {
                        g2.setPaint(ca[(getAnimationIndex() + idx++) % 2]);
                        g2.fill(new Rectangle2D.Float(offset, yOffset, delta, pHeight));
                    }
                } else {
                    for (float offset = r.y; offset - r.y < r.height; offset += delta) {
                        g2.setPaint(ca[(getAnimationIndex() + idx++) % 2]);
                        g2.fill(new Rectangle2D.Float(xOffset, offset, delta, pWidth));
                    }
                }
            } else {
                Shape shape;
                int step = scale(6);
                if (orientation == SwingConstants.HORIZONTAL) {
                    shape = getShapedRect(r.x, yOffset, r.width, pHeight, pHeight);
                    yOffset = r.y + pHeight / 2;
                    g2.setPaint(new GradientPaint(r.x + getAnimationIndex() * step * 2, yOffset, startColor,
                            r.x + getFrameCount() * step + getAnimationIndex() * step * 2, yOffset, endColor, true));
                } else {
                    shape = getShapedRect(xOffset, r.y, pWidth, r.height, pWidth);
                    xOffset = r.x + pWidth / 2;
                    g2.setPaint(new GradientPaint(xOffset, r.y + getAnimationIndex() * step * 2, startColor,
                            xOffset, r.y + getFrameCount() * step + getAnimationIndex() * step * 2, endColor, true));
                }
                g2.fill(shape);
            }

            // Paint text
            if (progressBar.isStringPainted()) {
                if (progressBar.getOrientation() == SwingConstants.HORIZONTAL) {
                    paintString((Graphics2D) g, i.left, i.top, r.width, r.height, boxRect.x, boxRect.width);
                } else {
                    paintString((Graphics2D) g, i.left, i.top, r.width, r.height, boxRect.y, boxRect.height);
                }
            }
        } finally {
            g2.dispose();
        }
    }

    private void removeFrom(Rectangle dimension, Insets insets) {
        if (insets != null) {
            dimension.width -= insets.left + insets.right;
            dimension.height -= insets.top + insets.bottom;

        }
    }

    protected Color getStartColor() {
        return INDETERMINATE_START_COLOR;
    }

    protected Color getEndColor() {
        return INDETERMINATE_END_COLOR;
    }

    private void paintString(Graphics2D g, int x, int y, int w, int h, int fillStart, int amountFull) {
        String progressString = progressBar.getString();
        g.setFont(progressBar.getFont());
        Point renderLocation = getStringPlacement(g, progressString, x, y, w, h);
        Rectangle oldClip = g.getClipBounds();

        if (progressBar.getOrientation() == SwingConstants.HORIZONTAL) {
            g.setColor(getSelectionBackground());
            g.drawString(progressString, renderLocation.x, renderLocation.y);

            g.setColor(getSelectionForeground());
            g.clipRect(fillStart, y, amountFull, h);

            g.drawString(progressString, renderLocation.x, renderLocation.y);
        } else { // VERTICAL
            g.setColor(getSelectionBackground());
            AffineTransform rotate = AffineTransform.getRotateInstance(Math.PI / 2);
            g.setFont(progressBar.getFont().deriveFont(rotate));
            renderLocation = getStringPlacement(g, progressString, x, y, w, h);
            g.drawString(progressString, renderLocation.x, renderLocation.y);

            g.setColor(getSelectionForeground());
            g.clipRect(x, fillStart, w, amountFull);
            g.drawString(progressString, renderLocation.x, renderLocation.y);
        }
        g.setClip(oldClip);
    }

    @Override
    protected void paintDeterminate(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);

            Rectangle r = new Rectangle(progressBar.getSize());
            if (c.isOpaque() && c.getParent() != null) {
                g2.setColor(c.getParent().getBackground());
                g2.fill(r);
            }

            Insets i = progressBar.getInsets();
            removeFrom(r, i);
            int amountFull = getAmountFull(i, r.width, r.height);

            Shape fullShape, coloredShape;
            int orientation = progressBar.getOrientation();
            if (orientation == SwingConstants.HORIZONTAL) {
                int pHeight = progressBar.getPreferredSize().height;
                int yOffset = r.y + (r.height - pHeight) / 2;

                fullShape = getShapedRect(r.x, yOffset, r.width, pHeight, pHeight);
                coloredShape = getShapedRect(r.x, yOffset, amountFull, pHeight, pHeight);
            } else {
                int pWidth = progressBar.getPreferredSize().width;
                int xOffset = r.x + (r.width - pWidth) / 2;

                fullShape = getShapedRect(xOffset, r.y, pWidth, r.height, pWidth);
                coloredShape = getShapedRect(xOffset, r.y, pWidth, amountFull, pWidth);
            }
            g2.setColor(getRemainderColor());
            g2.fill(fullShape);

            // Use foreground color as a reference, don't use it directly. This is done for compatibility reason.
            // Colors are hardcoded in UI delegates by design. If more colors are needed contact designers.
            Color foreground = progressBar.getForeground();
            if (foreground == Color.RED) {
                g2.setColor(FAILED_COLOR);
            } else if (foreground == Color.GREEN) {
                g2.setColor(PASSED_COLOR);
            } else {
                g2.setColor(getFinishedColor());
            }
            g2.fill(coloredShape);

            // Paint text
            if (progressBar.isStringPainted()) {
                paintString(g, i.left, i.top, r.width, r.height, amountFull, i);
            }
        } finally {
            g2.dispose();
        }
    }

    protected Color getRemainderColor() {
        return TRACK_COLOR;
    }

    protected Color getFinishedColor() {
        return PROGRESS_COLOR;
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        Dimension size = super.getPreferredSize(c);
        if (!(c instanceof JProgressBar)) {
            return size;
        }
        if (!((JProgressBar) c).isStringPainted()) {
            if (((JProgressBar) c).getOrientation() == SwingConstants.HORIZONTAL) {
                size.height = getStripeWidth();
            } else {
                size.width = getStripeWidth();
            }
        }
        return size;
    }

    private int getStripeWidth() {
        Object ho = progressBar.getClientProperty("ProgressBar.stripeWidth");
        if (ho != null) {
            try {
                return scale(Integer.parseInt(ho.toString()));
            } catch (NumberFormatException nfe) {
                return scale(DEFAULT_WIDTH);
            }
        } else {
            return scale(DEFAULT_WIDTH);
        }
    }

    public int scale(int i) {
        return Math.round(1f * i);
    }

    @Override
    protected int getBoxLength(int availableLength, int otherDimension) {
        return availableLength;
    }

    private Shape getShapedRect(float x, float y, float w, float h, float ar) {
        boolean flatEnds = progressBar.getClientProperty("ProgressBar.flatEnds") == Boolean.TRUE;
        return flatEnds ? new Rectangle2D.Float(x, y, w, h) : new RoundRectangle2D.Float(x, y, w, h, ar, ar);
    }

    private static boolean isSimplified() {
        // TODO improve user experience based on System.properties
        // Avoid using Services directly to make UI code independent.
        return false;
    }
}