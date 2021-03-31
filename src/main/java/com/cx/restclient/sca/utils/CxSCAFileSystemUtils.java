package com.cx.restclient.sca.utils;

import com.cx.restclient.dto.PathFilter;
import org.apache.tools.ant.DirectoryScanner;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CxSCAFileSystemUtils {

    public static String[] scanAndGetIncludedFiles(String baseDir, PathFilter filter) {
        DirectoryScanner ds = createDirectoryScanner(new File(baseDir), filter.getIncludes(), filter.getExcludes());
        ds.setFollowSymlinks(true);
        ds.scan();
        return ds.getIncludedFiles();
    }

    private static DirectoryScanner createDirectoryScanner(File baseDir, String[] filterIncludePatterns, String[] filterExcludePatterns) {
        DirectoryScanner ds = new DirectoryScanner();
        ds.setBasedir(baseDir);
        ds.setCaseSensitive(false);
        ds.setFollowSymlinks(false);
        ds.setErrorOnMissingDir(false);

        if (filterIncludePatterns != null && filterIncludePatterns.length > 0) {
            ds.setIncludes(filterIncludePatterns);
        }

        if (filterExcludePatterns != null && filterExcludePatterns.length > 0) {
            ds.setExcludes(filterExcludePatterns);
        }

        return ds;
    }

    public static HashMap<String, String> convertStringToKeyValueMap(String envString) {

        HashMap<String, String> envMap = new HashMap<>();
        //"Key1:Val1,Key2:Val2"
        String trimmedString = envString.replace("\"","");
        List<String> envlist = Arrays.asList(trimmedString.split(","));

        for( String pair : envlist)
        {
            String[] splitFromColon = pair.split(":",2);
            String key = (splitFromColon[0]).trim();
            String value = (splitFromColon[1]).trim();
            envMap.put(key, value);
        }
        return envMap;

    }

}
