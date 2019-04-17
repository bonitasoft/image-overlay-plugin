/**
 * Copyright (C) 2014 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.assertj.core.api.Condition;
import org.bonitasoft.studio.maven.plugin.exception.CreateImageException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Romain Bioteau
 */
@RunWith(MockitoJUnitRunner.class)
public class SetImageVersionTest {

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    private SetImageVersion setImageVersion;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        setImageVersion = spy(new SetImageVersion());
    }

    @Test(expected = IllegalArgumentException.class)
    public void must_have_a_valid_configuration() throws Exception {
        setImageVersion.createImage();
    }

    @Test(expected = IllegalArgumentException.class)
    public void must_have_a_baseImgPath() throws Exception {
        setImageVersion.setBaseImgPath(null);
        setImageVersion.setxLocation(200);
        setImageVersion.setyLocation(200);
        setImageVersion.setVersionLabel("6.4.0");
        setImageVersion.setOutputImageFormat("bmp");
        setImageVersion.setOutputImagePath(tmpFolder.newFile("splash.bmp").getAbsolutePath());
        setImageVersion.createImage();
    }

    @Test(expected = IllegalArgumentException.class)
    public void must_have_a_versionLabel() throws Exception {
        setImageVersion
                .setBaseImgPath(
                        new File(SetImageVersionTest.class.getResource("/splash_sp_without_version.bmp").getFile())
                                .getAbsolutePath());
        setImageVersion.setxLocation(200);
        setImageVersion.setyLocation(200);
        setImageVersion.setVersionLabel(null);
        setImageVersion.setOutputImageFormat("bmp");
        setImageVersion.setOutputImagePath(tmpFolder.newFile("splash.bmp").getAbsolutePath());
        setImageVersion.createImage();
    }

    @Test(expected = IllegalArgumentException.class)
    public void must_have_a_outputImageFormat() throws Exception {
        setImageVersion
                .setBaseImgPath(
                        new File(SetImageVersionTest.class.getResource("/splash_sp_without_version.bmp").getFile())
                                .getAbsolutePath());
        setImageVersion.setxLocation(200);
        setImageVersion.setyLocation(200);
        setImageVersion.setVersionLabel("6.4.0");
        setImageVersion.setOutputImageFormat(null);
        setImageVersion.setOutputImagePath(tmpFolder.newFile("splash.bmp").getAbsolutePath());
        setImageVersion.createImage();
    }

    @Test(expected = IllegalArgumentException.class)
    public void must_have_a_outputImagePath() throws Exception {
        setImageVersion
                .setBaseImgPath(
                        new File(SetImageVersionTest.class.getResource("/splash_sp_without_version.bmp").getFile())
                                .getAbsolutePath());
        setImageVersion.setxLocation(200);
        setImageVersion.setyLocation(200);
        setImageVersion.setVersionLabel("6.4.0");
        setImageVersion.setOutputImageFormat("bmp");
        setImageVersion.setOutputImagePath(null);
        setImageVersion.createImage();
    }

    @Test
    public void create_image_write_a_bmp_valid_file() throws Exception {
        setImageVersion
                .setBaseImgPath(
                        new File(SetImageVersionTest.class.getResource("/splash_sp_without_version.bmp").getFile())
                                .getAbsolutePath());
        setImageVersion.setxLocation(200);
        setImageVersion.setyLocation(200);
        setImageVersion.setVersionLabel("6.4.0");
        setImageVersion.setOutputImageFormat("png");
        File newFile = tmpFolder.newFile("splash.bmp");
        setImageVersion.setOutputImagePath(newFile.getAbsolutePath());
        setImageVersion.createImage();
        assertThat(newFile).exists()
                .canRead()
                .doesNotHave(emptyFile());

        setImageVersion.setVersionLabel("6.4.1");
        setImageVersion.createImage();
        assertThat(newFile).exists()
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
    public void create_image_write_a_valid_png_file() throws Exception {
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
        File newFile = tmpFolder.newFile("splash.png");
        setImageVersion.setOutputImagePath(newFile.getAbsolutePath());
        setImageVersion.createImage();
        assertThat(newFile).exists().canRead();
    }

    @Test
    public void configure_set_valid_output_img_format() throws Exception {
        setImageVersion.configure();
        assertThat(setImageVersion.getFontName()).isEqualTo(SetImageVersion.DEFAULT_FONT_NAME);
    }

    @Test(expected = CreateImageException.class)
    public void create_image_throw_CreateImageException_if_baseImgPath_points_to_invalid_file() throws Exception {
        setImageVersion.setBaseImgPath("/not_existing.bmp");
        setImageVersion.setxLocation(200);
        setImageVersion.setyLocation(200);
        setImageVersion.setVersionLabel("6.4.0");
        setImageVersion.setOutputImageFormat("bmp");
        setImageVersion.setOutputImagePath(tmpFolder.newFile("splash.bmp").getAbsolutePath());
        setImageVersion.createImage();
    }

    @Test(expected = CreateImageException.class)
    public void create_image_throw_CreateImageException_if_font_is_invalid() throws Exception {
        doNothing().when(setImageVersion).configure();
        final String absolutePath = new File(
                SetImageVersionTest.class.getResource("/splash_sp_without_version.bmp").getFile())
                        .getAbsolutePath();
        setImageVersion.setBaseImgPath(absolutePath);
        setImageVersion.setxLocation(200);
        setImageVersion.setyLocation(200);
        setImageVersion.setVersionLabel("6.4.0");
        setImageVersion.setOutputImageFormat("bmp");
        setImageVersion.setOutputImagePath(tmpFolder.newFile("splash.bmp").getAbsolutePath());
        setImageVersion.setFontName("invalid");
        setImageVersion.setFontResourcePath(absolutePath);
        setImageVersion.createImage();
    }

    @Test
    public void should_format_version_to_3_digits_if_snapshot() throws Exception {
        assertThat(setImageVersion.format("1.0.0-SNAPSHOT")).isEqualTo("1.0.0");
    }

    @Test
    public void should_format_version_to_3_digits_if_tag() throws Exception {
        assertThat(setImageVersion.format("1.0.0.myTagId")).isEqualTo("1.0.0");
    }

    @Test
    public void should_format_version_to_3_digits_if_tag_with_specialChars() throws Exception {
        assertThat(setImageVersion.format("1.0.0.myTag-Id")).isEqualTo("1.0.0");
    }
    
    @Test
    public void should_trim_dot() throws Exception {
        assertThat(setImageVersion.trimDot("1.0.0.")).isEqualTo("1.0.0");
    }

    @Test
    public void should_throw_IllegalArgumentException_if_version_format_is_not_supported() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        setImageVersion.format("1.0-SNAPSHOT");
    }

}
