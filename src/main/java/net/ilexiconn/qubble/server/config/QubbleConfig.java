package net.ilexiconn.qubble.server.config;

import net.ilexiconn.llibrary.server.config.ConfigEntry;

import java.awt.*;

public class QubbleConfig {
    @ConfigEntry(name = "Accent Color")
    public String accentColor = "0x00746B";

    @ConfigEntry(name = "Dark Mode")
    public boolean darkMode = false;

    public int getPrimaryColor() {
        return this.darkMode ? 0xFF212121 : 0xFFCDCDCD;
    }

    public int getSecondaryColor() {
        return this.darkMode ? 0xFF363636 : 0xFFACACAC;
    }

    public int getTertiaryColor() {
        return this.darkMode ? 0xFF464646 : 0xFFECECEC;
    }

    public int getPrimarySubcolor() {
        return this.darkMode ? 0xFF212121 : 0xFFCDCDCD;
    }

    public int getSecondarySubcolor() {
        return this.darkMode ? 0xFF1F1F1F : 0xFFC2C2C2;
    }

    public int getTextColor() {
        return this.darkMode ? 0xFFFFFFFF : 0xFF000000;
    }

    public int getAccentColor() {
        if (this.accentColor.startsWith("0x")) {
            String hex = this.accentColor.split("0x")[1];
            while (hex.length() < 8) {
                hex = "F" + hex;
            }
            return (int) Long.parseLong(hex, 16);
        } else {
            return Integer.parseInt(this.accentColor);
        }
    }

    public int getDarkerColor(int color) {
        int r = color >> 16 & 255;
        int g = color >> 8 & 255;
        int b = color & 255;
        float[] hsb = Color.RGBtoHSB(r, g, b, null);
        Color newColor = Color.getHSBColor(hsb[0], hsb[1], hsb[2] * 0.85F);
        return (0xFF << 24) | ((newColor.getRed() & 0xFF) << 16) | ((newColor.getGreen() & 0xFF) << 8) | (newColor.getBlue() & 0xFF);
    }
}
