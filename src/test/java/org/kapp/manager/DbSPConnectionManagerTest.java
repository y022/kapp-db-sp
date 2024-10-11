package org.kapp.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kapp.core.DbSPConnectionManager;
import org.openjdk.jol.info.ClassLayout;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Author:Heping
 * Date: 2024/7/18 18:49
 */
class DbSPConnectionManagerTest {

    @Test
    void getConnection() throws SQLException, InterruptedException {
        DbSPConnectionManager cm = new DbSPConnectionManager(null);
        Thread.sleep(3000);
        Connection con = cm.getConnection();
        CallableStatement callableStatement = con.prepareCall("select 1;");
        boolean execute = callableStatement.execute();
        Assertions.assertTrue(execute);
        con.close();
    }

    @Test
    public void test_() {
        DbSPConnectionManager cm = new DbSPConnectionManager(null);
        System.out.println("ClassLayout.parseInstance(cm) = " + ClassLayout.parseInstance(cm).toPrintable());
    }
}