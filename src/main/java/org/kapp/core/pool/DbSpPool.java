package org.kapp.core.pool;

import org.kapp.core.PoolController;
import org.kapp.core.connection.DbSpConnection;
import org.kapp.entity.SourceStatus;
import org.kapp.entity.exception.ConnectionAcquireException;
import org.kapp.support.property.DbSpProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DbSpPool implements PoolController<DbSpConnection> {
    private final Logger logger = LoggerFactory.getLogger(DbSpPool.class);
    /**
     * 配置
     */
    private final DbSpProperties dbSpProperties;
    /**
     * 空闲连接队列
     */
    private final ArrayBlockingQueue<DbSpConnection> taskQueue;
    /**
     * 所有已经建立的连接
     */
    private final ConcurrentMap<DbSpConnection, SourceStatus> allConnections = new ConcurrentHashMap<>(256);
    /**
     * 等待者
     */
    private final AtomicInteger waiters = new AtomicInteger(0);
    /**
     * 监视锁对象
     */
    private final Object lock = new Object();

    public DbSpPool(DbSpProperties dbSpProperties) {
        this.dbSpProperties = dbSpProperties;
        taskQueue = new ArrayBlockingQueue<>(dbSpProperties.getMaxWaitingTask() <= 0
                ? dbSpProperties.getMaxWaitingTask() : Integer.MAX_VALUE, true);
    }

    @Override
    public DbSpProperties poolProperty() {
        return this.dbSpProperties;
    }

    @Override
    public DbSpConnection borrow() throws ConnectionAcquireException {
        return getConnectionFromPool();
    }

    /**
     * 从连接池中获取连接
     *
     * @return available-connection
     * @throws ConnectionAcquireException
     */
    private DbSpConnection getConnectionFromPool() throws ConnectionAcquireException {
        waiters.incrementAndGet();
        try {
            //如果工作队列是空的或者小于最大线程数,就尝试创建一个,并直接返回
            DbSpConnection dp_cn = null;
            if (taskQueue.isEmpty() || taskQueue.size() < dbSpProperties.getMaxActiveThread()) {
                synchronized (lock) {
                    dp_cn = createConnection();
                }
                if (dp_cn == null) {
                    throw new ConnectionAcquireException("create new connection error");
                }
                if (dp_cn.updateStatus(SourceStatus.IDLE, SourceStatus.RUNNING)) {
                    return dp_cn;
                }
            }
            //如果工作队列存在连接,就尝试从队列中获取连接，此处控制获取的超时时间
            DbSpConnection connection = taskQueue.poll(poolProperty().getConnectionTimeOut(), TimeUnit.MICROSECONDS);
            if (connection != null) {
                return connection;
            }
        } catch (Exception e) {
            return null;
        } finally {
            waiters.decrementAndGet();
        }
        return null;
    }

    /**
     * @return DbSpConnection
     * @throws ConnectionAcquireException
     */
    private DbSpConnection createConnection() throws ConnectionAcquireException {
        try {
            synchronized (lock) {
                Connection connection = DriverManager.getConnection(dbSpProperties.getUrl(), dbSpProperties.getUsername(), dbSpProperties.getUsername());
                DbSpConnection cn = new DbSpConnection(connection, dbSpProperties.getMaxSurvivalTime());
                if (cn.canUse()) {
                    allConnections.put(cn, SourceStatus.IDLE);
                    taskQueue.add(cn);
                    return cn;
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.error("create connection error", e);
        }
        return null;
    }
}
