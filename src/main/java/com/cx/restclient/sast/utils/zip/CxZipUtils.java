package com.cx.restclient.sast.utils.zip;


import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.PathFilter;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static com.cx.restclient.sast.utils.SASTParam.MAX_ZIP_SIZE_BYTES;
import static com.cx.restclient.sast.utils.SASTParam.TEMP_FILE_NAME_TO_ZIP;


/**
 * CxZipUtils generates the patterns used for zipping the workspace folder
 */
public abstract class CxZipUtils {
    public static File getZippedSources(CxScanConfig config, PathFilter filter, String sourceDir, Logger log) throws IOException {
        File result = config.getZipFile();
        if (result == null) {
            log.info("Zipping sources");
            Long maxZipSize = config.getMaxZipSize() != null ? config.getMaxZipSize() * 1024 * 1024 : MAX_ZIP_SIZE_BYTES;

            CxZip cxZip = new CxZip(TEMP_FILE_NAME_TO_ZIP, maxZipSize, log);
            result = cxZip.zipWorkspaceFolder(new File(sourceDir), filter);
            log.debug("The sources were zipped to " + result.getAbsolutePath());
        }
        return result;
    }

    public synchronized static byte[] getZippedSourcesbyte(CxScanConfig config, PathFilter filter, String sourceDir, Logger log) throws IOException {
        byte[] zipFile = config.getZipFile() != null ? FileUtils.readFileToByteArray(config.getZipFile()) : null;
        if (zipFile == null) {
            log.debug("----------------------------------- Start zipping files :------------------------------------");
            Long maxZipSize = config.getMaxZipSize() != null ? config.getMaxZipSize() * 1024 * 1024 : MAX_ZIP_SIZE_BYTES;

            CxZip cxZip = new CxZip(TEMP_FILE_NAME_TO_ZIP, maxZipSize, log);
            zipFile = cxZip.zipWorkspaceFolderbyte(new File(sourceDir), filter);
            log.debug("----------------------------------- Finish zipping files :------------------------------------");
        }
        return zipFile;
    }

      public static void deleteZippedSources(File file, CxScanConfig config, Logger log) {
        boolean isZipFileProvidedExternally = (config.getZipFile() != null);
        if (!isZipFileProvidedExternally) {
            if (file.exists() && !file.delete()) {
                log.warn("Failed to delete temporary zip file: " + file.getAbsolutePath());
            } else {
                log.info("Temporary file deleted");
            }
        }
    }
}

