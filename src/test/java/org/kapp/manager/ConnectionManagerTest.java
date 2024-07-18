package org.kapp.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kapp.core.ConnectionManager;
import org.kapp.core.DbSpPool;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Author:Heping
 * Date: 2024/7/18 18:49
 */
class ConnectionManagerTest {

    @Test
    void getCon() throws SQLException, InterruptedException {
        ConnectionManager cm = new ConnectionManager(null);
        Thread.sleep(3000);
        Connection con = cm.getCon();
        CallableStatement callableStatement = con.prepareCall("select 1;");
        boolean execute = callableStatement.execute();
        Thread.sleep(20000);
        Assertions.assertTrue(execute);
        con.close();
    }
}