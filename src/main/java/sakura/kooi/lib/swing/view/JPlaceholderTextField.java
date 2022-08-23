package sakura.kooi.lib.swing.view;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;

public class JPlaceholderTextField extends JTextField {
    @Getter @Setter
    private String placeholder;

    public JPlaceholderTextField() {
    }

    public JPlaceholderTextField(Document pDoc, String pText, int pColumns) {
        super(pDoc, pText, pColumns);
    }

    public JPlaceholderTextField(int pColumns) {
        super(pColumns);
    }

    public JPlaceholderTextField(String pText) {
        super(pText);
    }

    public JPlaceholderTextField(String pText, int pColumns) {
        super(pText, pColumns);
    }

    @Override
    protected void paintComponent(Graphics pG) {
        super.paintComponent(pG);

        if (placeholder == null || placeholder.length() == 0 || getText().length() > 0) {
            return;
        }

        final Graphics2D g = (Graphics2D) pG;
        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(getDisabledTextColor());
        g.drawString(placeholder, getInsets().left, pG.getFontMetrics()
                .getMaxAscent() + getInsets().top);
    }
}