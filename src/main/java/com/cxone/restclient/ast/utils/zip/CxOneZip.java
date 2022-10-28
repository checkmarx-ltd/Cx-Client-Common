package com.cxone.restclient.ast.utils.zip;


import com.cx.restclient.dto.PathFilter;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;


public class CxOneZip {
    private long maxZipSizeInBytes = 2147483648l;
    private int numOfZippedFiles = 0;

    private String tempFileName;
    private Logger log;

    public CxOneZip(String tempFileName, long maxZipSizeInBytes, Logger log) {
        this.tempFileName = tempFileName;
        this.log = log;
        this.maxZipSizeInBytes = maxZipSizeInBytes;
    }

    public byte[] zipWorkspaceFolder(File baseDir, PathFilter filter) throws IOException {
        log.debug("Zipping workspace: '" + baseDir + "'");

        ZipListener zipListener = new ZipListener() {
            public void updateProgress(String fileName, long size) {
                numOfZippedFiles++;
                log.debug("Zipping (" + FileUtils.byteCountToDisplaySize(size) + "): " + fileName);
            }
        };

        byte[] zipFileBA;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            try {
                new Zipper(log).zip(baseDir, filter.getIncludes(), filter.getExcludes(), byteArrayOutputStream, maxZipSizeInBytes, zipListener);
            } catch (Zipper.MaxZipSizeReached e) {
                throw new IOException("Reached maximum upload size limit of " + FileUtils.byteCountToDisplaySize(maxZipSizeInBytes));
            } catch (Zipper.NoFilesToZip e) {
                throw new IOException("No files to zip");
            }

            log.debug("Zipping complete with " + numOfZippedFiles + " files, total compressed size: " +
                    FileUtils.byteCountToDisplaySize(byteArrayOutputStream.size()));

            zipFileBA = byteArrayOutputStream.toByteArray();
        }
        return zipFileBA;
    }

    public CxOneZip setMaxZipSizeInBytes(long maxZipSizeInBytes) {
        this.maxZipSizeInBytes = maxZipSizeInBytes;
        return this;
    }

    public CxOneZip setTempFileName(String tempFileName) {
        this.tempFileName = tempFileName;
        return this;
    }

    public String getTempFileName() {
        return tempFileName;
    }

}
