package org.kapp.core;

import org.kapp.DbSpConnection;
import org.kapp.entity.ConnectionStatus;
import org.kapp.support.property.DbSpProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.concurrent.*;

/**
 * Author:Heping
 * Date: 2024/7/17 22:13
 */
public class DbSpPool implements PoolController<DbSpConnection> {
    private static final Logger LOG = LoggerFactory.getLogger(DbSpPool.class);
    private static final Object LOCK_OBJ = new Object();
    private static final String TEST_QUERY = "select 1;";
    private final DbSpProperties spProperties;
    private final CopyOnWriteArrayList<DbSpConnection> all_connections;
    private final CopyOnWriteArrayList<DbSpConnection> ide_connections;
    private final Semaphore semaphore;
//    private final ScheduledFuture

    public DbSpPool(DbSpProperties spProperties) {
        this.spProperties = spProperties;
        ide_connections = new CopyOnWriteArrayList<>();
        all_connections = new CopyOnWriteArrayList<>();
        semaphore = new Semaphore(spProperties.getMaxIdleThread(), true);
        LOG.info("semaphore availablePermits:{}", semaphore.availablePermits());
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
            long start = System.currentTimeMillis();
            Connection connection = DriverManager.getConnection(spProperties.getUrl(), spProperties.getUsername(), spProperties.getPassword());
            boolean execute = connection.prepareCall(TEST_QUERY).execute();
            if (execute) {
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


    /**
     * try to get an idle connection with time-out
     *
     * @return
     * @throws TimeoutException
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
