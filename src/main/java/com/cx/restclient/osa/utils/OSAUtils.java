package com.cx.restclient.osa.utils;

import com.cx.restclient.common.ShragaUtils;
import com.cx.restclient.osa.dto.OSAResults;
import com.cx.restclient.osa.dto.OSASummaryResults;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.io.File;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.cx.restclient.common.CxPARAM.CX_REPORT_LOCATION;

public abstract class OSAUtils {

    private static final String[] SUPPORTED_EXTENSIONS = {"jar", "war", "ear", "aar", "dll", "exe", "msi", "nupkg", "egg", "whl", "tar.gz", "gem", "deb", "udeb",
            "dmg", "drpm", "rpm", "pkg.tar.xz", "swf", "swc", "air", "apk", "zip", "gzip", "tar.bz2", "tgz", "js"};

    private static final String INCLUDE_ALL_EXTENSIONS = "**/**";
    private static final String JSON_EXTENSION = ".json";

    public static final String DEFAULT_ARCHIVE_INCLUDES = "**/*.jar,**/*.war,**/*.ear,**/*.sca,**/*.gem,**/*.whl,**/*.egg,**/*.tar,**/*.tar.gz,**/*.tgz,**/*.zip,**/*.rar";


    public static void writeToOsaListToFile(File dir, String osaDependenciesJson, Logger log) {
        try {
            File file = new File(dir, "OSADependencies.json");
            FileUtils.writeStringToFile(file, osaDependenciesJson, Charset.defaultCharset());
            log.info("OSA dependencies saved to file: [" + file.getAbsolutePath() + "]");
        } catch (Exception e) {
            log.info("Failed to write OSA dependencies to file: " + e.getMessage());
        }

    }

    public static String composeProjectOSASummaryLink(String url, long projectId) {
        return String.format(url + "/CxWebClient/SPA/#/viewer/project/%s", projectId);
    }

    public static Properties generateOSAScanConfiguration(String folderExclusions, String filterPatterns, String archiveIncludes, String sourceDir, boolean installBeforeScan, String osaScanDepth, Logger log) {
        Properties ret = new Properties();
        filterPatterns = StringUtils.defaultString(filterPatterns);
        archiveIncludes = StringUtils.defaultString(archiveIncludes);

        Map<String, List<String>> stringListMap = ShragaUtils.generateIncludesExcludesPatternLists(folderExclusions, filterPatterns, log);

        List<String> inclusions = stringListMap.get(ShragaUtils.INCLUDES_LIST);
        List<String> exclusions = stringListMap.get(ShragaUtils.EXCLUDES_LIST);

        String includesString = StringUtils.join(inclusions, ",");
        String excludesString = StringUtils.join(exclusions, ",");

        if (StringUtils.isNotEmpty(includesString)) {
            ret.put("includes", includesString);
        } else {
            ret.put("includes", INCLUDE_ALL_EXTENSIONS);
        }

        if (StringUtils.isNotEmpty(excludesString)) {
            ret.put("excludes", excludesString);
        }

        ret.put("acceptExtensionsList", SUPPORTED_EXTENSIONS);

        if (StringUtils.isNotEmpty(archiveIncludes)) {
            String[] archivePatterns = archiveIncludes.split("\\s*,\\s*"); //split by comma and trim (spaces + newline)
            for (int i = 0; i < archivePatterns.length; i++) {
                if (StringUtils.isNotEmpty(archivePatterns[i]) && archivePatterns[i].startsWith("*.")) {
                    archivePatterns[i] = "**/" + archivePatterns[i];
                }
            }
            archiveIncludes = StringUtils.join(archivePatterns, ",");
            ret.put("archiveIncludes", archiveIncludes);
        } else {
            ret.put("archiveIncludes", DEFAULT_ARCHIVE_INCLUDES);
        }

        ret.put("archiveExtractionDepth", StringUtils.isNotEmpty(osaScanDepth) ? osaScanDepth : "4");

        ret.put("npm.ignoreNpmLsErrors", "true");

        if (installBeforeScan) {
            setPreInstallDependencies(ret, "true");
            ret.put("npm.ignoreScripts", "true");
            ret.put("sbt.targetFolder", getSbtTargetFolder(sourceDir));
        } else {
            setPreInstallDependencies(ret, "false");
        }

        ret.put("d", sourceDir);
        return ret;
    }

    private static void setPreInstallDependencies(Properties ret, String resolveDependencies) {
        ret.put("npm.runPreStep", resolveDependencies);
        ret.put("php.runPreStep", resolveDependencies);
        ret.put("sbt.runPreStep", resolveDependencies);
        ret.put("gradle.runAssembleCommand", resolveDependencies);
        ret.put("nuget.restoreDependencies", resolveDependencies);
        ret.put("python.ignorePipInstallErrors", resolveDependencies);
    }

    private static String getSbtTargetFolder(String sourceFolder) {
        List<File> files = new ArrayList<File>();
        files = getBuildSbtFiles(sourceFolder, files);
        if (!files.isEmpty()) {
            return files.get(0).getAbsolutePath().replace("build.sbt", "target");
        }
        return "target";
    }

    private static List<File> getBuildSbtFiles(String path, List<File> inputFiles) {
        File folder = new File(path);
        List<File> files = Arrays.asList(folder.listFiles());
        for (File file : files) {
            if (file.isFile()) {
                if (file.getName().endsWith("build.sbt")) {
                    inputFiles.add(file);
                }
            } else if (file.isDirectory()) {
                inputFiles = getBuildSbtFiles(file.getAbsolutePath(), inputFiles);
            }
        }
        return inputFiles;
    }

    public static void printOSAResultsToConsole(OSAResults osaResults, boolean enableViolations, Logger log) {
        OSASummaryResults osaSummaryResults = osaResults.getResults();
        log.info("----------------------------Checkmarx Scan Results(CxOSA):-------------------------------");
        log.info("");
        log.info("------------------------");
        log.info("Vulnerabilities Summary:");
        log.info("------------------------");
        log.info("OSA high severity results: " + osaSummaryResults.getTotalHighVulnerabilities());
        log.info("OSA medium severity results: " + osaSummaryResults.getTotalMediumVulnerabilities());
        log.info("OSA low severity results: " + osaSummaryResults.getTotalLowVulnerabilities());
        log.info("Vulnerability score: " + osaSummaryResults.getVulnerabilityScore());
        log.info("");
        log.info("-----------------------");
        log.info("Libraries Scan Results:");
        log.info("-----------------------");
        log.info("Open-source libraries: " + osaSummaryResults.getTotalLibraries());
        log.info("Vulnerable and outdated: " + osaSummaryResults.getVulnerableAndOutdated());
        log.info("Vulnerable and updated: " + osaSummaryResults.getVulnerableAndUpdated());
        log.info("Non-vulnerable libraries: " + osaSummaryResults.getNonVulnerableLibraries());
        log.info("");
        log.info("OSA scan results location: " + osaResults.getOsaProjectSummaryLink());
        log.info("-----------------------------------------------------------------------------------------");
    }

    public static File getWorkDirectory(File filePath, Boolean osaGenerateJsonReport) {
        if (filePath == null) {
            return null;
        }

        if (!osaGenerateJsonReport) {
            return filePath;
        }

        File workDirectory;
        if (!filePath.isAbsolute()) {
            workDirectory = new File(System.getProperty("user.dir") + CX_REPORT_LOCATION);
        } else {
            workDirectory = filePath.getParentFile();
        }
        if (!workDirectory.exists()) {
            workDirectory.mkdirs();
        }

        return workDirectory;
    }

    public static void writeJsonToFile(String name, Object jsonObj, File workDirectory, Boolean cliOsaGenerateJsonReport, Logger log) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObj);

            if (cliOsaGenerateJsonReport) {
                //workDirectory = new File(workDirectory.getPath().replace(".json", "_" + name + ".json"));
                if (!workDirectory.isAbsolute()) {
                    workDirectory = new File(System.getProperty("user.dir") + CX_REPORT_LOCATION + File.separator + workDirectory);
                }
                if (!workDirectory.getParentFile().exists()) {
                    workDirectory.getParentFile().mkdirs();
                }
                name = name.endsWith(JSON_EXTENSION) ? name : name + JSON_EXTENSION;
                File jsonFile = new File(workDirectory + File.separator + name);
                FileUtils.writeStringToFile(jsonFile, json);
                log.info(name + " saved under location: " + jsonFile);
            } else {
                String now = new SimpleDateFormat("dd_MM_yyyy-HH_mm_ss").format(new Date());
                String fileName = name + "_" + now + JSON_EXTENSION;
                File jsonFile = new File(workDirectory + CX_REPORT_LOCATION, fileName);
                FileUtils.writeStringToFile(jsonFile, json);
                log.info(name + " saved under location: " + workDirectory + CX_REPORT_LOCATION + File.separator + fileName);
            }
        } catch (Exception ex) {
            log.warn("Failed to write OSA JSON report (" + name + ") to file: " + ex.getMessage());
        }
    }

}
