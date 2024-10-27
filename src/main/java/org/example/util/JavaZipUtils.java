package org.example.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@UtilityClass
public class JavaZipUtils {
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
//            String newContent = "\n" + text;
            String newContent = StringUtils.LF + text;
            Files.write(path, newContent.getBytes(), StandardOpenOption.APPEND);
        } else {
            Files.write(path, text.getBytes(), StandardOpenOption.CREATE);
        }

        zipFile(pathToZip, path);

        return Files.readAllLines(path).toString();
    }

    private static void zipFile(String pathToZip, Path path) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(Paths.get(pathToZip)))) {
//            String getFileName = pathToFile.substring(2); // divide ./
            String getFileName = path.getFileName().toString();
            zos.putNextEntry(new ZipEntry(getFileName));
            Files.copy(path, zos);
            zos.closeEntry();
        }
    }

    private static void unzipFile(String pathToZip, Path pathToFile) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(Paths.get(pathToZip)))) {
            ZipEntry zipEntry = zis.getNextEntry();

            while (zipEntry != null) {
                Path resolvedPath = pathToFile.getParent().resolve(zipEntry.getName());

                if (zipEntry.isDirectory()) {
                    Files.createDirectories(resolvedPath);
                } else {
                    // If the parent directory does not exist, create it
                    if (Files.notExists(resolvedPath.getParent())) {
                        Files.createDirectories(resolvedPath.getParent());
                    }
                    // Copy files, the StandardCopyOption.REPLACE_EXISTING argument replaces the file if it already exists
                    Files.copy(zis, resolvedPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zipEntry = zis.getNextEntry();
            }
        }
    }
}