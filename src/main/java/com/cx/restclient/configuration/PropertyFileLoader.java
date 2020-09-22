package com.cx.restclient.configuration;

import com.cx.restclient.exception.CxClientException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

@Slf4j
public class PropertyFileLoader {
    private static final String DEFAULT_FILENAME = "common.properties";

    @Getter(lazy = true)
    private static final PropertyFileLoader defaultInstance = new PropertyFileLoader(DEFAULT_FILENAME);

    private final Properties properties;

    /**
     * Loads properties from resources.
     *
     * @param filenames list of resource filenames to load properties from.
     *                  If the same property appears several times in the files, the property value from a file will be overridden with the value from the next file.
     * @throws CxClientException if no filenames is provided, or if an error occurred while loading a file.
     */
    public PropertyFileLoader(String... filenames) {
        if (filenames.length == 0) {
            throw new CxClientException("Please provide at least one filename.");
        }

        properties = new Properties();
        for (String filename : filenames) {
            URL url = getResourceUrl(filename);
            Properties singleFileProperties = getPropertiesFromResource(url);
            properties.putAll(singleFileProperties);
        }
    }

    private Properties getPropertiesFromResource(URL resourceUrl) {
        Properties result = new Properties();
        if (resourceUrl != null) {
            log.debug("Loading properties from resource: {}", resourceUrl);
            try (FileReader fileReader = new FileReader(resourceUrl.getPath())) {
                result.load(fileReader);
            } catch (IOException e) {
                throw new CxClientException(String.format("Error loading the '%s' resource.", resourceUrl), e);
            }
        }
        return result;
    }

    private URL getResourceUrl(String filename) {
        log.debug("Getting resource URL for a property file: {}", filename);
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL result = classLoader.getResource(filename);
        if (result != null) {
            log.debug("Property file URL: {}", result);
        } else {
            log.warn("Unable to find resource: {}, skipping.", filename);
        }
        return result;
    }

    public String get(String propertyName) {
        return properties.getProperty(propertyName);
    }
}
