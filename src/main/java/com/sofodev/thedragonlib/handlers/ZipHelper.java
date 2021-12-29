package com.sofodev.thedragonlib.handlers;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipHelper {

    public static void unzip(File zipFile, File destination) throws IOException {
        try (ZipFile zip = new ZipFile(zipFile)) {

            destination.mkdir();
            Enumeration zipFileEntries = zip.entries();

            while (zipFileEntries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
                String currentEntry = entry.getName();
                File destFile = new File(destination, currentEntry);
                File destinationParent = destFile.getParentFile();
                destinationParent.mkdirs();

                if (!entry.isDirectory()) {
                    BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry));
                    int currentByte;
                    byte[] data = new byte[2048];
                    FileOutputStream fos = new FileOutputStream(destFile);
                    try (BufferedOutputStream dest = new BufferedOutputStream(fos, 2048)) {
                        while ((currentByte = is.read(data, 0, 2048)) != -1) {
                            dest.write(data, 0, currentByte);
                        }
                        dest.flush();
                    }
                    is.close();
                }
            }
        }
    }

}