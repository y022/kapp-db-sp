package org.kapp.core.connection;

import org.kapp.entity.SourceStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * Author:Heping
 * Date: 2024/7/17 16:54
 */
public class DbSpConnection extends AbstractDbSpConnection {
    private final Logger logger = LoggerFactory.getLogger(DbSpConnection.class);
    /**
     * connection create timestamp
     */
    private final long createTime;
    /**
     * connection max survival Time
     */
    private final long closeTime;
    /**
     * connection-status
     *
     * @see SourceStatus
     */
    private volatile SourceStatus status;

    private static final AtomicReferenceFieldUpdater<DbSpConnection, SourceStatus> STATUS_UPDATER = AtomicReferenceFieldUpdater.newUpdater(DbSpConnection.class, SourceStatus.class, "status");


    public DbSpConnection(Connection delegate, long maxSurvivalTime) {
        super(delegate);
        this.status = SourceStatus.IDLE;
        this.createTime = System.currentTimeMillis();
        this.closeTime = createTime + maxSurvivalTime;
    }

    @Override
    public boolean updateStatus(SourceStatus except, SourceStatus update) {
        return STATUS_UPDATER.compareAndSet(this, except, update);
    }

    @Override
    public boolean canUse() {
        synchronized (this) {
            return idle() && testAvailable();
        }
    }

    @Override
    public void tryClose() {
        if (SourceStatus.CLOSED != status) {
            STATUS_UPDATER.set(this, SourceStatus.CLOSED);
        }
    }

    @Override
    public boolean canStop() {
        return SourceStatus.CLOSED.equals(STATUS_UPDATER.get(this));
    }

    @Override
    public boolean idle() {
        return SourceStatus.IDLE.equals(STATUS_UPDATER.get(this));
    }

    @Override
    public SourceStatus status() {
        return STATUS_UPDATER.get(this);
    }

    private boolean testAvailable() {
        try {
            synchronized (this) {
                CallableStatement callableStatement = delegate.prepareCall("select 1");
                return callableStatement.execute();
            }
        } catch (Exception e) {
            logger.error("test available_idle exception", e);
            return false;
        }

    }


}
