package org.kapp.support.task;

import org.kapp.core.DbSpPool;
import org.kapp.support.property.DbSpProperties;
import org.slf4j.Logger;

import java.util.concurrent.Semaphore;

/**
 * Author:Heping
 * Date: 2024/7/18 17:07
 */
public abstract class PoolTask implements Runnable {
    protected DbSpPool dbSpPool;
    private static final Object LOCK_OBJ = new Object();

    public PoolTask(DbSpPool dbSpPool) {
        this.dbSpPool = dbSpPool;
    }

    /**
     * append connection
     *
     * @param logger logger
     */
    protected void append_connection(Logger logger) {
        synchronized (LOCK_OBJ) {
            DbSpProperties dbSpProperties = dbSpPool.poolProperty();
            Semaphore semaphore = dbSpPool.sourceLimiter();
            int maxIdleThread = dbSpProperties.getMaxIdleThread();
            int idleSize = dbSpPool.IdleSource().size();
            if (idleSize < maxIdleThread) {
                for (int i = 0; i < maxIdleThread - idleSize; i++) {
                    logger.info("attempt to add a new connection...");
                    try {
                        dbSpPool.newSource();
                    } catch (Exception e) {
                        logger.error("failed to add new connection, will retry in the next task cycle");
                    }
                }
            }
            if (semaphore.availablePermits() < maxIdleThread) {
                while (semaphore.availablePermits() == maxIdleThread) {
                    logger.info("attempt to release the connection...");
                    dbSpPool.releaseSource();
                }
            }
        }
    }
}
