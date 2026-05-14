package com.app.quantitymeasurement.database;

import com.app.quantitymeasurement.config.ApplicationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionPool {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionPool.class);

    private final String url;
    private final String username;
    private final String password;
    private final int maxSize;
    private final long timeoutMs;

    private final List<Connection> availableConnections;
    private final List<Connection> usedConnections;
    private final AtomicInteger totalConnectionsCreated;

    public ConnectionPool(String url, String username, String password,
                          int maxSize, long timeoutMs) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.maxSize = maxSize;
        this.timeoutMs = timeoutMs;
        this.availableConnections = new ArrayList<>();
        this.usedConnections = new ArrayList<>();
        this.totalConnectionsCreated = new AtomicInteger(0);

        LOGGER.info("ConnectionPool initialized: maxSize={}, timeoutMs={}", maxSize, timeoutMs);
    }

    public synchronized Connection acquireConnection() throws SQLException {
        long startTime = System.currentTimeMillis();

        while (true) {
            if (!availableConnections.isEmpty()) {
                Connection conn = availableConnections.remove(availableConnections.size() - 1);
                if (isConnectionValid(conn)) {
                    usedConnections.add(conn);
                    LOGGER.debug("Connection acquired from pool. Active: {}, Idle: {}",
                            usedConnections.size(), availableConnections.size());
                    return conn;
                } else {
                    closeConnection(conn);
                }
            }

            if (usedConnections.size() < maxSize) {
                Connection conn = createConnection();
                usedConnections.add(conn);
                LOGGER.debug("New connection created. Active: {}, Total: {}",
                        usedConnections.size(), totalConnectionsCreated.get());
                return conn;
            }

            long elapsed = System.currentTimeMillis() - startTime;
            if (elapsed >= timeoutMs) {
                throw new SQLException("Connection pool exhausted. Max size: " + maxSize +
                        ", Active: " + usedConnections.size() +
                        ", Timeout: " + timeoutMs + "ms");
            }

            try {
                wait(timeoutMs - elapsed);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new SQLException("Interrupted while waiting for connection", e);
            }
        }
    }

    public synchronized void releaseConnection(Connection conn) {
        if (conn == null) return;

        if (usedConnections.remove(conn)) {
            availableConnections.add(conn);
            LOGGER.debug("Connection released to pool. Active: {}, Idle: {}",
                    usedConnections.size(), availableConnections.size());
            notifyAll();
        }
    }

    public synchronized void closeAllConnections() {
        for (Connection conn : availableConnections) {
            closeConnection(conn);
        }
        availableConnections.clear();

        for (Connection conn : usedConnections) {
            closeConnection(conn);
        }
        usedConnections.clear();

        LOGGER.info("All connections closed");
    }

    public synchronized String getStatistics() {
        return String.format("Pool size: %d | Active: %d | Idle: %d | Total created: %d",
                maxSize, usedConnections.size(), availableConnections.size(),
                totalConnectionsCreated.get());
    }

    public int getActiveCount() {
        return usedConnections.size();
    }

    public int getIdleCount() {
        return availableConnections.size();
    }

    public int getTotalCreated() {
        return totalConnectionsCreated.get();
    }

    private Connection createConnection() throws SQLException {
        try {
            Class.forName(ApplicationConfig.getInstance().getDatabaseDriver());
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver not found", e);
        }
        Connection conn = DriverManager.getConnection(url, username, password);
        totalConnectionsCreated.incrementAndGet();
        return conn;
    }

    private boolean isConnectionValid(Connection conn) {
        try {
            return conn != null && !conn.isClosed() && conn.isValid(5);
        } catch (SQLException e) {
            return false;
        }
    }

    private void closeConnection(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            LOGGER.warn("Error closing connection", e);
        }
    }
}