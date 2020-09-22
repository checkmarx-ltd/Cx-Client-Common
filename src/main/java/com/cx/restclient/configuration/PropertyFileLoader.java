package com.cx.restclient.configuration;

import com.cx.restclient.exception.CxClientException;
import lombok.Getter;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class PropertyFileLoader {
    private static final String DEFAULT_FILENAME = "common.properties";

    @Getter(lazy = true)
    private static final PropertyFileLoader defaultInstance = new PropertyFileLoader(DEFAULT_FILENAME);

    private final Properties properties;

    /**
     * Loads properties from resources.
     * @param filenames list of resource filenames to load properties from.
     *                  If the same property appears several times in the files, the property value from a file will be overridden with the value from the next file.
     * @throws  CxClientException if no filenames is provided, or if an error occurred while loading a file.
     */
    public PropertyFileLoader(String... filenames) {
        if (filenames.length == 0) {
            throw new CxClientException("Please provide at least one filename.");
        }

        properties = new Properties();
        for (String filename : filenames) {
            Properties singleFileProperties = getPropertiesFromResource(filename);
            properties.putAll(singleFileProperties);
        }
    }

    private Properties getPropertiesFromResource(String filename) {
        Properties result = new Properties();
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL resource = classLoader.getResource(filename);
        if (resource == null) {
            throw new CxClientException(String.format("Resource '%s' is not found.", filename));
        }
        try {
            result.load(new FileReader(resource.getFile()));
        } catch (IOException e) {
            throw new CxClientException(String.format("Error loading the '%s' resource.", filename), e);
        }

        return result;
    }

    public String get(String propertyName) {
        return properties.getProperty(propertyName);
    }
}
