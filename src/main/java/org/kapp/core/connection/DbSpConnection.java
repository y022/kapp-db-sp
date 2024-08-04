package org.kapp.core.connection;

import org.kapp.entity.SourceStatus;

import java.sql.Connection;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * Author:Heping
 * Date: 2024/7/17 16:54
 */
public class DbSpConnection extends AbstractDbSpConnection {
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

    private final Runnable runnable;

    private static final Object OBJ = new Object();

    public DbSpConnection(Connection delegate, long maxSurvivalTime, SourceStatus status) {
        super(delegate);
        this.status = status;
        this.createTime = System.currentTimeMillis();
        this.closeTime = createTime + maxSurvivalTime;
        runnable = () -> {
            if (expire()) {
                tryClose();
            }
        };
    }


    @Override
    public DbSpConnection using() {
        STATUS_UPDATER.set(this, SourceStatus.RUNNING);
        return this;
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
    public Runnable healthyCheck() {
        return this.runnable;
    }

    private boolean expire() {
        return System.currentTimeMillis() - closeTime <= 0;
    }

    @Override
    public boolean available_idle() {
        return status.equals(SourceStatus.IDLE);
    }

    @Override
    public SourceStatus status() {
        return STATUS_UPDATER.get(this);
    }


}
