package org.kapp.core.connection;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Author:Heping
 * Date: 2024/7/17 17:05
 */
public interface ConnectAction {
    Statement createStatement() throws SQLException;

    PreparedStatement prepareStatement(String sql) throws SQLException;

    boolean close();
}
