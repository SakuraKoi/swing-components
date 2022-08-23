/*
 * Copyright (C) 2019. SakuraKooi(sakurakoi993519867@gmail.com) - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package sakura.kooi.lib.swing.view;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.*;
import java.awt.*;

public class ColoredTextPane extends JTextPane {
    private static final Color D_Black = Color.getHSBColor(0.000f, 0.000f, 0.000f);
    private static final Color D_Blue = Color.getHSBColor(0.667f, 1.000f, 0.502f);
    private static final Color D_Green = Color.getHSBColor(0.333f, 1.000f, 0.502f);
    private static final Color D_Cyan = Color.getHSBColor(0.500f, 1.000f, 0.502f);
    private static final Color D_Red = Color.getHSBColor(0.000f, 0.800f, 0.802f);
    private static final Color D_Magenta = Color.getHSBColor(0.833f, 1.000f, 0.502f);
    private static final Color D_Yellow = Color.getHSBColor(0.167f, 1.000f, 0.502f);
    private static final Color D_White = Color.getHSBColor(0.000f, 0.000f, 0.753f);
    private static final Color B_Black = Color.getHSBColor(0.000f, 0.000f, 0.502f);
    private static final Color B_Blue = Color.getHSBColor(0.667f, 1.000f, 1.000f);
    private static final Color B_Green = Color.getHSBColor(0.333f, 1.000f, 1.000f);
    private static final Color B_Cyan = Color.getHSBColor(0.500f, 1.000f, 1.000f);
    private static final Color B_Red = Color.getHSBColor(0.000f, 1.000f, 1.000f);
    private static final Color B_Magenta = Color.getHSBColor(0.833f, 1.000f, 1.000f);
    private static final Color B_Yellow = Color.getHSBColor(0.167f, 1.000f, 1.000f);
    private static final Color B_White = Color.getHSBColor(0.000f, 0.000f, 0.950f);

    private static final Color cReset = Color.getHSBColor(0.000f, 0.000f, 0.950f);
    private static Color colorCurrent = cReset;

    private static final Color COLOR_BACKGROUND = Color.getHSBColor(0f, 0f, 0.12f);

    public ColoredTextPane() {
        setBorder(new LineBorder(COLOR_BACKGROUND, 8, false));
        setForeground(Color.WHITE);
        setBackground(COLOR_BACKGROUND);
        setFont(new Font("Consolas", Font.PLAIN, 12));
    }

    private void append(Color color, String content) {
        StyleContext styleContext = StyleContext.getDefaultStyleContext();
        AttributeSet attributeSet = styleContext.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color);
        int length = getDocument().getLength();
        try {
            getDocument().insertString(length, content, attributeSet);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void appendANSI(String ansiColoredText) {
        for (String str : ansiColoredText.split("\u001B\\[")) {
            if (str.indexOf('m') != -1) {
                int m = str.indexOf('m');
                Color c = getANSIColor(str.substring(0, m));
                if (c != null) {
                    colorCurrent = c;
                }
                append(colorCurrent, str.substring(m+1));
            } else {
                append(colorCurrent, str);
            }
        }
    }

    private Color getANSIColor(String ANSIColor) {
        switch (ANSIColor) {
            case "0;30;22": return D_Black; // Black §0
            case "0;34;22": return D_Blue; // Dark Blue §1
            case "0;32;22": return D_Green; // Dark Green §2
            case "0;36;22": return D_Cyan; // Dark Aqua §3
            case "0;31;22": return D_Red; // Dark Red §4
            case "0;35;22": return D_Magenta; // Dark Purple §5
            case "0;33;22": return D_Yellow; // Gold §6
            case "0;37;22": return D_White; // Gray §7
            case "0;30;1": return B_Black; // Dark Gray §8
            case "0;34;1": return B_Blue; // Blue §9
            case "0;32;1": return B_Green; // Green §a
            case "0;36;1": return B_Cyan; // Aqua §b
            case "0;31;1": return B_Red; // Red §c
            case "0;35;1": return B_Magenta; // Light Purple §d
            case "0;33;1": return B_Yellow; // Yellow §e
            case "0;37;1": return B_White; // White §f
            case "39;0": return cReset; // Reset §r
            default:
                return null;
        }
    }
}