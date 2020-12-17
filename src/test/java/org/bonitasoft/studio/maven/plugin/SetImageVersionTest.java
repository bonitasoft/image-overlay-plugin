/*
 * Copyright (C) 2009 - 2020 Bonitasoft S.A.
 * Bonitasoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.bonitasoft.studio.maven.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

import org.assertj.core.api.Condition;
import org.bonitasoft.studio.maven.plugin.exception.CreateImageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SetImageVersionTest {

    private SetImageVersion setImageVersion;

    @BeforeEach
    public void setUp() throws Exception {
        setImageVersion = spy(new SetImageVersion());
    }

    @Test
    void must_have_a_valid_configuration() throws Exception {
       assertThrows(IllegalArgumentException.class, () -> setImageVersion.createImage());
    }

    @Test
    void must_have_a_baseImgPath(@TempDir Path tmpFolder) throws Exception {
        setImageVersion.setBaseImgPath(null);
        setImageVersion.setxLocation(200);
        setImageVersion.setyLocation(200);
        setImageVersion.setVersionLabel("6.4.0");
        setImageVersion.setOutputImageFormat("bmp");
        setImageVersion.setOutputImagePath(tmpFolder.resolve("splash.bmp").toFile().getAbsolutePath());
        assertThrows(IllegalArgumentException.class, () -> setImageVersion.createImage());
    }

    @Test
    void must_have_a_versionLabel(@TempDir Path tmpFolder) throws Exception {
        setImageVersion
                .setBaseImgPath(
                        new File(SetImageVersionTest.class.getResource("/splash_sp_without_version.bmp").getFile())
                                .getAbsolutePath());
        setImageVersion.setxLocation(200);
        setImageVersion.setyLocation(200);
        setImageVersion.setVersionLabel(null);
        setImageVersion.setOutputImageFormat("bmp");
        setImageVersion.setOutputImagePath(tmpFolder.resolve("splash.bmp").toFile().getAbsolutePath());
        assertThrows(IllegalArgumentException.class, () -> setImageVersion.createImage());
    }

    @Test
    void must_have_a_outputImageFormat(@TempDir Path tmpFolder) throws Exception {
        setImageVersion
                .setBaseImgPath(
                        new File(SetImageVersionTest.class.getResource("/splash_sp_without_version.bmp").getFile())
                                .getAbsolutePath());
        setImageVersion.setxLocation(200);
        setImageVersion.setyLocation(200);
        setImageVersion.setVersionLabel("6.4.0");
        setImageVersion.setOutputImageFormat(null);
        setImageVersion.setOutputImagePath(tmpFolder.resolve("splash.bmp").toFile().getAbsolutePath());
        assertThrows(IllegalArgumentException.class, () -> setImageVersion.createImage());
    }

    @Test
    void must_have_a_outputImagePath() throws Exception {
        setImageVersion
                .setBaseImgPath(
                        new File(SetImageVersionTest.class.getResource("/splash_sp_without_version.bmp").getFile())
                                .getAbsolutePath());
        setImageVersion.setxLocation(200);
        setImageVersion.setyLocation(200);
        setImageVersion.setVersionLabel("6.4.0");
        setImageVersion.setOutputImageFormat("bmp");
        setImageVersion.setOutputImagePath(null);
        assertThrows(IllegalArgumentException.class, () -> setImageVersion.createImage());
    }

    @Test
    void create_image_write_a_bmp_valid_file(@TempDir Path tmpFolder) throws Exception {
        setImageVersion
                .setBaseImgPath(
                        new File(SetImageVersionTest.class.getResource("/splash_sp_without_version.bmp").getFile())
                                .getAbsolutePath());
        setImageVersion.setxLocation(200);
        setImageVersion.setyLocation(200);
        setImageVersion.setVersionLabel("6.4.0");
        setImageVersion.setOutputImageFormat("png");
        File imageFile = tmpFolder.resolve("splash.bmp").toFile();
        setImageVersion.setOutputImagePath(imageFile.getAbsolutePath());
        setImageVersion.createImage();
        assertThat(imageFile).exists()
                .canRead()
                .doesNotHave(emptyFile());

        setImageVersion.setVersionLabel("6.4.1");
        setImageVersion.createImage();
        assertThat(imageFile).exists()
                .canRead()
                .doesNotHave(emptyFile());
    }

    private Condition<File> emptyFile() {
        return new Condition<File>() {

            @Override
            public boolean matches(File value) {
                try (FileInputStream fis = new FileInputStream(value)) {
                    return fis.available() == 0;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Test
    void create_image_write_a_valid_png_file(@TempDir Path tmpFolder) throws Exception {
        setImageVersion.setBaseImgPath(
                new File(SetImageVersionTest.class.getResource("/Logo-Welcome-Community-Without-Version.png").getFile())
                        .getAbsolutePath());
        setImageVersion.setxLocation(192);
        setImageVersion.setyLocation(78);
        setImageVersion.setItalic(true);
        setImageVersion.setBold(true);
        setImageVersion.setSize(35);
        setImageVersion.setColor("#0b4361");
        setImageVersion.setVersionLabel("7.6.0");
        setImageVersion.setOutputImageFormat("png");
        File imageFile = tmpFolder.resolve("splash.png").toFile();
        setImageVersion.setOutputImagePath(imageFile.getAbsolutePath());
        setImageVersion.createImage();
        assertThat(imageFile).exists().canRead();
    }

    @Test
    void configure_set_valid_output_img_format() throws Exception {
        setImageVersion.configure();
        assertThat(setImageVersion.getFontName()).isEqualTo(SetImageVersion.DEFAULT_FONT_NAME);
    }

    @Test
    void create_image_throw_CreateImageException_if_baseImgPath_points_to_invalid_file(@TempDir Path tmpFolder) throws Exception {
        setImageVersion.setBaseImgPath("/not_existing.bmp");
        setImageVersion.setxLocation(200);
        setImageVersion.setyLocation(200);
        setImageVersion.setVersionLabel("6.4.0");
        setImageVersion.setOutputImageFormat("bmp");
        setImageVersion.setOutputImagePath( tmpFolder.resolve("splash.bmp").toFile().getAbsolutePath());
        assertThrows(CreateImageException.class, () -> setImageVersion.createImage());
    }

    @Test
    void create_image_throw_CreateImageException_if_font_is_invalid(@TempDir Path tmpFolder) throws Exception {
        doNothing().when(setImageVersion).configure();
        final String absolutePath = new File(
                SetImageVersionTest.class.getResource("/splash_sp_without_version.bmp").getFile())
                        .getAbsolutePath();
        setImageVersion.setBaseImgPath(absolutePath);
        setImageVersion.setxLocation(200);
        setImageVersion.setyLocation(200);
        setImageVersion.setVersionLabel("6.4.0");
        setImageVersion.setOutputImageFormat("bmp");
        setImageVersion.setOutputImagePath(tmpFolder.resolve("splash.png").toFile().getAbsolutePath());
        setImageVersion.setFontName("invalid");
        setImageVersion.setFontResourcePath(absolutePath);
        assertThrows(CreateImageException.class, () -> setImageVersion.createImage());
    }

    @Test
    void should_strip_version_snapshot() throws Exception {
        assertThat(setImageVersion.stripSNAPSHOT("1.0.0-SNAPSHOT")).isEqualTo("1.0.0");
    }

    @Test
    void should_trim_dot() throws Exception {
        assertThat(setImageVersion.trimDot("1.0.0.")).isEqualTo("1.0.0");
    }

}
