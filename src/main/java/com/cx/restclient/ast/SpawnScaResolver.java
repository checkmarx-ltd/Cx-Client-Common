package com.cx.restclient.ast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SpawnScaResolver {
	
	protected static int runScaResolver(String pathToScaResolver, String scaResolverAddParams,  boolean debug){
		int exitCode = -100;
		String[] arguments = {};
		String[] scaResolverCommand;
		
		/*
		 Convert path and additional parameters into a single CMD command
		 */
		arguments = scaResolverAddParams.split(" ");
		scaResolverCommand = new String[arguments.length + 2];
		scaResolverCommand[0] = pathToScaResolver + "\\ScaResolver.exe"; //might be different for Linux Machine
		scaResolverCommand[1] = "offline";
		for  (int i = 0 ; i < arguments.length ; i++){
			scaResolverCommand[i+2] = arguments[i];
		}
		
		ProcessBuilder processBuilder = new ProcessBuilder(scaResolverCommand);
		try {
            Process process = processBuilder.start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) { }


            exitCode = process.waitFor();            
            
        }catch (IOException e) {
        	e.printStackTrace();
        }catch (InterruptedException e) {
        	e.printStackTrace();
        }
		
		return exitCode;
    }
	
	protected static String getScaResolverResultDir(String scaResolverAddParams)
	{
		String pathToEvidenceFile ="";
		String[] arguments = {};
		
		/*
		 Convert path and parameters into a single CMD command
		 */
		arguments = scaResolverAddParams.split(" ");
		
		for (int i = 0; i <  arguments.length ; i++) {
        	if (arguments[i].equals("-r") )
        		pathToEvidenceFile =  arguments[i+1];
        }
		
		return pathToEvidenceFile;
	}
}

