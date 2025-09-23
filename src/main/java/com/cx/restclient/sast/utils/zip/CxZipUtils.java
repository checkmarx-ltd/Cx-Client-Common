package com.cx.restclient.sast.utils.zip;


import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.PathFilter;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.cx.restclient.sast.utils.SASTParam.MAX_ZIP_SIZE_BYTES;
import static com.cx.restclient.sast.utils.SASTParam.TEMP_FILE_NAME_TO_ZIP;


/**
 * CxZipUtils generates the patterns used for zipping the workspace folder
 */
public abstract class CxZipUtils {
    private static File tempExtractedDir;

    public synchronized static byte[] getZippedSources(CxScanConfig config, PathFilter filter, String sourceDir, Logger log, String prefix) throws IOException {
        byte[] zipFile;
        try {
            if (config.getZipFile() != null) {
                try{
                    sourceDir = extractZipToTempDirectory(config.getZipFile().getAbsolutePath(), log, prefix);
                } catch (IOException e) {
                    log.error("Failed to extract ZIP file: {}", config.getZipFile().getAbsolutePath(), e);
                    throw new IOException("Error extracting ZIP file for scanning", e);
                }
            }else {
                log.info("Uploading the zipped source code.");
            }
            log.debug("----------------------------------- Start zipping files :------------------------------------");
            Long maxZipSize = config.getMaxZipSize() != null ? config.getMaxZipSize() * 1024 * 1024 : MAX_ZIP_SIZE_BYTES;

            CxZip cxZip = new CxZip(TEMP_FILE_NAME_TO_ZIP, maxZipSize, log);
            zipFile = cxZip.zipWorkspaceFolder(new File(sourceDir), filter);
            log.debug("sourceDir:" + sourceDir);
            log.debug("----------------------------------- Finish zipping files :------------------------------------");

            return zipFile;
        } finally {
            cleanupTempExtractedDir(log);
        }
    }

    public static String extractZipToTempDirectory(String zipFilePath, Logger log, String prefix) throws IOException {

        tempExtractedDir = Files.createTempDirectory(prefix + "_extracted_").toFile();
        log.info("Created temporary directory: {}", tempExtractedDir.getAbsolutePath());

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry zipEntry;
            byte[] buffer = new byte[1024];

            while ((zipEntry = zis.getNextEntry()) != null) {
                File newFile = new File(tempExtractedDir, zipEntry.getName());

                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory: " + newFile);
                    }
                } else {
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory: " + parent);
                    }

                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();
            }
        }

        log.info("Successfully extracted ZIP file to: {}", tempExtractedDir.getAbsolutePath());
        return tempExtractedDir.getAbsolutePath();
    }

    public static boolean isZip(String effectiveDir, Logger log) {
        // Check for null path
        if (effectiveDir == null) {
            log.info("Source directory path is null");
            return false;
        }

        // Check if the path exists and is a file
        File file = new File(effectiveDir);
        if (!file.exists()) {
            log.info("Source path does not exist: {}", effectiveDir);
            return false;
        }

        if (!file.isFile()) {
            log.info("Source path is not a file: {}", effectiveDir);
            return false;
        }

        // Check if the file has .zip extension
        return effectiveDir.toLowerCase().endsWith(".zip");
    }


    public static void cleanupTempExtractedDir(Logger log) {
        if (tempExtractedDir != null && tempExtractedDir.exists()) {
            try {
                log.info("Cleaning up temporary extracted directory: {}", tempExtractedDir.getAbsolutePath());
                FileUtils.deleteDirectory(tempExtractedDir);
                tempExtractedDir = null;
            } catch (IOException e) {
                log.error("Failed to delete temporary extracted directory: {}", tempExtractedDir.getAbsolutePath(), e);
            }
        }
    }
}

