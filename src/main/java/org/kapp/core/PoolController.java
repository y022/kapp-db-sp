package org.kapp.core;

import org.kapp.support.property.DbSpProperties;

import java.sql.SQLException;
import java.util.Collection;
import java.util.concurrent.Semaphore;

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

    void newSource() throws SQLException;
}
