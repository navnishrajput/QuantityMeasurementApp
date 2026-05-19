package com.app.quantitymeasurement.repository;

import com.app.quantitymeasurement.model.QuantityMeasurementEntity;
import com.app.quantitymeasurement.exception.DatabaseException;
import com.app.quantitymeasurement.database.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class QuantityMeasurementDatabaseRepository implements IQuantityMeasurementRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            QuantityMeasurementDatabaseRepository.class);
    private final ConnectionPool connectionPool;

    private static final String SQL_INSERT =
            "INSERT INTO quantity_measurement_entity " +
                    "(operation, measurement_type, input_value1, input_unit1, input_value2, " +
                    "input_unit2, target_unit, result_value, result_unit, has_error, error_message) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_SELECT_ALL =
            "SELECT * FROM quantity_measurement_entity ORDER BY created_at DESC";

    private static final String SQL_SELECT_BY_OPERATION =
            "SELECT * FROM quantity_measurement_entity WHERE operation = ? ORDER BY created_at DESC";

    private static final String SQL_SELECT_BY_TYPE =
            "SELECT * FROM quantity_measurement_entity WHERE measurement_type = ? ORDER BY created_at DESC";

    private static final String SQL_COUNT =
            "SELECT COUNT(*) FROM quantity_measurement_entity";

    private static final String SQL_DELETE_ALL =
            "DELETE FROM quantity_measurement_entity";

    public QuantityMeasurementDatabaseRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        initializeSchema();
        LOGGER.info("DatabaseRepository initialized");
    }

    private void initializeSchema() {
        try (Connection conn = connectionPool.acquireConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS quantity_measurement_entity (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "operation VARCHAR(50) NOT NULL, " +
                    "measurement_type VARCHAR(50) NOT NULL, " +
                    "input_value1 DOUBLE NOT NULL, " +
                    "input_unit1 VARCHAR(50) NOT NULL, " +
                    "input_value2 DOUBLE, " +
                    "input_unit2 VARCHAR(50), " +
                    "target_unit VARCHAR(50), " +
                    "result_value DOUBLE, " +
                    "result_unit VARCHAR(50), " +
                    "has_error BOOLEAN DEFAULT FALSE, " +
                    "error_message VARCHAR(500), " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            stmt.execute("CREATE INDEX IF NOT EXISTS idx_operation " +
                    "ON quantity_measurement_entity(operation)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_measurement_type " +
                    "ON quantity_measurement_entity(measurement_type)");

            LOGGER.info("Database schema initialized");
        } catch (SQLException e) {
            throw new DatabaseException("Failed to initialize database schema", e);
        }
    }

    @Override
    public void save(QuantityMeasurementEntity entity) {
        Connection conn = null;
        try {
            conn = connectionPool.acquireConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT)) {
                pstmt.setString(1, entity.getOperation());
                pstmt.setString(2, entity.getMeasurementType());
                pstmt.setDouble(3, entity.getInputValue1());
                pstmt.setString(4, entity.getInputUnit1());

                if (entity.getInputUnit2() != null) {
                    pstmt.setDouble(5, entity.getInputValue2());
                    pstmt.setString(6, entity.getInputUnit2());
                } else {
                    pstmt.setNull(5, Types.DOUBLE);
                    pstmt.setNull(6, Types.VARCHAR);
                }

                pstmt.setString(7, entity.getTargetUnit());
                pstmt.setDouble(8, entity.getResultValue());
                pstmt.setString(9, entity.getResultUnit());
                pstmt.setBoolean(10, entity.hasError());
                pstmt.setString(11, entity.getErrorMessage());

                pstmt.executeUpdate();
                conn.commit();

                LOGGER.debug("Entity saved: operation={}, type={}",
                        entity.getOperation(), entity.getMeasurementType());
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save entity: " + entity.getOperation(), e);
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
    }

    @Override
    public List<QuantityMeasurementEntity> findAll() {
        List<QuantityMeasurementEntity> entities = new ArrayList<>();
        Connection conn = null;

        try {
            conn = connectionPool.acquireConnection();

            try (PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT_ALL);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    entities.add(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve all measurements", e);
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }

        LOGGER.debug("Retrieved {} entities", entities.size());
        return entities;
    }

    @Override
    public List<QuantityMeasurementEntity> findByOperation(String operation) {
        List<QuantityMeasurementEntity> entities = new ArrayList<>();
        Connection conn = null;

        try {
            conn = connectionPool.acquireConnection();

            try (PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT_BY_OPERATION)) {
                pstmt.setString(1, operation);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        entities.add(mapResultSetToEntity(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find by operation: " + operation, e);
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }

        return entities;
    }

    @Override
    public List<QuantityMeasurementEntity> findByMeasurementType(String measurementType) {
        List<QuantityMeasurementEntity> entities = new ArrayList<>();
        Connection conn = null;

        try {
            conn = connectionPool.acquireConnection();

            try (PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT_BY_TYPE)) {
                pstmt.setString(1, measurementType);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        entities.add(mapResultSetToEntity(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find by type: " + measurementType, e);
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }

        return entities;
    }

    @Override
    public long getTotalCount() {
        Connection conn = null;

        try {
            conn = connectionPool.acquireConnection();

            try (PreparedStatement pstmt = conn.prepareStatement(SQL_COUNT);
                 ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get total count", e);
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }

        return 0;
    }

    @Override
    public void deleteAll() {
        Connection conn = null;

        try {
            conn = connectionPool.acquireConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(SQL_DELETE_ALL)) {
                int deleted = pstmt.executeUpdate();
                conn.commit();
                LOGGER.debug("Deleted {} entities", deleted);
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete all entities", e);
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
    }

    @Override
    public void clear() {
        deleteAll();
    }

    private QuantityMeasurementEntity mapResultSetToEntity(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String operation = rs.getString("operation");
        String measurementType = rs.getString("measurement_type");
        double inputValue1 = rs.getDouble("input_value1");
        String inputUnit1 = rs.getString("input_unit1");
        double inputValue2 = rs.getDouble("input_value2");
        String inputUnit2 = rs.getString("input_unit2");
        String targetUnit = rs.getString("target_unit");
        double resultValue = rs.getDouble("result_value");
        String resultUnit = rs.getString("result_unit");
        boolean hasError = rs.getBoolean("has_error");
        String errorMessage = rs.getString("error_message");
        Timestamp ts = rs.getTimestamp("created_at");
        LocalDateTime createdAt = ts != null ? ts.toLocalDateTime() : null;

        return new QuantityMeasurementEntity(id, operation, measurementType,
                inputValue1, inputUnit1, inputValue2, inputUnit2,
                targetUnit, resultValue, resultUnit, hasError, errorMessage, createdAt);
    }
}