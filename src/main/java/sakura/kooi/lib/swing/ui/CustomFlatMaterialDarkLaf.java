package sakura.kooi.lib.swing.ui;

import com.formdev.flatlaf.IntelliJTheme;
import com.formdev.flatlaf.util.LoggingFacade;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class CustomFlatMaterialDarkLaf extends IntelliJTheme.ThemeLaf {
    public static final String NAME = "Material Darker Contrast (Material)";

    public static boolean setup() {
        try {
            if (setup(new CustomFlatMaterialDarkLaf())) {
                UIManager.put("ProgressBarUI", "sakura.kooi.lib.swing.ui.DarculaProgressBarUI");
                //UIManager.put("Component.focusColor", "#76ff03");
                UIManager.put("Button.arc", 0);
                UIManager.put("Component.arc", 0);
                UIManager.put("CheckBox.arc", 0);
                UIManager.put("ProgressBar.arc", 0);
                UIManager.put("Component.focusWidth", 0);
                UIManager.put("Component.innerFocusWidth", 0);
                UIManager.put("ScrollBar.trackInsets", new Insets(2, 4, 2, 4));
                UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
                UIManager.put("ScrollBar.track", new Color(0xe0e0e0));
                UIManager.put("TabbedPane.tabsPopupPolicy", "never");
                UIManager.put("TabbedPane.scrollButtonsPlacement", "trailing");
                // UIManager.put("TabbedPane.tabWidthMode", "compact");
                return true;
            }
            return false;
        } catch (RuntimeException var1) {
            return false;
        }
    }

    public CustomFlatMaterialDarkLaf() {
        super(loadTheme());
    }

    static IntelliJTheme loadTheme() {
        try {
            return new IntelliJTheme(Objects.requireNonNull(
                    CustomFlatMaterialDarkLaf.class.getResourceAsStream("/theme-customed.json")
            ));
        } catch (IOException var3) {
            String msg = "FlatLaf: Failed to load IntelliJ theme";
            LoggingFacade.INSTANCE.logSevere(msg, var3);
            throw new RuntimeException(msg, var3);
        }
    }

    public String getName() {
        return "Material Darker Contrast (Material)";
    }
}