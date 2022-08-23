package sakura.kooi.lib.swing.view;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;

public class JReversibleProgressBar extends JProgressBar {
    @Getter
    @Setter
    private boolean reverse = false;

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        if (reverse) {
            g2d.scale(-1, 1);
            g2d.translate(-getWidth(), 0);
        }
        super.paintComponent(g2d);
    }
}
