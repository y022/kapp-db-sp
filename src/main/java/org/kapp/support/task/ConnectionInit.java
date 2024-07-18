package org.kapp.support.task;

import org.kapp.core.DbSpPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author:Heping
 * Date: 2024/7/18 19:05
 */
public class ConnectionInit extends PoolTask {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectionInit.class);
    public ConnectionInit(DbSpPool dbSpPool) {
        super(dbSpPool);
    }

    @Override
    public void run() {
        append_connection(LOG);
    }
}
