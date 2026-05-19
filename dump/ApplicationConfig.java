package com.app.quantitymeasurement.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.InputStream;
import java.util.Properties;

public class ApplicationConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfig.class);
    private static ApplicationConfig instance;
    private final Properties properties;

    private ApplicationConfig() {
        this.properties = new Properties();
        loadProperties();
    }

    public static synchronized ApplicationConfig getInstance() {
        if (instance == null) {
            instance = new ApplicationConfig();
        }
        return instance;
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input == null) {
                LOGGER.warn("application.properties not found, using defaults");
                setDefaults();
                return;
            }
            properties.load(input);
            LOGGER.info("Configuration loaded successfully");
        } catch (Exception e) {
            LOGGER.error("Failed to load application.properties", e);
            setDefaults();
        }
    }

    private void setDefaults() {
        properties.setProperty("spring.datasource.url", "jdbc:mysql://localhost:3306/quantitydb?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
        properties.setProperty("spring.datasource.driver-class-name", "com.mysql.cj.jdbc.Driver");
        properties.setProperty("spring.datasource.username", "root");
        properties.setProperty("spring.datasource.password", "MySQL#Nav@2025");
        properties.setProperty("pool.max.size", "10");
        properties.setProperty("pool.min.idle", "2");
        properties.setProperty("pool.timeout.ms", "30000");
        properties.setProperty("repository.type", "database");
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public String getDatabaseUrl() {
        return getProperty("spring.datasource.url");
    }

    public String getDatabaseDriver() {
        return getProperty("spring.datasource.driver-class-name");
    }

    public String getDatabaseUsername() {
        return getProperty("spring.datasource.username");
    }

    public String getDatabasePassword() {
        return getProperty("spring.datasource.password");
    }

    public int getPoolMaxSize() {
        return getIntProperty("pool.max.size", 10);
    }

    public int getPoolMinIdle() {
        return getIntProperty("pool.min.idle", 2);
    }

    public int getPoolTimeoutMs() {
        return getIntProperty("pool.timeout.ms", 30000);
    }

    public String getRepositoryType() {
        return getProperty("repository.type", "database");
    }
}