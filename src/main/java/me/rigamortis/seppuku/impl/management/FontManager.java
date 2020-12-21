package me.rigamortis.seppuku.impl.management;

import com.client.glowclient.utils.render.fonts.CFont;
import com.client.glowclient.utils.render.fonts.MinecraftFontRenderer;
import net.minecraft.client.Minecraft;

import java.awt.*;
import java.io.IOException;

/**
 * @author cats
 */
public final class FontManager {

    private final Minecraft mc = Minecraft.getMinecraft();

    private final boolean useStandardFont = false;

    public final MinecraftFontRenderer fontRenderer = Fonts.VERDANA;

    public void drawString(String text, float x, float y, int color, boolean shadow, boolean centered) {
        if (useStandardFont) {
            if (shadow) {
                mc.fontRenderer.drawStringWithShadow(text, x, y, color);
                return;
            }
            mc.fontRenderer.drawString(text, Math.round(x), Math.round(y), color);
        } else {
            if (shadow && centered) {
                fontRenderer.drawCenteredStringWithShadow(text, x, y, color);
                return;
            }

            if (shadow) {
                fontRenderer.drawStringWithShadow(text, x, y, color);
                return;
            }

            if (centered) {
                fontRenderer.drawCenteredString(text, x, y, color);
                return;
            }

            fontRenderer.drawString(text, x, y, color);
        }
    }

    public void formatString(String string, Double width) {
        if (!useStandardFont) {
            fontRenderer.formatString(string, width);
        }
    }


    public void setAntiAlias(boolean antiAlias) {
        if (!useStandardFont) {
            fontRenderer.setAntiAlias(antiAlias);
        }
    }

    public void setFont(Font font) {
        if (!useStandardFont) {
            fontRenderer.setFont(font);
        }
    }

    public void setFractionalMetrics(boolean fractionalMetrics) {
        if (!useStandardFont) {
            fontRenderer.setFractionalMetrics(fractionalMetrics);
        }
    }

    public void wrapWords(String text, Double width) {
        if (!useStandardFont) {
            fontRenderer.wrapWords(text, width);
        }
    }

    public void drawChar(CFont.CharData[] charData, char c, float x, float y) {
        if (!useStandardFont) {
            fontRenderer.drawChar(charData, c, x, y);
        }
    }

    public void drawSplitString(String string, int x, int y, int wrapWidth, int textColor) {
        mc.fontRenderer.drawSplitString(string, x, y, wrapWidth, textColor);
    }

    //These are used for getting values
    public MinecraftFontRenderer getFont() {
        return fontRenderer;
    }

    public Integer getHeight() {
        if (useStandardFont) {
            return mc.fontRenderer.FONT_HEIGHT;
        } else return fontRenderer.getHeight();
    }

    public Integer getStringHeight(String string) {
        return fontRenderer.getStringHeight(string);
    }

    public Integer getStringWidth(String string) {
        if (useStandardFont) {
            return mc.fontRenderer.getStringWidth(string);
        } else {
            return fontRenderer.getStringWidth(string);
        }
    }

    public void drawStringWithShadow(String name, int i, int i1, int color) {
        drawString(name, i, i1, color, true, false);
    }


    public Boolean isAntiAliased() {
        return fontRenderer.isAntiAlias();
    }

    public Boolean isFractionalMetrics() {
        return fontRenderer.isFractionalMetrics();
    }


    public static class Fonts {
        public static MinecraftFontRenderer ARIAL = new MinecraftFontRenderer(new Font("Arial", Font.PLAIN, 18), true, true);
        public static MinecraftFontRenderer VERDANA = new MinecraftFontRenderer(new Font("Verdana", Font.PLAIN, 18), true, true);
        public static MinecraftFontRenderer TREBUCHETMS = new MinecraftFontRenderer(new Font("Trebuchet MS", 0, 18), true, true);
        public static MinecraftFontRenderer CONSOLAS = new MinecraftFontRenderer(new Font("Consolas", Font.PLAIN, 18), true, true);
        public static MinecraftFontRenderer CALIBRI = new MinecraftFontRenderer(new Font("Calibri", Font.PLAIN, 18), true, true);
        public static MinecraftFontRenderer CAMBRIA = new MinecraftFontRenderer(new Font("Cambria", Font.PLAIN, 18), true, true);
        public static MinecraftFontRenderer GEORGIA = new MinecraftFontRenderer(new Font("Georgia", Font.PLAIN, 18), true, true);
        public static MinecraftFontRenderer ARIALBLACK = new MinecraftFontRenderer(new Font("Arial Black", Font.PLAIN, 18), true, true);
        public static MinecraftFontRenderer COMICSANSMS = new MinecraftFontRenderer(new Font("Comic Sans MS", Font.PLAIN, 18), true, true);
        public static MinecraftFontRenderer IMPACT = new MinecraftFontRenderer(new Font("Impact", Font.PLAIN, 18), true, true);
        public static MinecraftFontRenderer VERDANA50 = new MinecraftFontRenderer(new Font("Verdana", Font.PLAIN, 9), true, true);
        public static MinecraftFontRenderer VERDANA75 = new MinecraftFontRenderer(new Font("Verdana", Font.PLAIN, 13), true, true);
        public static MinecraftFontRenderer TITLEFONT = new MinecraftFontRenderer(new Font("Arial", Font.PLAIN, 30), true, true);
        public static MinecraftFontRenderer ImpactFont;

        static {
            try {
                ImpactFont = new MinecraftFontRenderer(Font.createFont(Font.TRUETYPE_FONT, Fonts.class.getResourceAsStream("/Comfortaa.ttf")).deriveFont(Font.PLAIN, 20), true, true);
            } catch (FontFormatException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
