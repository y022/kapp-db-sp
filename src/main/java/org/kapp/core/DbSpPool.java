package org.kapp.core;

import org.kapp.core.connection.DbSpConnection;
import org.kapp.entity.ConnectionStatus;
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
public class DbSpPool implements PoolController<DbSpConnection> {
    private static final Logger LOG = LoggerFactory.getLogger(DbSpPool.class);
    private static final Object LOCK_OBJ = new Object();
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
     * try to get an idle connection with time-out
     *
     * @return warp connection
     * @throws TimeoutException create time out
     * @throws SQLException     sql exception
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
            throw new TimeoutException("get connection time-out");
        }
        throw new SQLException("get connection failed");
    }

    /**
     * create a database connection,add to {@link # all_connections}
     *
     * @return available connection
     * @throws SQLException create connection error
     * @see Connection
     */
    private DbSpConnection create() throws SQLException {
        synchronized (LOCK_OBJ) {


            Connection connection = DriverManager.getConnection(spProperties.getUrl(), spProperties.getUsername(), spProperties.getPassword());
            if (testCon(connection)) {
                DbSpConnection dbSpConnection = new DbSpConnection(connection, spProperties.getMaxSurvivalTime(), ConnectionStatus.IDLE);
                all_connections.add(dbSpConnection);
                ide_connections.add(dbSpConnection);
                releaseSource();
                return dbSpConnection;
            } else {
                throw new SQLException("get connection failed");
            }
        }
    }


    @Override
    public Semaphore releaseSource() {
        synchronized (LOCK_OBJ) {
            if (semaphore.availablePermits() < spProperties.getMaxIdleThread()) {
                semaphore.release();
            }
        }
        return sourceLimiter();
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
    public Collection<DbSpConnection> sourceIdle() {
        return this.ide_connections;
    }

    @Override
    public Collection<DbSpConnection> sourceAll() {
        return this.all_connections;
    }

    @Override
    public void appendCon() throws SQLException {
        create();
    }

}
