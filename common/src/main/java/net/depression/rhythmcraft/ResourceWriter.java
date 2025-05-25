package net.depression.rhythmcraft;

import dev.architectury.platform.Platform;
import net.depression.Depression;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ResourceWriter {
    public static void write() {
        // 复制谱面资源
        File chartFolder = new File(Platform.getGameFolder() + "/rc_charts");
        URL chartURL = ResourceWriter.class.getResource("/assets/depression/rc_charts/");
        if (chartURL == null) {
            Depression.LOGGER.error("Failed to copy Rhythmcraft resources. Chart folder not found in jar file.");
            return;
        }
        chartFolder.mkdirs();
        try {
            if (chartURL.getProtocol().equals("file")) {
                // 开发环境，资源作为文件存在
                File chartFolderFile = new File(chartURL.toURI());
                copyDirectory(chartFolderFile, chartFolder);
            }
            else if (chartURL.getProtocol().equals("jar")) {
                // 运行环境，资源在jar包中
                String jarPath = ResourceWriter.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
                try (JarFile jar = new JarFile(jarPath)) {
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String entryName = entry.getName();
                        if (entryName.startsWith("assets/depression/rc_charts/")) {
                            if (entry.isDirectory()) {
                                copyFile(jar, entry, chartFolder);
                            }
                            else {
                                String fileName = entryName.substring("assets/depression/rc_charts/".length());
                                if (fileName.contains("/")) {
                                    File parent = new File(chartFolder, fileName.substring(0, fileName.lastIndexOf('/')));
                                    if (!parent.exists()) {
                                        parent.mkdirs();
                                    }
                                    copyFile(jar, entry, new File(parent, fileName.substring(fileName.lastIndexOf('/') + 1)));
                                }
                            }
                        }
                    }
                }
            }
            else {
                Depression.LOGGER.error("Failed to copy Chart resources. Unknown protocol: " + chartURL.getProtocol());
            }
        }
        catch (Exception e) {
            Depression.LOGGER.error("Failed to copy Chart resources.", e);
        }

        // 复制音乐资源
        File songFolder = new File(Platform.getGameFolder() + "/rc_songs");
        URL songURL = ResourceWriter.class.getResource("/assets/depression/rc_songs/");
        if (songURL == null) {
            Depression.LOGGER.error("Failed to copy Rhythmcraft resources. Songs folder not found in jar file.");
            return;
        }
        songFolder.mkdirs();
        try {
            if (songURL.getProtocol().equals("file")) {
                // 开发环境，资源作为文件存在
                File songFolderFile = new File(songURL.toURI());
                copyDirectory(songFolderFile, songFolder);
            }
            else if (songURL.getProtocol().equals("jar")) {
                // 运行环境，资源在jar包中
                String jarPath = ResourceWriter.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
                try (JarFile jar = new JarFile(jarPath)) {
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String entryName = entry.getName();
                        if (!entry.isDirectory() && entryName.startsWith("assets/depression/rc_songs/")) {
                            String fileName = entryName.substring("assets/depression/rc_songs/".length());
                            copyFile(jar, entry, new File(songFolder, fileName));
                        }
                    }
                }
            }
            else {
                Depression.LOGGER.error("Failed to copy Rhythmcraft resources. Unknown protocol: " + songURL.getProtocol());
            }
        }
        catch (Exception e) {
            Depression.LOGGER.error("Failed to copy Rhythmcraft resources.", e);
        }
    }

    public static void copyFile(JarFile jar, JarEntry entry, File outputFile) throws IOException {
        if (outputFile.exists()) {
            return;
        }
        // 复制文件
        try (InputStream inputStream = jar.getInputStream(entry);
             OutputStream outputStream = new FileOutputStream(outputFile)) {
            IOUtils.copy(inputStream, outputStream);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copyDirectory(File sourceDir, File destDir) throws IOException {
        if (!destDir.exists()) {
            return;
        }

        File[] files = sourceDir.listFiles();
        if (files != null) {
            for (File file : files) {
                File destFile = new File(destDir, file.getName());
                if (file.isDirectory()) {
                    copyDirectory(file, destFile);
                } else {
                    Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }
}
