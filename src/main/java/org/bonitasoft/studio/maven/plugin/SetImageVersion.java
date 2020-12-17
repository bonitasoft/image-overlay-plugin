/*
 * Copyright (C) 2009 - 2020 Bonitasoft S.A.
 * Bonitasoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonitasoft.studio.maven.plugin;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.imageio.ImageIO;

import org.bonitasoft.studio.maven.plugin.exception.CreateImageException;

/**
 * @author Romain Bioteau
 */
public class SetImageVersion {

    protected static final String DEFAULT_FONT_NAME = "HelveticaNeueLTStd-Cn";

    private String baseImgPath;
    private String fontName;
    private String fontResourcePath;
    private String versionLabel;
    private String buildId;
    private int buildIdX;
    private int buildIdY;
    private String outputImageFormat; //bmp,jpg,png..
    private int xLocation;
    private int yLocation;
    private String outputImagePath;
    private boolean isBold = false;
    private boolean isItalic = false;

    private float size;
    private String color = "#ffffff"; //white

    public void createImage() throws CreateImageException {
        configure();
        checkArgumentsNotNull(baseImgPath, "baseImgPath");
        checkArgumentsNotNull(fontName, "fontName");
        checkArgumentsNotNull(versionLabel, "versionLabel");
        checkArgumentsNotNull(outputImagePath, "outputImagePath");
        checkArgumentsNotNull(outputImageFormat, "outputImageFormat");

        Font font;
        try {
            font = createCustomFont();
        } catch (FontFormatException | IOException e) {
            throw new CreateImageException("Failed to load font.", e);
        }

        BufferedImage loadImg = null;
        try {
            loadImg = loadBaseImage();
        } catch (final IOException e) {
            throw new CreateImageException(e.getMessage(), e);
        }

        BufferedImage img = new BufferedImage(
                loadImg.getWidth(), loadImg.getHeight(), getType());
        drawProductVersion(loadImg, img, font);
        try {
            writeOutputImage(img);
        } catch (final IOException e) {
            throw new CreateImageException(e.getMessage(), e);
        }
    }

    private void drawProductVersion(BufferedImage loadImg, BufferedImage img, Font bontitaBrandingFont) {
        Graphics2D graphics = img.createGraphics();
        graphics.drawImage(loadImg, 0, 0, loadImg.getWidth(), loadImg.getHeight(), null);

        graphics.setColor(Color.decode(color));
        graphics.setFont(configureVersionFontStyle(bontitaBrandingFont));
        graphics.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        graphics.drawString(stripSNAPSHOT(getVersionLabel()), getxLocation(), getyLocation());
        if (buildId != null && !buildId.isEmpty()) {
            graphics.setFont(configureQualifierFontStyle(bontitaBrandingFont));
            graphics.drawString(String.format("Build: %s", trimDot(buildId)), buildIdX, buildIdY);
        }
        graphics.dispose();
    }

    String trimDot(String label) {
        return label.lastIndexOf(".") == label.length() - 1 ? label.substring(0, label.length() - 1) : label;
    }

    private int getType() {
        return Objects.equals(getOutputImageFormat().toLowerCase(), "bmp") ? BufferedImage.TYPE_INT_RGB
                : BufferedImage.TYPE_INT_ARGB;
    }

    protected void writeOutputImage(final BufferedImage loadImg) throws IOException {
        if (!ImageIO.write(loadImg, getOutputImageFormat(), new File(getOutputImagePath()))) {
            throw new IOException(String.format("Failed to create image %s with %s fromat.", getOutputImagePath(),
                    getOutputImageFormat()));
        }
    }

    private BufferedImage loadBaseImage() throws IOException {
        final File inputFile = getBaseImageFile();
        return ImageIO.read(inputFile);
    }

    private void checkArgumentsNotNull(final Object arg, final String argName) {
        if (arg == null) {
            throw new IllegalArgumentException(argName + " must be set.");
        }
    }

    protected void configure() {
        if (fontName == null) {
            setFontName(DEFAULT_FONT_NAME);
        }
        if (size == 0) {
            setSize(32);
        }
    }

    private InputStream getDefaultFontInputStream() {
        return SetImageVersion.class.getResourceAsStream("/HelveticaNeueLTStd-Cn.otf");
    }

    private File getBaseImageFile() throws FileNotFoundException {
        final File inputFile = new File(getBaseImgPath());
        if (!inputFile.exists()) {
            throw new FileNotFoundException(inputFile.getAbsolutePath());
        }
        return inputFile;
    }

    public String getBaseImgPath() {
        return baseImgPath;
    }

    public void setBaseImgPath(final String baseImgPath) {
        this.baseImgPath = baseImgPath;
    }

    protected Font configureVersionFontStyle(final Font bontitaBrandingFont) {
        final Map<TextAttribute, Object> attributes = new HashMap<>();
        attributes.put(TextAttribute.WIDTH, TextAttribute.WIDTH_SEMI_CONDENSED);
        attributes.put(TextAttribute.BACKGROUND, Transparency.TRANSLUCENT);
        if (isItalic) {
            attributes.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
        }
        if (isBold) {
            attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_MEDIUM);
        } else {
            attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_LIGHT);
        }
        return bontitaBrandingFont.deriveFont(Font.TRUETYPE_FONT, getSize()).deriveFont(attributes);
    }

    protected Font configureQualifierFontStyle(final Font bontitaBrandingFont) {
        final Map<TextAttribute, Object> attributes = new HashMap<>();
        attributes.put(TextAttribute.WIDTH, TextAttribute.WIDTH_SEMI_CONDENSED);
        attributes.put(TextAttribute.BACKGROUND, Transparency.TRANSLUCENT);
        attributes.put(TextAttribute.POSTURE, TextAttribute.POSTURE_REGULAR);
        attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_MEDIUM);
        return bontitaBrandingFont.deriveFont(Font.TRUETYPE_FONT, 15).deriveFont(attributes);
    }

    private Font createCustomFont() throws FontFormatException, IOException {
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font customFont = getFont(ge);
        if (customFont == null) {
            try (InputStream fontInputStream = getFontResourcePath() != null
                    ? new FileInputStream(new File(getFontResourcePath()))
                    : getDefaultFontInputStream()) {
                customFont = Font.createFont(Font.TRUETYPE_FONT, fontInputStream);
                ge.registerFont(customFont);
            }
        }
        return customFont;
    }

    private Font getFont(final GraphicsEnvironment ge) {
        for (final Font f : ge.getAllFonts()) {
            if (getFontName().equals(f.getName())) {
                return f;
            }
        }
        return null;
    }

    String stripSNAPSHOT(String version) {
        if (version != null && version.endsWith("-SNAPSHOT")) {
            version = version.substring(0, version.indexOf("-SNAPSHOT"));
        }
        return version;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(final String fontName) {
        this.fontName = fontName;
    }

    public String getVersionLabel() {
        return versionLabel;
    }

    public void setVersionLabel(final String versionLabel) {
        this.versionLabel = versionLabel;
    }

    public String getOutputImagePath() {
        return outputImagePath;
    }

    public void setOutputImagePath(final String outputImagePath) {
        this.outputImagePath = outputImagePath;
    }

    public void setOutputImageFormat(final String outputImageFormat) {
        this.outputImageFormat = outputImageFormat;
    }

    public String getOutputImageFormat() {
        return outputImageFormat;
    }

    public int getxLocation() {
        return xLocation;
    }

    public void setxLocation(final int xLocation) {
        this.xLocation = xLocation;
    }

    public int getyLocation() {
        return yLocation;
    }

    public void setyLocation(final int yLocation) {
        this.yLocation = yLocation;
    }

    public String getFontResourcePath() {
        return fontResourcePath;
    }

    public void setFontResourcePath(final String fontResourcePath) {
        this.fontResourcePath = fontResourcePath;
    }

    public void setColor(final String color) {
        this.color = color;
    }

    public float getSize() {
        return size;
    }

    public void setSize(final float size) {
        this.size = size;
    }

    public boolean isBold() {
        return isBold;
    }

    public void setBold(final boolean isBold) {
        this.isBold = isBold;
    }

    public void setItalic(final boolean isItalic) {
        this.isItalic = isItalic;
    }

    public void setBuildIdX(int buildIdX) {
        this.buildIdX = buildIdX;
    }

    public void setBuildIdY(int buildIdY) {
        this.buildIdY = buildIdY;
    }

    public void setBuildId(String buildId) {
        this.buildId = buildId;
    }
}
