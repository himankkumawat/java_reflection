package com.himank.automation.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApplicationProperties {

    private static Properties properties;

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationProperties.class);

    public static Properties getProperties() {
        if(null != properties)
            return properties;

        properties = new Properties();
        try{
            InputStream input = new FileInputStream("src/main/resources/config.properties");
            properties.load(input);

        } catch (IOException io) {
            LOGGER.error("Exception while reading properties file. Msg: " + io.getMessage());
        }
        return properties;
    }
}
