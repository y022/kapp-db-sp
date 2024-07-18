package org.kapp;

import org.junit.jupiter.api.Test;
import org.kapp.support.property.DbSpProperties;
import org.kapp.support.property.PropertiesLoader;

/**
 * Author:Heping
 * Date: 2024/7/17 16:28
 */
class PropertiesLoaderTest {

    @Test
    void createConnectProperties() {
        DbSpProperties connectProperties = PropertiesLoader.createConnectProperties();
        System.out.println("connectProperties:" + connectProperties);
    }
}