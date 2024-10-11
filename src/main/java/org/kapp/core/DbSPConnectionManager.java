package org.kapp.core;

import org.kapp.core.pool.DbSpPool;
import org.kapp.entity.exception.ConnectionAcquireException;
import org.kapp.support.property.DbSpProperties;
import org.kapp.support.property.PropertiesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * Author:Heping
 * Date: 2024/7/17 16:36
 */
public class DbSPConnectionManager {
    private static final Logger LOG = LoggerFactory.getLogger(DbSPConnectionManager.class);
    private final DbSpProperties spProperties;
    private final DbSpPool spPool;
    private final ScheduledExecutorService scheduledExecutorService;
    private final ThreadPoolExecutor executor;

    public DbSPConnectionManager(DbSpProperties dbSpProperties) {
        spProperties = Objects.requireNonNullElseGet(dbSpProperties, PropertiesLoader::createConnectProperties);
        spPool = new DbSpPool(spProperties);

        this.scheduledExecutorService = Executors.newScheduledThreadPool(1, r -> {
            Thread thread = new Thread(r);
            thread.setName("db-sp-monitor-" + thread.getId());
            return thread;
        });
        this.executor =
                new ThreadPoolExecutor(2, 4, 60000, TimeUnit.MICROSECONDS, new ArrayBlockingQueue<>(100000));
        initNecessaryProperty();
    }

    private void initNecessaryProperty() {
    }

    public Connection getConnection() throws ConnectionAcquireException {
        return spPool.borrow();
    }


}
