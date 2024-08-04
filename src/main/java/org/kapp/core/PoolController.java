package org.kapp.core;

import org.kapp.entity.Val;
import org.kapp.support.property.DbSpProperties;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeoutException;

/**
 * Author:Heping
 * Date: 2024/7/18 17:13
 */
public interface PoolController<T> {
    DbSpProperties poolProperty();

    Semaphore sourceLimiter();

    Semaphore releaseSource();

    Collection<T> IdleSource();

    Collection<T> AllSource();

    void appendCon() throws SQLException;

    default boolean testCon(Connection connection) {
        try {
            return connection.prepareCall(Val.TEST_QUERY).execute();
        } catch (Exception e) {
            return false;
        }
    }
    void newSource() throws SQLException;
}
