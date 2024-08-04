package org.kapp.support.metrics;

public interface DbSpMetrics {
    int createdConnections();
    int idleConnections();
    int activeConnections();

}
