package com.cx.restclient.sast.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Set;

import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SASTToASTProjectRoutingMapper {

	private static LinkedHashMap<String, Object> yamlMap;
	private static LinkedHashMap<String, RoutingFunction> functionMap;

	static {
		parseYaml("src/main/resources/test.yaml");
	}

	public static void parseYaml(String fileName) {
		Yaml yaml = new Yaml();
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(new File(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		yamlMap = yaml.load(inputStream);

		LinkedHashMap<String, Object> routingMap = (LinkedHashMap<String, Object>) yamlMap.get("routing");
		Set<String> routingKeys = routingMap.keySet();

		functionMap = new LinkedHashMap<String, RoutingFunction>();

		for (String key : routingKeys) {
			ObjectMapper mapper = new ObjectMapper();
			RoutingFunction fun = mapper.convertValue(routingMap.get(key), RoutingFunction.class);
			if (fun.getRegex() != null && !fun.getRegex().isEmpty()) {
				functionMap.put(key, fun);
			}
		}

	}

	public static ResponseFunction matchRegex(String input) {
		ResponseFunction response = null;
		Set<String> functionKeys = functionMap.keySet();
		for (String key : functionKeys) {
		   if (input.matches((String.valueOf(functionMap.get(key).getRegex())))) {
			   response = new ResponseFunction(key, functionMap.get(key).getRegex(), 
					   Boolean.valueOf(functionMap.get(key).getIsMigrate()));
			   return response;
		   }
		}
		return response;
	}
}
