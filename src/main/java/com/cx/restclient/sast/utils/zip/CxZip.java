package com.cx.restclient.sast.utils.zip;


import com.cx.restclient.dto.PathFilter;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import java.io.*;


public class CxZip {
    private long maxZipSizeInBytes = 2147483648l;
    private int numOfZippedFiles = 0;

    private String tempFileName;
    private Logger log;

    public CxZip(String tempFileName, long maxZipSizeInBytes, Logger log) {
        this.tempFileName = tempFileName;
        this.log = log;
        this.maxZipSizeInBytes = maxZipSizeInBytes;
    }

    public File zipWorkspaceFolder(File baseDir, PathFilter filter)
            throws IOException {
        log.info("Zipping workspace: '" + baseDir + "'");

        ZipListener zipListener = new ZipListener() {
            public void updateProgress(String fileName, long size) {
                numOfZippedFiles++;
                log.info("Zipping (" + FileUtils.byteCountToDisplaySize(size) + "): " + fileName);
            }
        };

        File tempFile = File.createTempFile(tempFileName, ".bin");

        try (OutputStream fileOutputStream = new FileOutputStream(tempFile)) {
            new Zipper(log).zip(baseDir, filter.getIncludes(), filter.getExcludes(), fileOutputStream, maxZipSizeInBytes, zipListener);
        } catch (Zipper.MaxZipSizeReached e) {
            tempFile.delete();
            throw new IOException("Reached maximum upload size limit of " + FileUtils.byteCountToDisplaySize(maxZipSizeInBytes));
        } catch (Zipper.NoFilesToZip e) {
            throw new IOException("No files to zip");
        }

        log.info("Zipping complete with " + numOfZippedFiles + " files, total compressed size: " +
                FileUtils.byteCountToDisplaySize(tempFile.length()));
        log.info("Temporary file with zipped sources was created at: '" + tempFile.getAbsolutePath() + "'");

        return tempFile;
    }

    public byte[] zipWorkspaceFolderbyte(File baseDir, PathFilter filter) throws IOException {
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

    public CxZip setMaxZipSizeInBytes(long maxZipSizeInBytes) {
        this.maxZipSizeInBytes = maxZipSizeInBytes;
        return this;
    }

    public CxZip setTempFileName(String tempFileName) {
        this.tempFileName = tempFileName;
        return this;
    }

    public String getTempFileName() {
        return tempFileName;
    }

}
