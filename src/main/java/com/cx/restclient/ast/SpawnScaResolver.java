package com.cx.restclient.ast;

import com.cx.restclient.exception.CxClientException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        String[] scaResolverCommand;

        List<String> arguments = new ArrayList<String>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(scaResolverAddParams);
        while (m.find())
            arguments.add(m.group(1));
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

        for (int i = 0; i < arguments.size(); i++) {

            String arg = arguments.get(i);
            if (arg.equalsIgnoreCase("debug")) {
                arg = "Debug";
            }
            if (arg.equalsIgnoreCase("error")) {
                arg = "Error";
            }
            scaResolverCommand[i + 2] = arg;
            if (arg.equals("-r")) {
                while (pathToResultJSONFile.contains("\""))
                    pathToResultJSONFile = pathToResultJSONFile.replace("\"", "");
                scaResolverCommand[i + 3] = pathToResultJSONFile;
                i++;
            }
        }
        log.debug("Finished created CMD command");
/*        Map<String, String> env = System.getenv();
        Iterator<Map.Entry<String, String>> iterator = env.entrySet().iterator();
        String[] envVars = new String[env.size()];
        for (int i = 0; i < env.size(); i++) {
            Map.Entry<String, String> entry = iterator.next();
            envVars[i] = entry.getKey() + "=" + entry.getValue();
        }*/
/*        log.info("##### Environment variables #####");
        Map<String, String> env = System.getenv();
        for (Map.Entry entry : env.entrySet()){
            log.info(entry.getKey() + "=" + entry.getValue());
        }*/
        try {
            log.info("Executing next command: " + Arrays.toString(scaResolverCommand));
            Process process;
            if (!SystemUtils.IS_OS_UNIX) {
                log.debug("Executing cmd command on windows. ");
                process = Runtime.getRuntime().exec(scaResolverCommand);
            } else {
                String tempPermissionValidation = "ls " + pathToScaResolver + " -ltr";
                printExecCommandOutput(tempPermissionValidation, log);

                log.debug("Executing ScaResolver command.");
                process = Runtime.getRuntime().exec(scaResolverCommand);
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

