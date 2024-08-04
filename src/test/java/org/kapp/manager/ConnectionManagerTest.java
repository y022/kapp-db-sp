package org.kapp.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kapp.core.ConnectionManager;
import org.kapp.core.DbSpPool;
import org.openjdk.jol.info.ClassLayout;

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
        Assertions.assertTrue(execute);
        con.close();
    }

    @Test
    public void test_(){
        ConnectionManager cm = new ConnectionManager(null);
        System.out.println("ClassLayout.parseInstance(cm) = " + ClassLayout.parseInstance(cm).toPrintable());
    }
}