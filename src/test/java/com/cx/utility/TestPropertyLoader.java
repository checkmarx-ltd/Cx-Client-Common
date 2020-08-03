package com.cx.utility;

import lombok.extern.slf4j.Slf4j;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

@Slf4j
public class TestPropertyLoader {
    private static final String MAIN_PROPERTIES_FILE = "config.properties";
    private static final String OVERRIDE_FILE = "config-secrets.properties";

    public Properties getProperties() {
        Properties result = getProps(MAIN_PROPERTIES_FILE);

        Properties overridingProps = getProps(OVERRIDE_FILE);
        result.putAll(overridingProps);
        return result;
    }

    private static Properties getProps(String filename) {
        Properties properties = new Properties();
        ClassLoader classLoader = TestPropertyLoader.class.getClassLoader();
        URL resource = classLoader.getResource(filename);
        if (resource == null) {
            log.warn("Test property file is not found in resources: {}", filename);
        }
        else {
            try {
                properties.load(new FileReader(resource.getFile()));
            } catch (IOException e) {
                log.warn("Error reading resource: {}", filename);
            }
        }
        return properties;
    }

}
