package org.kapp.entity;

/**
 * Author:Heping
 * Date: 2024/7/17 17:01
 */
public enum SourceStatus {
    IDLE("idle", "idle"),
    RUNNING("running", "running"),
    CLOSED("closed", "closed");

    SourceStatus(String status, String message) {
        this.status = status;
        this.message = message;
    }

    private final String status;

    private final String message;
}
