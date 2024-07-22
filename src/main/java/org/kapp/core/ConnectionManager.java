package org.kapp.core;

import org.kapp.support.property.DbSpProperties;
import org.kapp.support.property.PropertiesLoader;
import org.kapp.support.task.ConnectionHealthyTask;
import org.kapp.support.task.ConnectionCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * Author:Heping
 * Date: 2024/7/17 16:36
 */
public class ConnectionManager {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectionManager.class);
    private final DbSpProperties spProperties;
    private final DbSpPool spPool;
    private final ScheduledExecutorService scheduledExecutorService;
    private final ThreadPoolExecutor executor;
    private final ConnectionCreator connectionCreator;

    public ConnectionManager(DbSpProperties dbSpProperties) {
        spProperties = Objects.requireNonNullElseGet(dbSpProperties, PropertiesLoader::createConnectProperties);
        spPool = new DbSpPool(spProperties);

        this.scheduledExecutorService = Executors.newScheduledThreadPool(1, r -> {
            Thread thread = new Thread(r);
            thread.setName("db-sp-monitor-" + thread.getId());
            return thread;
        });
        this.executor =
                new ThreadPoolExecutor(2, 4, 60000, TimeUnit.MICROSECONDS, new ArrayBlockingQueue<>(100000));
        connectionCreator = new ConnectionCreator(spPool,true);
        initNecessaryProperty();
    }

    private void initNecessaryProperty() {
        scheduledExecutorService.schedule(new ConnectionHealthyTask(spPool), 5000, TimeUnit.MILLISECONDS);
        executor.execute(connectionCreator);
    }

    public Connection getCon() throws SQLException {
        try {
            return spPool.borrow();
        } catch (TimeoutException e) {
            throw new SQLException(e);
        }
    }


}
