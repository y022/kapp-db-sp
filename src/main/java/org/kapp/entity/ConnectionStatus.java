package org.kapp.entity;

/**
 * Author:Heping
 * Date: 2024/7/17 17:01
 */
public enum ConnectionStatus {

    IDLE("idle", "idle"),
    RUNNING("running", "running"),
    CLOSED("closed", "closed");


    ConnectionStatus(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    private final String status;

    private final String message;
}
