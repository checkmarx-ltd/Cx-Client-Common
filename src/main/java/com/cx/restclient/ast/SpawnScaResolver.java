package com.cx.restclient.ast;

import com.cx.restclient.exception.CxClientException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import org.slf4j.Logger;

/**
 * This class executes sca resolver executable to generate evidence/result file.
 */

public class SpawnScaResolver {

	public static final String SCA_RESOLVER_EXE = "ScaResolver.exe";
	public static final String OFFLINE = "offline";

	/**
	 * This method executes
	 * @param pathToScaResolver - Path to SCA Resolver executable
	 * @param scaResolverAddParams - Additional parameters for SCA resolver
	 * @return
	 */
	protected static int runScaResolver(String pathToScaResolver, String scaResolverAddParams,String pathToResultJSONFile)
			throws CxClientException {
		int exitCode = -100;
		String[] arguments = {};
		String[] scaResolverCommand;
		
		/*
		 Convert path and additional parameters into a single CMD command
		 */
		arguments = scaResolverAddParams.split(" ");
		scaResolverCommand = new String[arguments.length + 2];
		scaResolverCommand[0] = pathToScaResolver + File.separator + SCA_RESOLVER_EXE;
		scaResolverCommand[1] = OFFLINE;
		for  (int i = 0 ; i < arguments.length ; i++){

			String arg = arguments[i];
			if(arg.equalsIgnoreCase("debug"))
			{
				arg = "Debug";
			}
			if(arg.equalsIgnoreCase("error"))
			{
				arg = "Error";
			}
			scaResolverCommand[i+2] = arg;
			if(arg.equals("-r"))
			{
				scaResolverCommand[i+3] = pathToResultJSONFile;
				i++;
			}
		}

		try 
		{
			ProcessBuilder processBuilder = new ProcessBuilder(scaResolverCommand);
			Process process = processBuilder.start();
			
			try(BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));) 
			{ 
	            while (reader.readLine() != null) {
				}
			}catch (IOException e) {
				throw new CxClientException(e);
	        }
	        exitCode = process.waitFor();
	            
		}catch (IOException e) {
			throw new CxClientException(e);
        }catch (InterruptedException e) {
			throw new CxClientException(e);
        }
		
		return exitCode;
    }
	

}

