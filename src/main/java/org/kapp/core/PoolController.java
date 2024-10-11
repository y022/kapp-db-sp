package org.kapp.core;

import org.kapp.entity.exception.ConnectionAcquireException;
import org.kapp.support.property.DbSpProperties;

/**
 * Author:Heping
 * Date: 2024/7/18 17:13
 */
public interface PoolController<T> {
    DbSpProperties poolProperty();

    T borrow() throws ConnectionAcquireException;

}
