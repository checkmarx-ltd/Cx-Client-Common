package com.cx.restclient.ast;

import com.cx.restclient.exception.CxClientException;
import com.cx.restclient.sca.utils.CxSCAResolverUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    protected static int runScaResolver(String pathToScaResolver, String scaResolverAddParams, String pathToResultJSONFile, Logger log)
            throws CxClientException {
        int exitCode = -100;
        List<String> scaResolverCommand = new ArrayList<>();

        Map<String, String> arguments;
        try {
            arguments = CxSCAResolverUtils.parseArguments(scaResolverAddParams);
        } catch (ParseException e) {
            throw new CxClientException(e.getMessage());
        }

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

        for (Map.Entry<String, String> entry: arguments.entrySet()) {
            String arg = entry.getKey();
            String value = entry.getValue();

            if (value == null) {
                log.debug("    " + arg);
                scaResolverCommand.add(arg);
                continue;
            }

            if (arg.equals("--log-level")) {
                value = StringUtils.capitalize(value);
            } else if (arg.equals("-r") || arg.equals("--resolver-result-path")) {
                value = pathToResultJSONFile;
            }

            if (arg.equals("-p") || arg.contains("password")) {
                log.debug("    " + arg + " *************");
            } else {
                log.debug("    " + arg + " " + value);
            }

            scaResolverCommand.add(arg);
            scaResolverCommand.add(value);
        }
        log.debug("Finished created CMD command");
        try {
            Process process;
            String[] command = new String[scaResolverCommand.size()];
            command = scaResolverCommand.toArray(command);
            if (!SystemUtils.IS_OS_UNIX) {
                log.debug("Executing cmd command on windows. ");
                process = Runtime.getRuntime().exec(command);
            } else {
                String tempPermissionValidation = "ls " + pathToScaResolver + " -ltr";
                printExecCommandOutput(tempPermissionValidation, log);

                log.debug("Executing ScaResolver command.");
                process = Runtime.getRuntime().exec(command);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));) {
            	String line = null;
                while ((line = reader.readLine()) != null) {
                }
            } catch (IOException e) {
                log.error("Error while trying write to the file: " + e.getMessage(), e.getStackTrace());
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

