package org.kapp.support.task;

import org.kapp.core.connection.DbSpConnection;
import org.kapp.core.DbSpPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Iterator;

/**
 * Author:Heping
 * Date: 2024/7/18 17:08
 */
public class ConnectionHealthyTask extends PoolTask {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectionHealthyTask.class);

    public ConnectionHealthyTask(DbSpPool dbSpPool) {
       super(dbSpPool);
    }

    @Override
    public void run() {
        checkSurvivalExpire();
        append_connection(LOG);
    }

    private void checkSurvivalExpire() {
        Collection<DbSpConnection> allCons = dbSpPool.sourceAll();
        Collection<DbSpConnection> idleCons = dbSpPool.sourceIdle();
        Iterator<DbSpConnection> iterator = allCons.iterator();
        while (iterator.hasNext()) {
            DbSpConnection con = iterator.next();
            con.healthyCheck().run();
            if (con.canStop()) {
                iterator.remove();
                idleCons.remove(con);
            }
        }
    }



}
