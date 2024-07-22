package org.kapp.core;

import org.kapp.support.property.DbSpProperties;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * Author:Heping
 * Date: 2024/7/18 22:13
 */
public class DbSpDataSource implements DataSource {
    private final DbSpProperties spProperties;
    private final ConnectionManager connectionManager;

    public DbSpDataSource(DbSpProperties spProperties) {
        this.spProperties = spProperties;
        this.connectionManager = new ConnectionManager(spProperties);
    }


    @Override
    public Connection getConnection() throws SQLException {
        return connectionManager.getCon();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return connectionManager.getCon();
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
