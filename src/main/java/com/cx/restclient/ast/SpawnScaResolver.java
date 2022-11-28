package com.cx.restclient.ast;

import com.cx.restclient.exception.CxClientException;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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
     * @param pathToScaResolver    - Path to SCA Resolver executable
     * @param scaResolverAddParams - Additional parameters for SCA resolver
     * @return
     */
    protected static int runScaResolver(String pathToScaResolver, List<String> scaResolverAddParams, String pathToResultJSONFile, Logger log)
            throws CxClientException {
        int exitCode = -100;
        List<String> scaResolverCommand = new ArrayList<>();

        if (!SystemUtils.IS_OS_UNIX) {
            //Add "ScaResolver.exe" to cmd command on Windows
            pathToScaResolver = pathToScaResolver + SCA_RESOLVER_EXE;
        } else {
            //Add "/ScaResolver" command on Linux machines
            pathToScaResolver = pathToScaResolver + SCA_RESOLVER_FOR_LINUX;
        }

        log.debug("Starting build CMD command");
        log.debug("Command: " + pathToScaResolver);
        scaResolverCommand.add(pathToScaResolver);
        log.debug("    " + OFFLINE);
        scaResolverCommand.add(OFFLINE);

        boolean configGiven = false;
        for (int i = 0; i < scaResolverAddParams.size(); i++) {
            String arg = scaResolverAddParams.get(i);
            String value = scaResolverAddParams.get(i + 1);

            if ((value.startsWith("-") && value.length() == 2) || value.startsWith("--")) {
                log.debug("    " + arg);
                scaResolverCommand.add(arg);
                continue;
            }

            if (arg.equals("--log-level")) {
                value = StringUtils.capitalize(value);
            } else if (arg.equals("-r") || arg.equals("--resolver-result-path")) {
                value = pathToResultJSONFile;
            } else if (arg.equals("-c") || arg.equals("--config-path")) {
                configGiven = true;
            }

            if (arg.equals("-p") || arg.contains("password")) {
                log.debug("    " + arg + " *************");
            } else {
                log.debug("    " + arg + " " + value);
            }

            scaResolverCommand.add(arg);
            scaResolverCommand.add(value);
            i++;
        }

        if (!configGiven) {
            Path parent = Paths.get(pathToResultJSONFile).getParent();
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
            scaResolverCommand.add("--config-path");
            scaResolverCommand.add(configPath.toString());
        }

        log.debug("Finished created CMD command");
        try {
            Process process;
            String[] command = new String[scaResolverCommand.size()];
            command = scaResolverCommand.toArray(command);

            if (SystemUtils.IS_OS_UNIX) {
                // FIXME: This has no action.
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
                    log.error(line);
                }
            } catch (IOException e) {
                log.error("Error while reading error output: " + e.getMessage(), e.getStackTrace());
                throw new CxClientException(e);
            }
            exitCode = process.waitFor();

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

