package klinik.view.theme;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.UIManager;

/**
 * Token desain monokrom Swiss (Minimalism) serta pemuat font Atkinson Hyperlegible Next & Mono.
 * Dial ENERGY 1 / RHYTHM 1 / MOTION 1: tanpa animasi, bayangan, atau gradasi.
 */
public final class DesignSystem {
    public static final Color BG_CANVAS = new Color(0xF6, 0xF6, 0xF4);
    public static final Color BG_CARD = new Color(0xFF, 0xFF, 0xFF);
    public static final Color TEXT_PRIMARY = new Color(0x11, 0x11, 0x11);
    public static final Color TEXT_SECONDARY = new Color(0x5F, 0x5F, 0x5F);
    public static final Color TEXT_PLACEHOLDER = new Color(0x88, 0x88, 0x88);
    public static final Color TEXT_DISABLED = new Color(0xB4, 0xB4, 0xB4);
    public static final Color BORDER_SUBTLE = new Color(0xE4, 0xE4, 0xE4);
    public static final Color BORDER_INPUT = new Color(0x9E, 0x9E, 0x9E);
    public static final Color BORDER_FOCUS = new Color(0x11, 0x11, 0x11);
    public static final Color DANGER = new Color(0xC6, 0x28, 0x28);
    public static final Color DANGER_HOVER = new Color(0xA6, 0x1D, 0x1D);
    public static final Color PRIMARY_HOVER = new Color(0x2D, 0x2D, 0x2D);
    public static final Color OUTLINE_HOVER = new Color(0xF6, 0xF6, 0xF4);
    public static final Color ROW_SELECTED = new Color(0xF0, 0xF0, 0xED);
    public static final Color ROW_HOVER = new Color(0xFA, 0xF9, 0xF7);
    public static final Color BUTTON_DISABLED_BG = new Color(0xEC, 0xEC, 0xEA);
    public static final Color BUTTON_DISABLED_BORDER = new Color(0xD8, 0xD8, 0xD6);

    private static Font nextLight;
    private static Font nextRegular;
    private static Font nextMedium;
    private static Font nextSemiBold;
    private static Font nextBold;
    private static Font monoRegular;
    private static Font monoMedium;
    private static Font monoSemiBold;
    private static Font monoBold;
    private static boolean initialized = false;

    private DesignSystem() {
    }

    public static synchronized void init() {
        if (initialized) {
            return;
        }
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        nextLight = loadFont("AtkinsonHyperlegibleNext-Light.ttf", Font.PLAIN);
        nextRegular = loadFont("AtkinsonHyperlegibleNext-Regular.ttf", Font.PLAIN);
        nextMedium = loadFont("AtkinsonHyperlegibleNext-Medium.ttf", Font.PLAIN);
        nextSemiBold = loadFont("AtkinsonHyperlegibleNext-SemiBold.ttf", Font.BOLD);
        nextBold = loadFont("AtkinsonHyperlegibleNext-Bold.ttf", Font.BOLD);

        monoRegular = loadFont("AtkinsonHyperlegibleMono-Regular.ttf", Font.PLAIN);
        monoMedium = loadFont("AtkinsonHyperlegibleMono-Medium.ttf", Font.PLAIN);
        monoSemiBold = loadFont("AtkinsonHyperlegibleMono-SemiBold.ttf", Font.BOLD);
        monoBold = loadFont("AtkinsonHyperlegibleMono-Bold.ttf", Font.BOLD);

        initialized = true;
        applyGlobalUiDefaults();
    }

    private static Font loadFont(String fileName, int fallbackStyle) {
        Font loaded = null;
        try (InputStream is = DesignSystem.class.getResourceAsStream("/fonts/" + fileName)) {
            if (is != null) {
                loaded = Font.createFont(Font.TRUETYPE_FONT, is);
            }
        } catch (FontFormatException | IOException ignored) {
        }
        if (loaded == null) {
            Path diskPath = Paths.get("resources", "fonts", fileName);
            if (Files.exists(diskPath)) {
                try (InputStream is = Files.newInputStream(diskPath)) {
                    loaded = Font.createFont(Font.TRUETYPE_FONT, is);
                } catch (FontFormatException | IOException ignored) {
                }
            }
        }
        if (loaded != null) {
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(loaded);
            return loaded;
        }
        String fallbackFamily = fileName.contains("Mono") ? Font.MONOSPACED : Font.SANS_SERIF;
        return new Font(fallbackFamily, fallbackStyle, 13);
    }

    private static void applyGlobalUiDefaults() {
        Font base = fontRegular(13f);
        UIManager.put("Panel.background", BG_CANVAS);
        UIManager.put("Label.font", base);
        UIManager.put("Label.foreground", TEXT_PRIMARY);
        UIManager.put("TextField.font", base);
        UIManager.put("PasswordField.font", base);
        UIManager.put("TextArea.font", base);
        UIManager.put("ComboBox.font", base);
        UIManager.put("Button.font", fontMedium(13f));
        UIManager.put("ToggleButton.font", fontMedium(13f));
        UIManager.put("Table.font", base);
        UIManager.put("TableHeader.font", fontMedium(12f));
        UIManager.put("ToolTip.background", TEXT_PRIMARY);
        UIManager.put("ToolTip.foreground", BG_CARD);
        UIManager.put("ToolTip.font", fontRegular(12f));
        UIManager.put("OptionPane.background", BG_CARD);
        UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);
    }

    public static Font fontLight(float size) {
        ensureInit();
        return nextLight.deriveFont(size);
    }

    public static Font fontRegular(float size) {
        ensureInit();
        return nextRegular.deriveFont(size);
    }

    public static Font fontMedium(float size) {
        ensureInit();
        return nextMedium.deriveFont(size);
    }

    public static Font fontSemiBold(float size) {
        ensureInit();
        return nextSemiBold.deriveFont(size);
    }

    public static Font fontBold(float size) {
        ensureInit();
        return nextBold.deriveFont(size);
    }

    public static Font monoRegular(float size) {
        ensureInit();
        return monoRegular.deriveFont(size);
    }

    public static Font monoMedium(float size) {
        ensureInit();
        return monoMedium.deriveFont(size);
    }

    public static Font monoSemiBold(float size) {
        ensureInit();
        return monoSemiBold.deriveFont(size);
    }

    public static Font monoBold(float size) {
        ensureInit();
        return monoBold.deriveFont(size);
    }

    public static void applyQualityHints(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }

    private static void ensureInit() {
        if (!initialized) {
            init();
        }
    }
}
