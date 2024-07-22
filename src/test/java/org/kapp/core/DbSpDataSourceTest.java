package org.kapp.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kapp.support.property.DbSpProperties;
import org.kapp.support.property.PropertiesLoader;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Author:Heping
 * Date: 2024/7/22 16:14
 */
class DbSpDataSourceTest {

    @Test
    void getConnection() {
        DbSpProperties connectProperties = PropertiesLoader.createConnectProperties();
        DbSpDataSource dbSpDataSource = new DbSpDataSource(connectProperties);
        try {
            Connection connection = dbSpDataSource.getConnection();
            CallableStatement callableStatement = connection.prepareCall("select * from tb_book limit 10;");
            boolean execute = callableStatement.execute();
            Assertions.assertTrue(execute);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}