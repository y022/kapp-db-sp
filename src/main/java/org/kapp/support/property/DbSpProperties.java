package org.kapp.support.property;

/**
 * Author:Heping
 * Date: 2024/7/17 15:28
 */
public class DbSpProperties {
    private String driverName;
    private String url;
    private String username;
    private String password;
    private int maxActiveThread;
    private int maxIdleThread;
    private int connectionTimeOut;
    private long maxSurvivalTime;
    private int maxWaitingTask;

    public int getMaxWaitingTask() {
        return maxWaitingTask;
    }

    public void setMaxWaitingTask(int maxWaitingTask) {
        this.maxWaitingTask = maxWaitingTask;
    }

    public int getMaxIdleThread() {
        return maxIdleThread;
    }

    public void setMaxIdleThread(int maxIdleThread) {
        this.maxIdleThread = maxIdleThread;
    }

    public int getConnectionTimeOut() {
        return connectionTimeOut;
    }

    public void setConnectionTimeOut(int connectionTimeOut) {
        this.connectionTimeOut = connectionTimeOut;
    }

    public DbSpProperties(String driverName, String url, String username, String password, int maxActiveThread, int maxIdleThread, int maxSurvivalTime, int connectionTimeOut) {
        this.driverName = driverName;
        this.url = url;
        this.username = username;
        this.password = password;
        this.maxActiveThread = maxActiveThread;
        this.maxIdleThread = maxIdleThread;
        this.maxSurvivalTime = maxSurvivalTime;
        this.connectionTimeOut = connectionTimeOut;
    }

    public DbSpProperties() {
    }

    public long getMaxSurvivalTime() {
        return maxSurvivalTime;
    }

    public void setMaxSurvivalTime(long maxSurvivalTime) {
        this.maxSurvivalTime = maxSurvivalTime;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMaxActiveThread() {
        return maxActiveThread;
    }

    public void setMaxActiveThread(int maxActiveThread) {
        this.maxActiveThread = maxActiveThread;
    }
}
