package com.cx.restclient.ast;

import com.cx.restclient.exception.CxClientException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.FileWriter;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;

/**
 * This class executes sca resolver executable to generate evidence/result file.
 */
public class SpawnScaResolver {

    public static final String SCA_RESOLVER_EXE = "\\" + "ScaResolver" + ".exe";
    public static final String SCA_RESOLVER_FOR_LINUX = "/" + "ScaResolver";
    public static final String OFFLINE = "offline";

    /**
     * This method executes
     *
     * @param pathToScaResolver - Path to SCA Resolver executable
     * @param scaResolverAddParams - Additional parameters for SCA resolver
     * @param pathToSASTResultJSONFile - Additional parameters for SCA resolver
     * @return
     */
    protected static int runScaResolver(String pathToScaResolver, String scaResolverAddParams, String pathToResultJSONFile, String pathToSASTResultJSONFile, Logger log)
            throws CxClientException {
        int exitCode = -100;
        String[] scaResolverCommand;

        List<String> arguments = new ArrayList<String>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(scaResolverAddParams);
        while (m.find()) {
            arguments.add(m.group(1));
        }
        /*
		 Convert path and additional parameters into a single CMD command
         */
        scaResolverCommand = new String[arguments.size() + 2];

        if (!SystemUtils.IS_OS_UNIX) {
            //Add "ScaResolver.exe" to cmd command on Windows
            pathToScaResolver = pathToScaResolver + SCA_RESOLVER_EXE;
        } else {
            //Add "/ScaResolver" command on Linux machines
            pathToScaResolver = pathToScaResolver + SCA_RESOLVER_FOR_LINUX;
        }

        log.debug("Starting build CMD command");
        scaResolverCommand[0] = pathToScaResolver;
        scaResolverCommand[1] = OFFLINE;
        boolean configGiven = false;
        for (int i = 0; i < arguments.size(); i++) {

            String arg = arguments.get(i);
            scaResolverCommand[i + 2] = arg;
            if (arg.equals("-r") || "--resolver-result-path".equals(arg)) {
                while (pathToResultJSONFile.contains("\"")) {
                    pathToResultJSONFile = pathToResultJSONFile.replace("\"", "");
                }
                scaResolverCommand[i + 3] = pathToResultJSONFile;
                i++;
            } else if (arg.equals("-c") || arg.equals("--config-path")) {
                configGiven = true;
            } else if ("--sast-result-path".equals(arg)) {
                while (pathToSASTResultJSONFile.contains("\"")) {
                    pathToSASTResultJSONFile = pathToSASTResultJSONFile.replace("\"", "");
                }
                scaResolverCommand[i + 3] = pathToSASTResultJSONFile;
                i++;
            }
        }

        if (!configGiven) {
            if (pathToResultJSONFile.equals("")) {
                Path parent = Paths.get(pathToResultJSONFile).getParent();
                if (parent != null) {
                    Path logDir = Paths.get(parent.toString(), "log");
                    Path configPath = Paths.get(parent.toString(), "Configuration.ini");

                    try {
                        Files.createDirectories(logDir);
                    } catch (IOException e) {
                        log.error("Could not create log directory: " + e.getMessage(), e.getStackTrace());
                        throw new CxClientException(e);
                    }
                    try (FileWriter config = new FileWriter(configPath.toString())) {
                        config.write("LogsDirectory=" + logDir);
                    } catch (IOException e) {
                        log.error("Could not create configuration file: " + e.getMessage(), e.getStackTrace());
                    }

                    log.debug("    --config-path " + configPath);
                    scaResolverCommand[arguments.size()] = "--config-path";
                    scaResolverCommand[arguments.size() + 1] = configPath.toString();
                }
            }
        }

        log.debug("Finished created CMD command");
        log.debug("Constructed ScaResolver command: " + Arrays.toString(scaResolverCommand));
        try {
            Process process;
            String[] command = scaResolverCommand;
            if (SystemUtils.IS_OS_UNIX) {
                String tempPermissionValidation = "ls " + pathToScaResolver + " -ltr";
                printExecCommandOutput(tempPermissionValidation, log);
            }

            log.debug("Executing ScaResolver command.");
            process = Runtime.getRuntime().exec(command);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line = null;
                while ((line = reader.readLine()) != null) {
                    log.info(line);
                }
            } catch (IOException e) {
                log.error("Error while reading standard output: " + e.getMessage(), e.getStackTrace());
                throw new CxClientException(e);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.debug(line);
                }
            } catch (IOException e) {
                log.error("Error while reading error output: " + e.getMessage(), e.getStackTrace());
                throw new CxClientException(e);
            }
            if (System.getProperty("JenkinsScaResolverTimer") != null) {
                int JenkinsScaResolverTimer = Integer.parseInt(System.getProperty("JenkinsScaResolverTimer"));
                log.info("JenkinsScaResolverTimer property detected. Enforcing {} minute timeout.", System.getProperty("JenkinsScaResolverTimer"));

                boolean finishedInTime = process.waitFor(JenkinsScaResolverTimer, TimeUnit.MINUTES);
                if (!finishedInTime) {
                    log.error("SCA Resolver process exceeded the timeout and will be terminated.");
                    log.info("JVM Memory Details - Total: {} bytes, Free: {} bytes, Max: {} bytes",
                            Runtime.getRuntime().totalMemory(),
                            Runtime.getRuntime().freeMemory(),
                            Runtime.getRuntime().maxMemory());
                    process.destroyForcibly();
                    throw new CxClientException("Process exceeded the allowed timeout");
                }
                exitCode = process.exitValue();
                log.info("SCA Resolver process exited with code: " + exitCode);
            } else {
                log.info("JenkinsScaResolverTimer property not detected.");
                exitCode = process.waitFor();
                log.info("SCA Resolver process exited with code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            log.error("Failed to execute next command : " + scaResolverCommand, e.getMessage(), e.getStackTrace());
            Thread.currentThread().interrupt();
            if (Thread.interrupted()) {
                throw new CxClientException(e);
            }
        }
        return exitCode;

    }

    private static void printExecCommandOutput(String execCommand, Logger log) {
        try {
            log.debug("Checking that next file has -rwxrwxrwx permissions " + execCommand);
            Runtime r = Runtime.getRuntime();
            Process p = r.exec(execCommand);
            BufferedReader is = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = is.readLine()) != null) {
                log.debug(line);
            }
        } catch (Exception ex) {
            log.debug("Failed to run execute [%s] command ");
        }
    }
}
