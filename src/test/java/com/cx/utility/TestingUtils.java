package com.cx.utility;

import com.cx.restclient.ast.dto.sca.AstScaConfig;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public final class TestingUtils {

    public static Properties getProps(String propsName, Class<?> clazz) throws IOException {
        Properties properties = new Properties();
        ClassLoader classLoader = clazz.getClassLoader();
        URL resource = classLoader.getResource(propsName);
        if (resource == null) {
            throw new IOException(String.format("Resource '%s' is not found.", propsName));
        }
        properties.load(new FileReader(resource.getFile()));

        return properties;
    }

    public static AstScaConfig getScaConfig(Properties props, boolean useOnPremiseAuthentication) {
        String accessControlProp, usernameProp, passwordProp;
        if (useOnPremiseAuthentication) {
            accessControlProp = "astSca.onPremise.accessControlUrl";
            usernameProp = "astSca.onPremise.username";
            passwordProp = "astSca.onPremise.password";
        } else {
            accessControlProp = "astSca.cloud.accessControlUrl";
            usernameProp = "astSca.cloud.username";
            passwordProp = "astSca.cloud.password";
        }

        AstScaConfig result = new AstScaConfig();
        result.setApiUrl(props.getProperty("astSca.apiUrl"));
        result.setWebAppUrl(props.getProperty("astSca.webAppUrl"));
        result.setTenant(props.getProperty("astSca.tenant"));
        result.setAccessControlUrl(props.getProperty(accessControlProp));
        result.setUsername(props.getProperty(usernameProp));
        result.setPassword(props.getProperty(passwordProp));
        return result;
    }
}
