package org.kapp.support.property;

import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

/**
 * Author:Heping
 * Date: 2024/7/17 16:09
 */
public class PropertiesLoader {
    private static final Properties APP_PROPERTIES = new Properties();
    private final static String PROPERTIES_FILE_NAME = "app.properties";

    public static Properties getDbSpProperties() {
        return APP_PROPERTIES;
    }

    static {
        try (InputStream is = PropertiesLoader.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME)) {
            if (is == null) {
                throw new IllegalArgumentException("Configuration file not found: " + PROPERTIES_FILE_NAME);
            }
            APP_PROPERTIES.load(is);
        } catch (Exception e) {
            throw new RuntimeException("Error loading configuration file: " + PROPERTIES_FILE_NAME, e);
        }
    }

    /**
     * create db-sp connection properties
     *
     * @return DbSpProperties
     * @see DbSpProperties
     */
    public static DbSpProperties createConnectProperties() {
        DbSpProperties dbSpProperties = new DbSpProperties();
        dbSpProperties.setDriverName(String.valueOf(APP_PROPERTIES.get("kapp.db.sp.driverName")));
        dbSpProperties.setUrl(String.valueOf(APP_PROPERTIES.get("kapp.db.sp.url")));
        dbSpProperties.setUsername(String.valueOf(APP_PROPERTIES.get("kapp.db.sp.username")));
        dbSpProperties.setPassword(String.valueOf(APP_PROPERTIES.get("kapp.db.sp.password")));
        dbSpProperties.setPassword(String.valueOf(APP_PROPERTIES.get("kapp.db.sp.password")));

        dbSpProperties.setMaxActiveThread(Integer.parseInt(Objects.toString(APP_PROPERTIES.get("kapp.db.sp.maxActiveThread"))));
        dbSpProperties.setMaxIdleThread(Integer.parseInt(Objects.toString(APP_PROPERTIES.get("kapp.db.sp.maxIdleThread"))));
        dbSpProperties.setMaxSurvivalTime(Integer.parseInt(Objects.toString(APP_PROPERTIES.get("kapp.db.sp.maxSurvivalTime"))));
        dbSpProperties.setConnectionTimeOut(Integer.parseInt(Objects.toString(APP_PROPERTIES.get("kapp.db.sp.connectionTimeout"))));

        return dbSpProperties;
    }


}
