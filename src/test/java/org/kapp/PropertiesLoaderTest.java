package org.kapp;

import org.junit.jupiter.api.Test;
import org.kapp.support.property.DbSpProperties;
import org.kapp.support.property.PropertiesLoader;
import org.openjdk.jol.info.ClassLayout;

/**
 * Author:Heping
 * Date: 2024/7/17 16:28
 */
class PropertiesLoaderTest {

    @Test
    void createConnectProperties() {
        DbSpProperties properties = PropertiesLoader.createConnectProperties();
        ClassLayout classLayout = ClassLayout.parseInstance(properties);
        System.out.println("printable:" + classLayout.toPrintable());
        System.out.println("=======================================");
        ClassLayout instance = ClassLayout.parseInstance(new DbSpProperties());
        System.out.println("printable:" + instance.toPrintable());
        System.out.println("=======================================");
        String[] array = {"2", "3"};
        ClassLayout arrayInstance = ClassLayout.parseInstance(array);
        System.out.println("printable:" + arrayInstance.toPrintable());
    }
}