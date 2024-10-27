package org.example.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Enumeration;

@UtilityClass
public class CompressUtils {
    @SneakyThrows
    public static String updateZipFile(String pathToZip, String pathToFile, String text) {
        Path path = Paths.get(pathToFile);

        unzipFile(pathToZip, path);

        String newContent = StringUtils.LF + text;
        Files.write(path, newContent.getBytes(), StandardOpenOption.APPEND);

        zipFile(pathToZip, path);

        return Files.readAllLines(path).toString();
    }

    @SneakyThrows
    public static String createNewZipFile(String pathToZip, String pathToFile, String text) {
        Path path = Paths.get(pathToFile);

        if (FileUtils.doesFileExist(pathToFile)) {
            String newContent = StringUtils.LF + text;
            Files.write(path, newContent.getBytes(), StandardOpenOption.APPEND);
        } else {
            Files.write(path, text.getBytes(), StandardOpenOption.CREATE);
        }

        zipFile(pathToZip, path);

        return Files.readAllLines(path).toString();
    }

    @SneakyThrows
    private static void zipFile(String pathToZip, Path pathToFile) {
        File file = pathToFile.toFile();
        try (ZipArchiveOutputStream archive = new ZipArchiveOutputStream(new FileOutputStream(pathToZip));
             FileInputStream fis = new FileInputStream(file)) {

            ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(file, file.getName());
            archive.putArchiveEntry(zipArchiveEntry);

            IOUtils.copy(fis, archive);

            archive.closeArchiveEntry();
            archive.finish();
        }
    }

    @SneakyThrows
    private static void unzipFile(String pathToZip, Path pathToFile) {
        try (ZipFile zipFile = new ZipFile(pathToZip)) {
            Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
            while (entries.hasMoreElements()) {
                ZipArchiveEntry entry = entries.nextElement();
                Path entryDestination = pathToFile.getParent().resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(entryDestination);
                } else {
                    Files.createDirectories(entryDestination.getParent());
                    try (InputStream in = zipFile.getInputStream(entry)) {
                        Files.copy(in, entryDestination, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
        }
    }
}