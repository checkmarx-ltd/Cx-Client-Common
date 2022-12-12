package com.cx.restclient.sast.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Set;

import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SASTToASTProjectRoutingMapper {

	private static LinkedHashMap<String, Object> yamlMap;
	private static LinkedHashMap<String, MigrationYaml> migrationDetailMap;

	private static void parseYaml(String filePath, Logger log) {
		Yaml yaml = new Yaml();
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(new File(filePath));
			yamlMap = yaml.load(inputStream);
			LinkedHashMap<String, Object> yamlRoutingMap = (LinkedHashMap<String, Object>) yamlMap.get("routing");
			Set<String> yamlRoutingKeys = yamlRoutingMap.keySet();
			migrationDetailMap = new LinkedHashMap<String, MigrationYaml>();
			for (String key : yamlRoutingKeys) {
				ObjectMapper mapper = new ObjectMapper();
				MigrationYaml fun = mapper.convertValue(yamlRoutingMap.get(key), MigrationYaml.class);
				if (fun.getRegex() != null && !fun.getRegex().isEmpty()) {
					migrationDetailMap.put(key, fun);
				}
			}
		} catch (FileNotFoundException e) {
			log.error("File not found for this path: " + filePath, e);
			e.printStackTrace();
			throw new RuntimeException("File not found  ");
		} catch (Exception e) {
			//log.error("Exception occurred while loading or parsing the Yaml", e);
			e.printStackTrace();
			throw new RuntimeException("Exception occurred while loading or parsing the YAML", e);
		
		}
		
	}

	private static MigrationYamlResponse matchRegex(String inputForMatch, Logger log) {
		MigrationYamlResponse response = null;
		if (migrationDetailMap != null && !migrationDetailMap.isEmpty()) {
			Set<String> functionKeys = migrationDetailMap.keySet();
			for (String key : functionKeys) {
				if (inputForMatch.matches((String.valueOf(migrationDetailMap.get(key).getRegex())))) {
					response = new MigrationYamlResponse(key, migrationDetailMap.get(key).getRegex(),
							Boolean.valueOf(migrationDetailMap.get(key).getIsMigrate()));
					return response;
				}
				log.info("No match found for given team path: " + inputForMatch);
				throw new RuntimeException("No match found for given team path");
			}
		}
		return response;
	}
	
	public static MigrationYamlResponse isProjectEligibleToMigrateAST(String filePath, String teamPath, String teamId,
			Logger log) throws Exception {
		if ((teamPath == null || teamPath.isEmpty()) && teamId != null && !teamId.isEmpty()
				&& (Integer.parseInt(teamId) > 0)) {
			log.warn("Group Id is not supported yet, Please provide teamPath");
			throw  new RuntimeException("Group Id is not supported yet, Please provide teamPath");
		}
		if (teamPath != null && !teamPath.isEmpty()) {
			parseYaml(filePath, log);
			return matchRegex(teamPath, log);
		}
		return null;
	}
}
