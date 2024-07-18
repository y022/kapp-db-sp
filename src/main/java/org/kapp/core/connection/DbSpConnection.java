package org.kapp.core.connection;

import org.kapp.entity.ConnectionStatus;

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
     * @see ConnectionStatus
     */
    private volatile ConnectionStatus status;

    private static final AtomicReferenceFieldUpdater<DbSpConnection, ConnectionStatus> STATUS_UPDATER = AtomicReferenceFieldUpdater.newUpdater(DbSpConnection.class, ConnectionStatus.class, "status");

    private final Runnable runnable;

    private static final Object OBJ = new Object();

    public DbSpConnection(Connection delegate, long maxSurvivalTime, ConnectionStatus status) {
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
        STATUS_UPDATER.set(this, ConnectionStatus.RUNNING);
        return this;
    }

    @Override
    public void tryClose() {
        if (ConnectionStatus.CLOSED != status) {
            STATUS_UPDATER.set(this, ConnectionStatus.CLOSED);
        }
    }

    @Override
    public boolean canStop() {
        return ConnectionStatus.CLOSED.equals(STATUS_UPDATER.get(this));
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
        return status.equals(ConnectionStatus.IDLE);
    }

    @Override
    public ConnectionStatus status() {
        return STATUS_UPDATER.get(this);
    }


}
