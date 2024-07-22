package org.kapp.support.task;

import org.kapp.core.DbSpPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author:Heping
 * Date: 2024/7/18 19:05
 */
public class ConnectionCreator extends PoolTask {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectionCreator.class);
    private final boolean selfAdaption;

    public ConnectionCreator(DbSpPool dbSpPool) {
        super(dbSpPool);
        this.selfAdaption = false;
    }

    public ConnectionCreator(DbSpPool dbSpPool, boolean selfAdaption) {
        super(dbSpPool);
        this.selfAdaption = selfAdaption;
    }

    @Override
    public void run() {
        if (selfAdaption) {
            append_connection(LOG);
        } else {

        }
    }
}
