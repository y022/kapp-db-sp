package org.kapp.core;

import org.kapp.core.connection.DbSpConnection;
import org.kapp.entity.SourceStatus;
import org.kapp.support.metrics.DbSpMetrics;
import org.kapp.support.property.DbSpProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Author:Heping
 * Date: 2024/7/17 22:13
 */
public class DbSpPool implements PoolController<DbSpConnection>, DbSpMetrics {
    private static final Logger LOG = LoggerFactory.getLogger(DbSpPool.class);
    private static final Object LOCK_OBJ = new Object();
    private static final String TEST_QUERY = "select 1;";
    private final DbSpProperties spProperties;
    private final CopyOnWriteArrayList<DbSpConnection> all_connections;
    private final CopyOnWriteArrayList<DbSpConnection> ide_connections;
    private final Semaphore semaphore;

    public DbSpPool(DbSpProperties spProperties) {
        this.spProperties = spProperties;
        ide_connections = new CopyOnWriteArrayList<>();
        all_connections = new CopyOnWriteArrayList<>();
        semaphore = new Semaphore(spProperties.getMaxIdleThread(), true);
        LOG.info("semaphore availablePermits:{}", semaphore.availablePermits());
    }

    /**
     * create and return a warped database connection,simultaneously do the following things:
     * <p> 1.add to {@link #all_connections} and {@link #ide_connections} </p>
     * <p> 2.release semaphore {@link #semaphore} </p>
     *
     * @return available connection
     * @throws SQLException create connection error
     * @see Connection
     * @see DbSpConnection
     */
    private DbSpConnection create() throws SQLException {
        synchronized (LOCK_OBJ) {
            Connection connection = DriverManager.getConnection(spProperties.getUrl(), spProperties.getUsername(), spProperties.getPassword());
            boolean execute = connection.prepareCall(TEST_QUERY).execute();
            if (execute) {
                DbSpConnection dbSpConnection = new DbSpConnection(connection, spProperties.getMaxSurvivalTime(), SourceStatus.IDLE);
                all_connections.add(dbSpConnection);
                ide_connections.add(dbSpConnection);
                releaseSource();
                return dbSpConnection;
            } else {
                throw new SQLException("get connection failed");
            }
        }
    }


    /**
     * try to get an idle connection with time-out
     *
     * @return connection
     * @throws TimeoutException get connection time out error
     */
    public Connection borrow() throws TimeoutException, SQLException {
        try {
            boolean acquired = semaphore.tryAcquire(spProperties.getConnectionTimeOut(), TimeUnit.MICROSECONDS);
            if (acquired) {
                DbSpConnection con = ide_connections.remove(0);
                if (con != null) {
                    return con.using();
                }
            }
        } catch (InterruptedException e) {
            LOG.error("", e);
            throw new TimeoutException("get connection time-out");
        }
        throw new SQLException("get connection failed");
    }


    @Override
    public DbSpProperties poolProperty() {
        return this.spProperties;
    }

    @Override
    public Semaphore sourceLimiter() {
        return this.semaphore;
    }

    @Override
    public Semaphore releaseSource() {
        synchronized (LOCK_OBJ) {
            if (semaphore.availablePermits() < spProperties.getMaxIdleThread()) {
                semaphore.release();
            }
        }
        return this.semaphore;
    }

    @Override
    public Collection<DbSpConnection> IdleSource() {
        return this.ide_connections;
    }

    @Override
    public Collection<DbSpConnection> AllSource() {
        return this.all_connections;
    }

    @Override
    public void newSource() throws SQLException {
        DbSpConnection dbSpConnection = create();
    }

    @Override
    public int createdConnections() {
        return all_connections.size();
    }

    @Override
    public int idleConnections() {
        return ide_connections.size();
    }

    @Override
    public int activeConnections() {
        return createdConnections() - idleConnections();
    }
}
