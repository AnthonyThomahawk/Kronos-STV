package org.kronos;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Zip {
    public static void compressFiles(ArrayList<String> filesToCompress, String archivePath) throws IOException {
        final FileOutputStream fos = new FileOutputStream(archivePath);
        ZipOutputStream zipOut = new ZipOutputStream(fos);

        for (String filePath : filesToCompress) {
            File fileToZip = new File(filePath);
            FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            fis.close();
        }

        zipOut.close();
        fos.close();
    }

    // fix for ZipSlip vulnerability
    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    public static ArrayList<String> listArchiveFiles(String zipFile) throws IOException {
        ArrayList<String> fileList = new ArrayList<>();
        ZipInputStream zis = new ZipInputStream(Files.newInputStream(Paths.get(zipFile)));
        ZipEntry zipEntry = zis.getNextEntry();

        while (zipEntry != null) {
            if (!zipEntry.isDirectory()) {
                fileList.add(zipEntry.getName());
            }

            zipEntry = zis.getNextEntry();
        }

        return fileList;
    }

    public static ArrayList<File> decompressFiles(String zipFile, String destDir) throws IOException {
        ArrayList<File> decompressedFiles = new ArrayList<>();
        File dDir = new File(destDir);
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(Files.newInputStream(Paths.get(zipFile)));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            File newFile = newFile(dDir, zipEntry);
            if (zipEntry.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs()) {
                    throw new IOException("Failed to create directory " + newFile);
                }
            } else {
                // fix for Windows-created archives
                File parent = newFile.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("Failed to create directory " + parent);
                }

                // write file content
                decompressedFiles.add(newFile);
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            zipEntry = zis.getNextEntry();
        }

        zis.closeEntry();
        zis.close();
        return decompressedFiles;
    }
}
