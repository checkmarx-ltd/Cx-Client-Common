package com.cxone.restclient.ast.utils.zip;

import static com.cx.restclient.sast.utils.SASTParam.MAX_ZIP_SIZE_BYTES;
import static com.cx.restclient.sast.utils.SASTParam.TEMP_FILE_NAME_TO_ZIP;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.PathFilter;


public abstract class CxOneZipUtils {
	
	public synchronized static byte[] getZippedSources(CxScanConfig config, PathFilter filter, String sourceDir, Logger log) throws IOException {
        byte[] zipFile = config.getZipFile() != null ? FileUtils.readFileToByteArray(config.getZipFile()) : null;
        if (zipFile == null) {
            log.debug("----------------------------------- Start zipping files :------------------------------------");
            Long maxZipSize = config.getMaxZipSize() != null ? config.getMaxZipSize() * 1024 * 1024 : MAX_ZIP_SIZE_BYTES;

            CxOneZip cxOneZip = new CxOneZip(TEMP_FILE_NAME_TO_ZIP, maxZipSize, log);
            zipFile = cxOneZip.zipWorkspaceFolder(new File(sourceDir), filter);
            log.debug("----------------------------------- Finish zipping files :------------------------------------");
        }
        return zipFile;
    }

}
